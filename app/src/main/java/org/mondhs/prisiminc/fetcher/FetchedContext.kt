package org.mondhs.prisiminc.fetcher

data class FetchedContext (
    val imagePaths: ArrayList<String> = ArrayList(),
    var position: Int = 0,
    var refreshMs: Long = 10000
)