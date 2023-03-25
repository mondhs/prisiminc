package org.mondhs.prisiminc.fetcher

import android.content.SharedPreferences

class NextCloudConfigListener : SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(sharedPreferences==null){
            return
        }
        when(key){
            "nextcloud_user"-> FetchAdapterNextcloud.config.user = sharedPreferences.getString(key, "")!!
            "nextcloud_password"-> FetchAdapterNextcloud.config.appPassword = sharedPreferences.getString(key, "")!!
            "nextcloud_url"-> FetchAdapterNextcloud.config.serverUrl = sharedPreferences.getString(key, "")!!
        }
    }

}
