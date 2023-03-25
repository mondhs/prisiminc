package org.mondhs.prisiminc.fetcher

import android.content.ContentResolver
import android.database.Cursor
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlin.math.log

class FetchAdapterLocal(contentResolver:ContentResolver) : IFetchAdapter {

    private val contentResolver:ContentResolver = contentResolver;

    override suspend fun listResources(): List<String> {
        return fetchImagePath();
    }

    override fun previewImageUri(imageId: String): String {
        return imageId
    }

    override fun largeImageUri(imageId: String): String {
        return imageId
    }

    override suspend fun getAuthHeader(): String {
        return "";
    }

    // on below line we are moving our cursor position

    // on below line we are getting image file path

    // after that we are getting the image file path
    // and adding that path in our array list.
    // after adding the data to our
    // array list we are closing our cursor.
    // if the sd card is present we are creating a new list in
    // which we are getting our images data with their ids.

    // on below line we are creating a new
    // string to order our images by string.

    // this method will stores all the images
    // from the gallery in Cursor

    // below line is to get total number of images

    // on below line we are running a loop to add
    // the image file path in our array list.
    // in this method we are adding all our image paths
    // in our arraylist which we have created.
    // on below line we are checking if the device is having an sd card or not.
    private fun fetchImagePath(): List<String> {
        // in this method we are adding all our image paths
        // in our arraylist which we have created.
        // on below line we are checking if the device is having an sd card or not.
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if (isSDPresent) {

            // if the sd card is present we are creating a new list in
            // which we are getting our images data with their ids.
            val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)

            // on below line we are creating a new
            // string to order our images by string.
            val orderBy = MediaStore.Images.Media._ID

            // this method will stores all the images
            // from the gallery in Cursor
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                orderBy
            )


            val mediaList = generateSequence { if (cursor!!.moveToNext()) cursor else null }
                .map { getStringFromCursor(it) }
                .toList()

//            imagePaths.addAll(mediaList);
//
//
//            imageRVAdapter.notifyDataSetChanged()
            // after adding the data to our
            // array list we are closing our cursor.
            cursor!!.close()
            return mediaList;
        }
        return listOf<String>();
    }

    private fun getStringFromCursor(cur: Cursor): String {
        val dataColumnIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA)
        return "file://"+cur.getString(dataColumnIndex)
    }

    companion object {
        private const val TAG: String = "FetchAdapterNextcloud"
    }


}