package org.mondhs.prisiminc.fetcher

interface IFetchAdapter {
    suspend fun listResources(): List<String>
    suspend fun getAuthHeader(): String
    fun previewImageUri(imageId: String): String
    fun largeImageUri(imageId: String): String
}