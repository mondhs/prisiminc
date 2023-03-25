package org.mondhs.prisiminc

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mondhs.prisiminc.fetcher.*


class MainActivity : AppCompatActivity() {
//    private  var imagePaths: ArrayList<String> = ArrayList()
    private val fetchedContext = FetchAdapterSingleton.getFetchContext()
//    private lateinit var imagesRV: RecyclerView
    private lateinit var imageRVAdapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // creating a new array list and
        // initializing our recycler view.
        var imagesRV: RecyclerView = findViewById(R.id.idRVImages)
        // calling a method to
        // prepare our recycler view.
        prepareRecyclerView(imagesRV);

        // we are calling a method to request
        // the permissions to read external storage.
        requestPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.preferences_menu -> {
                val i = Intent(this, SettingsActivity::class.java)
                startActivity(i);
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }


    private fun checkPermission(): Boolean {
        // in this method we are checking if the permissions are granted or not and returning the result.
        val result =
            ContextCompat.checkSelfPermission(applicationContext, permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (checkPermission()) {
            // if the permissions are already granted we are calling
            // a method to get all images from our external storage.
            Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show()
            fetchImagePath();
        } else {
            // if the permissions are not granted we are
            // calling a method to request permissions.
            requestPermission()
        }
    }

    private fun requestPermission() {
        //on below line we are requesting the read external storage permissions.
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun prepareRecyclerView(imagesRV: RecyclerView) {

        // in this method we are preparing our recycler view.
        // on below line we are initializing our adapter class.
        imageRVAdapter = RecyclerViewAdapter(this@MainActivity, fetchedContext)

        // on below line we are creating a new grid layout manager.
        val manager = GridLayoutManager(this@MainActivity, 4)

        // on below line we are setting layout
        // manager and adapter to our recycler view.
        imagesRV.layoutManager = manager
        imagesRV.adapter = imageRVAdapter
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // this method is called after permissions has been granted.
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->                // in this case we are checking if the permissions are accepted or not.
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        // if the permissions are accepted we are displaying a toast message
                        // and calling a method to get image path.
                        Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
                        fetchImagePath();
                    } else {
                        // if permissions are denied we are closing the app and displaying the toast message.
                        Toast.makeText(
                            this,
                            "Permissions denied, Permissions are required to use the app..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    val nextCloudConfigListener =  NextCloudConfigListener();

    private fun fetchImagePath() {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        FetchAdapterSingleton.setInstance(FetchAdapterLocal(contentResolver));

        val fetchConfig = FetchConfig();
        fetchConfig.user = prefs.getString("nextcloud_user", "")!!
        fetchConfig.appPassword = prefs.getString("nextcloud_password", "")!!
        fetchConfig.serverUrl = prefs.getString("nextcloud_url", "")!!


        fetchConfig.widthPixels = Resources.getSystem().displayMetrics.widthPixels
        fetchConfig.heightPixels = Resources.getSystem().displayMetrics.heightPixels

        val fetchAdapter = FetchAdapterSingleton.getNextCloudInstance(fetchConfig)
        prefs.registerOnSharedPreferenceChangeListener(nextCloudConfigListener)


        CoroutineScope(Dispatchers.IO).launch  {
            val resources = fetchAdapter.listResources();
            fetchedContext.imagePaths.clear();
            fetchedContext.imagePaths.addAll(resources);
            CoroutineScope(Dispatchers.Main).launch{
                imageRVAdapter.notifyDataSetChanged()
            }
        }
    }


    companion object {
        // on below line we are creating variables for
        // our array list, recycler view and adapter class.
        private const val PERMISSION_REQUEST_CODE = 200
    }
}