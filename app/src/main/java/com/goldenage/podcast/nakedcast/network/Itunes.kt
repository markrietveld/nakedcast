package com.goldenage.podcast.nakedcast.network

import com.goldenage.podcast.nakedcast.model.ItunesResponse
import retrofit.RestAdapter
import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

object Itunes {
    val client = buildClient()
    
    private fun buildClient(): ItunesClient {
        val builder = RestAdapter.Builder()
        builder.setEndpoint("https://itunes.apple.com")
        builder.setLogLevel(RestAdapter.LogLevel.FULL)
        return builder.build().create<ItunesClient>(javaClass<ItunesClient>())
    }
    public interface ItunesClient {

        GET("/search?media=podcast")
        public fun searchPodcast(Query("term") searchTerm: String): Observable<ItunesResponse>
    }
}
