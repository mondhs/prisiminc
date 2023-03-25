package org.mondhs.prisiminc.fetcher

import android.content.SharedPreferences
import android.util.Base64
import android.util.Xml
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.xmlpull.v1.XmlPullParser
import java.util.logging.Logger


class FetchAdapterNextcloud() : IFetchAdapter {

//    private val ns: String = "oc"

    override suspend fun listResources(): List<String> {
        checkAppPassword()
        val result = getMediaList()//.map { "${config.serverUrl}/apps/photos/api/v1/preview/$it?x=64&y=64" }
        return result
    }
    override fun previewImageUri(imgId:String):String{
        return "${config.serverUrl}/apps/photos/api/v1/preview/$imgId?x=64&y=64"
    }
    override fun largeImageUri(imgId:String):String{
        return "${config.serverUrl}/apps/photos/api/v1/preview/$imgId?x=${config.widthPixels}&y=${config.heightPixels}"
    }

    override suspend fun getAuthHeader(): String {
        return "Basic $appPassword"
    }

    private fun checkAppPassword() {
        val appPasswordVal = "${config.user}:${config.appPassword}";

        // it must be android.util.Base64
        appPassword = String(
            Base64.encode(appPasswordVal.toByteArray(), Base64.NO_WRAP)
        );



    }

    private fun getAppPassword(): String { //If you want to use get method to hit server

        val appPasswordPattern = "(?<=<apppassword>)[^<]+".toRegex()

        val client = OkHttpClient()
        val theurl = "${config.serverUrl}/ocs/v2.php/core/getapppassword"
        val request = Request.Builder().url(theurl)
            .addHeader("OCS-APIRequest", "true")
            .addHeader("authorization", "Basic TBD")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body!!.string()
        val patternsFound = appPasswordPattern.findAll(body)
        return patternsFound.first().value
    }

    private suspend fun getMediaList(): List<String> { //If you want to use get method to hit server
        val client = OkHttpClient()
        val theurl = "${config.serverUrl}/remote.php/dav/photos/${config.user}/albums/Prisiminc/"
        val mediaType = "application/xml; charset=utf-8".toMediaType()

        val dataValue = "<?xml version=\"1.0\"?><d:propfind xmlns:d=\"DAV:\" xmlns:oc=\"http://owncloud.org/ns\" xmlns:nc=\"http://nextcloud.org/ns\" xmlns:ocs=\"http://open-collaboration-services.org/ns\"> <d:prop> <d:getcontentlength /> <d:getcontenttype /> <d:getetag /> <d:getlastmodified /> <d:resourcetype /> <nc:face-detections /> <nc:file-metadata-size /> <nc:has-preview /> <nc:realpath /> <oc:favorite /> <oc:fileid /> <oc:permissions /> </d:prop></d:propfind>"
        val data: RequestBody = dataValue.toRequestBody(mediaType)
        val request = Request.Builder().url(theurl)
            .method("PROPFIND",data)
            .addHeader("authorization", getAuthHeader())
            .build()
        val response = client.newCall(request).execute()


        val mediaIds = response.body?.byteStream().use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            return readIds(parser)
        }


        return mediaIds;
    }



    private fun readIds(parser: XmlPullParser): List<String> {
//        val entries = mutableListOf<Entry>()
        val ids = mutableListOf<String>()

//        parser.require(XmlPullParser.START_TAG, ns, "multistatus")
        var event = parser.eventType
        var tagName: String?
        var tagText: String? = null
        while (event != XmlPullParser.END_DOCUMENT) {
            tagName = parser.name
            when(event){
                XmlPullParser.TEXT -> tagText = parser.text
                XmlPullParser.END_TAG -> when (tagName) {
                    "oc:fileid" -> tagText?.let { ids.add(it) }
                    else -> tagText=null
                }
            }
            event = parser.next()
        }
        return ids
    }

    companion object {
        private val logger = Logger.getLogger(FetchAdapterNextcloud::class.java.name);
        private lateinit var appPassword:String;
        val config = NextCloudConfig();

    }
}

data class NextCloudConfig(
    var user:String = "",
    var appPassword:String = "",
    var serverUrl:String = "") {
    var widthPixels: Int = 0
    var heightPixels: Int = 0
}

