package org.mondhs.prisiminc

//import android.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.mondhs.prisiminc.fetcher.FetchAdapterSingleton
import org.mondhs.prisiminc.fetcher.FetchedContext

//import com.squareup.picasso.Picasso

class RecyclerViewAdapter     // on below line we have created a constructor.
    (// creating a variable for our context and array list.
    private val context: Context, private val fetchedContext: FetchedContext, /*var fullScreenOnListener: (()->Unit)? = null*/
) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout in this method which we have created.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return RecyclerViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        // on below line we are getting the file from the
        // path which we have stored in our list.
        val fetchAdapter = FetchAdapterSingleton.getInstance()
        val imgUrlStr = fetchAdapter.previewImageUri(fetchedContext.imagePaths[position])
//        val imgFile = File(imgUrlStr)

        val imgUri = Uri.parse(imgUrlStr)


        // on below line we are checking if the file exists or not.
        if (/*imgFile.exists()*/ true) {




            val imageLoader = ImageLoader.Builder(context)
                .crossfade(true)
                .build()


            GlobalScope.async {
                val authHeader = fetchAdapter.getAuthHeader();
                val request = ImageRequest.Builder(context)
                    .data(imgUri)
                    .setHeader(authHeader.first,authHeader.second )
                    .target(holder.imageIV)
                    .build()
                imageLoader.execute(request)
            }


            // if the file exists then we are displaying that file in our image view using picasso library.
//            holder.imageIV.load(imgUri,){
//                placeholder(R.drawable.ic_launcher_background)
//
//            }

            // on below line we are adding click listener to our item of recycler view.
            holder.itemView.setOnClickListener { // inside on click listener we are creating a new intent
                val i = Intent(context, ImageDetailActivity::class.java)
                fetchedContext.position = position
                // on below line we are passing the image path to our new activity.
                i.putExtra("imgId", fetchedContext.imagePaths[position])

//                fullScreenOnListener?.invoke()

                // at last we are starting our activity.
                context.startActivity(i)


            }
        }
    }





    override fun getItemCount(): Int {
        // this method returns
        // the size of recyclerview
        return fetchedContext.imagePaths.size
    }

    // View Holder Class to handle Recycler View.
    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our views.
        val imageIV: ImageView

        init {
            // initializing our views with their ids.
            imageIV = itemView.findViewById(R.id.idIVImage)
        }
    }
}