package com.rodolfonavalon.canadatransit.controller.transit

import com.google.gson.GsonBuilder
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.converter.gson.DateTimeConverter
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorsResponse

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TransitLandApi {

    @GET("operators?exclude_geometry=true")
    fun operators(@Query("country") country: String, @Query("offset") offset: Int, @Query("per_page") perPage: Int): Observable<OperatorsResponse>

    @GET("feeds/{onestop_id}?exclude_geometry=true")
    fun feed(@Path("onestop_id") oneStopId: String): Observable<OperatorFeed>

    @GET("feed_versions/{active_feed_version}")
    fun feedVersion(@Path("active_feed_version") activeFeedVersion: String): Observable<OperatorFeedVersion>

    companion object {
        /**
         * API endpoint for the bus transits (http://transit.land)
         */
        const val TRANSIT_LAND_API = "http://transit.land/api/v1/"

        /**
         * The country to retrieve bus operators (http://transit.land)
         */
        const val API_COUNTRY = "CA"

        /**
         * The max pagination data objects (http://transit.land)
         */
        const val API_PAGINATION_PER_PAGE = 50

        /**
         *  Retrieves the {@link CompositeDisposable} singleton instance
         */
        val retrofitInstance: TransitLandApi by lazy {
            val gson = GsonBuilder()
                    .registerTypeAdapter(DateTime::class.java, DateTimeConverter())
                    .setPrettyPrinting()
                    .create()
            val retrofit = Retrofit.Builder()
                    .baseUrl(TRANSIT_LAND_API)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            retrofit.create(TransitLandApi::class.java)
        }

        /**
         *  Retrieves the {@link TransitLandApi} instance
         */
        val disposableInstance: CompositeDisposable = CompositeDisposable()
    }
}
