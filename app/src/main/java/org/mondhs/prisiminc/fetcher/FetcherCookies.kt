package org.mondhs.prisiminc.fetcher

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.Collections
import java.util.logging.Logger

class FetcherCookies: CookieJar {
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        logger.info("[loadForRequest]cookieJar: ${cookieJar} ")
        return cookieJar
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        logger.info("[saveFromResponse]cookieJar: ${cookies} ")
        cookieJar.addAll(cookies);
    }
    companion object {
        private val logger = Logger.getLogger(FetcherCookies::class.java.name)
        var cookieJar = ArrayList<Cookie>()
    }

}
