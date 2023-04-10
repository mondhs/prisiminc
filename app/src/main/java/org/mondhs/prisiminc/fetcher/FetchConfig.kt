package org.mondhs.prisiminc.fetcher

data class FetchConfig(
    var user:String = "",
    var appPassword:String = "",
    var serverUrl:String = "") {
    var widthPixels: Int = 0
    var heightPixels: Int = 0
}

