package org.mondhs.prisiminc.fetcher

import android.content.SharedPreferences

class FetchAdapterSingleton  {
    enum class DocumentType { Drawing, Word }
    companion object{
        private var instance : IFetchAdapter? = null
        private var fetchedContext = FetchedContext()

        fun  getInstance(): IFetchAdapter {
            if (instance == null) {  // NOT thread safe!
                instance = FetchAdapterNextcloud()
            }
            return instance!!
        }

        fun  getNextCloudInstance(fetchConfig:FetchConfig): IFetchAdapter {
            if (instance == null) {  // NOT thread safe!
                val ncInstance = FetchAdapterNextcloud()
                FetchAdapterNextcloud.config.user = fetchConfig.user;
                FetchAdapterNextcloud.config.appPassword = fetchConfig.appPassword;
                FetchAdapterNextcloud.config.serverUrl = fetchConfig.serverUrl;
                FetchAdapterNextcloud.config.widthPixels = fetchConfig.widthPixels
                FetchAdapterNextcloud.config.heightPixels = fetchConfig.heightPixels
                instance = ncInstance
            }

            return instance!!
        }

        fun setInstance(fetcher : IFetchAdapter): IFetchAdapter {
            instance = fetcher
            return fetcher;
        }

        fun getFetchContext(): FetchedContext {
            return fetchedContext
        }

        fun nextImgId(): String {
            var ctx = getFetchContext();
            var nextPosition = ctx.position+1
            nextPosition = if(nextPosition >= ctx.imagePaths.size)  0 else nextPosition
            ctx.position = nextPosition
            return ctx.imagePaths[nextPosition];


        }
//        suspend fun listResources() {
//            getInstance().listResources();
//        }

    }
}

data class FetchConfig(
    var user:String = "",
    var appPassword:String = "",
    var serverUrl:String = "") {
    var heightPixels: Int = 0
    var widthPixels: Int = 0
 }