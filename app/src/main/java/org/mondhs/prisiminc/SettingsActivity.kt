package org.mondhs.prisiminc


import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity: AppCompatActivity() {//PreferenceFragmentCompat
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource(R.xml.main_preferences, rootKey)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_activity);
        supportActionBar?.title = "Settings";
        val frameLayout = findViewById<FrameLayout>(R.id.idFrameLayout);
        if ( frameLayout != null) {
            if(savedInstanceState!=null){
                return
            }
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()
            ft.replace(R.id.idFrameLayout, SettingsFragment())
            ft.commit()
        }
    }
}