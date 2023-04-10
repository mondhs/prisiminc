package org.mondhs.prisiminc

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.mondhs.prisiminc.fetcher.FetchAdapterSingleton
import java.util.*
import java.util.logging.Logger
import kotlin.concurrent.schedule


class ImageDetailActivity : AppCompatActivity() {
    private var fullscreenInd: Boolean = false

    // creating a string variable, image view variable
    // and a variable for our scale gesture detector class.
//    lateinit var imgPath: String
    private lateinit var imageView: ImageView
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private val fetchedContext = FetchAdapterSingleton.getFetchContext()
    private val fetchAdapter = FetchAdapterSingleton.getInstance()

    // on below line we are defining our scale factor.
    private var mScaleFactor = 1.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        val view = this.window.decorView
        view.setBackgroundColor(Color.BLACK)
        //        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        //        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;

        setContentView(R.layout.activity_image_detail)

        // on below line getting data which we have passed from our adapter class.
        val imgId = intent.getStringExtra("imgId")




        // initializing our image view.
        imageView = findViewById<ImageView>(R.id.idIVImage)

        // on below line we are initializing our scale gesture detector for zoom in and out for our image.
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        // on below line we are getting our image file from its path.
//        val imgFile = File(imgPath)

        // if the file exists then we are loading that image in our image view.
        if (/*imgFile.exists()*/true) {

            val timer = Timer("Refresh", false)

            imgId?.let {
                renderImage(it)
                val repeatInterval = fetchedContext.refreshMs
                timer.schedule(repeatInterval, repeatInterval) {
                    renderImage(FetchAdapterSingleton.nextImgId())
                }
            }

        }
    }



    private fun renderImage(imgId: String){
        val imgUrlStr = fetchAdapter.largeImageUri(imgId)
        logger.info("Render image $imgUrlStr")
        val imgUri = Uri.parse(imgUrlStr)
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .build()

        val theContext = this


        GlobalScope.async {
//            imageView.rotation = 90f
            val authHeader = fetchAdapter.getAuthHeader();
            val request = ImageRequest.Builder(theContext)
                .data(imgUri)
                .setHeader(authHeader.first,authHeader.second )
//                .crossfade(750)
//                .transformations(BlurTransformation(theContext, radius = 18f))
                .target(imageView)
                .build()
            imageLoader.execute(request)

        }


    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        // inside on touch event method we are calling on
        // touch event method and passing our motion event to it.
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (fullscreenInd) hideSystemUI() else showSystemUI()
            fullscreenInd = !fullscreenInd

        }
        scaleGestureDetector!!.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        // on below line we are creating a class for our scale
        // listener and extending it with gesture listener.
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {

            // inside on scale method we are setting scale
            // for our image in our image view.
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))

            // on below line we are setting
            // scale x and scale y to our image view.
            imageView.scaleX = mScaleFactor
            imageView.scaleY = mScaleFactor
            return true
        }
    }

    private fun hideSystemUI() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        val mainContainer = window.decorView;
//        WindowInsetsControllerCompat(window, mainContainer).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            if (window.insetsController != null) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                window.insetsController!!.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun showSystemUI() {
        val mainContainer = window.decorView
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, mainContainer).show(WindowInsetsCompat.Type.systemBars())

    }
    companion object {
        private val logger = Logger.getLogger(ImageDetailActivity::class.java.name)
    }
}