package org.mondhs.prisiminc.fetcher

import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URL
import java.util.LinkedList
import java.util.logging.Logger


class FetchAdapterImmich() : IFetchAdapter {

    override suspend fun listResources(): List<String> {
        val theurl = "${config.serverUrl}/api/album/937b484f-a373-4b38-b101-a027e8d217a5"
        logger.info("Hiting url: ${theurl}")
        val mediaType = "application/json; charset=utf-8".toMediaType()



        val request = Request.Builder().url(theurl)
            .get()
            .build()
        val response = client
            .newCall(request)
            .execute()
        val resourceList = LinkedList<String>()
        logger.info("[listResources]response: ${response.code} ${response.message}")
        if(response.code == 200) {logger.info("response: ${response.code} ${response.message}")
            val jsonData: String = response.body?.string() ?: "{}"
            logger.info("[listResources]jsonData: $jsonData")
            val jsonObject = JSONObject(jsonData)
            val assets = jsonObject.getJSONArray("assets")
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val id = asset.get("id")
//                logger.info("[listResources]asset: $asset")
                resourceList.add(id.toString())
            }
        }
        return resourceList
    }

    override suspend fun getAuthHeader(): Pair<String, String> {

        val theurl = "${config.serverUrl}/api/auth/login"
        var cookies = client.cookieJar.loadForRequest(theurl.toHttpUrl())
        if(cookies.isNotEmpty()){
            val authValue = toAuthHeaderValue(cookies)
            return Pair("Cookie",authValue)
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        logger.info("[getAuthHeader]Hiting url: ${theurl}")

        val dataValue = "{\"email\": \"${config.user}\",\"password\": \"${config.appPassword}\"}"
//        logger.info("[getAuthHeader]request: ${dataValue}")
        val data: RequestBody = dataValue.toRequestBody(mediaType)
        val request = Request.Builder().url(theurl)
            .method("POST",data)
            .build()
        val response = client
            .newCall(request)
            .execute()

        logger.info("[getAuthHeader]response: ${response.code} ${response.message}")
//        logger.info("[getAuthHeader]headers: ${response.headers("Set-Cookie")}")

        return Pair("Cookie",toAuthHeaderValue(cookies))
    }

    private fun toAuthHeaderValue(cookies: List<Cookie>): String {
        val result = LinkedList<String>()
        for (c in cookies.indices) {
//            logger.info("[getAuthHeader]cookies before: ${cookies[c].name} ${cookies[c].value}")
            result.add ("${cookies[c].name}=${cookies[c].value}")
        }
        return result.joinToString("; ")
    }

    override fun previewImageUri(imageId: String): String {
        return "${config.serverUrl}/api/asset/thumbnail/$imageId"
    }

    override fun largeImageUri(imageId: String): String {
        return "${config.serverUrl}/api/asset/thumbnail/$imageId"
//        return "${config.serverUrl}/api/asset/download/$imageId"
    }

    companion object {
        private val logger = Logger.getLogger(FetchAdapterImmich::class.java.name)
//        private lateinit var appPassword:String
        private val fetcherCookies = FetcherCookies()
        private val client = OkHttpClient()
        .newBuilder()
        .cookieJar(cookieJar = fetcherCookies)
        .build();
        val config = FetchConfig()

    }
}