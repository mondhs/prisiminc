package org.mondhs.prisiminc.fetcher

import android.content.SharedPreferences
import android.content.res.Resources

class FetchAdapterSingleton  {
    enum class DocumentType { Drawing, Word }
    companion object{
        private var instance : IFetchAdapter? = null
        private var fetchedContext = FetchedContext()

        fun  getInstance(): IFetchAdapter {
//            if (instance == null) {  // NOT thread safe!
//                instance = FetchAdapterNextcloud()
//            }
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


//        fun setInstance(fetcher : IFetchAdapter): IFetchAdapter {
//            instance = fetcher
//            return fetcher;
//        }

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


        fun createNextCloudInstance(prefs: SharedPreferences): IFetchAdapter? {
            val fetchConfig = FetchConfig()

            val nextcloudStorageInd = prefs.getBoolean("nextcloud_storage_ind", false)!!
            if(!nextcloudStorageInd){
                return null
            }
            fetchConfig.user = prefs.getString("nextcloud_user", "")!!
            fetchConfig.appPassword = prefs.getString("nextcloud_password", "")!!
            fetchConfig.serverUrl = prefs.getString("nextcloud_url", "")!!


            fetchConfig.widthPixels = Resources.getSystem().displayMetrics.widthPixels
            fetchConfig.heightPixels = Resources.getSystem().displayMetrics.heightPixels
            configCommons(fetchedContext, prefs)
//            fetchedContext.refreshMs = 1000//prefs.getLong("fetch_context_refresh_ms", 10000)

            val fetchAdapter = getNextCloudInstance(fetchConfig)
            return fetchAdapter
        }

        private fun configCommons(fetchedContext: FetchedContext, prefs: SharedPreferences) {
            val refreshMsStr = prefs.getString("fetch_context_refresh_ms", "10000")
            Companion.fetchedContext.refreshMs = refreshMsStr?.toLongOrNull() ?: 10000
        }

        fun createImmichInstance(prefs: SharedPreferences): IFetchAdapter? {
//            val fetchConfig = FetchConfig()

            val storageInd = prefs.getBoolean("immich_storage_ind", false)!!
            if(!storageInd){
                return null
            }

            configCommons(fetchedContext, prefs)

            val ncInstance = FetchAdapterImmich()
            FetchAdapterImmich.config.user = prefs.getString("immich_user", "")!!;
            FetchAdapterImmich.config.appPassword = prefs.getString("immich_password", "")!!;
            FetchAdapterImmich.config.serverUrl = prefs.getString("immich_url", "")!!;
            FetchAdapterImmich.config.widthPixels = Resources.getSystem().displayMetrics.widthPixels
            FetchAdapterImmich.config.heightPixels = Resources.getSystem().displayMetrics.heightPixels
            instance = ncInstance

//            val fetchAdapter = getImmichInstance(fetchConfig)
            return ncInstance
        }



//        suspend fun listResources() {
//            getInstance().listResources();
//        }

    }
}

