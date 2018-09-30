package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorFeedVersionsResponse
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorFeedsResponse
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface TransitLandApi {

    @GET("operators?exclude_geometry=true&without_feed=false&country=${TransitLandApi.API_COUNTRY}")
    fun operators(@Query("offset") offset: Int, @Query("per_page") perPage: Int): Observable<OperatorsResponse>

    @GET("feeds?active_feed_version_update=true&exclude_geometry=true")
    fun feed(@Query("onestop_id") feedOneStopIds: String): Observable<OperatorFeedsResponse>

    @GET("feed_versions")
    fun feedVersion(@Query("sha1") feedVersionIds: String): Observable<OperatorFeedVersionsResponse>

    @GET @Streaming
    fun downloadFeed(@Url url: String): Observable<Response<ResponseBody>>

    companion object : AbstractTransitApi<TransitLandApi>(

            /**
             * API endpoint for the bus transits (http://transit.land)
             */
            apiUrl = "http://transit.land/api/v1/",

            /**
             * API class that holds the endpoint interface for the target transit
             */
            apiClass = TransitLandApi::class.java
            ) {
        /**
         * The country to retrieve bus operators (http://transit.land)
         */
        private const val API_COUNTRY = "CA"

        /**
         * The max pagination data objects (http://transit.land)
         */
        private const val API_PAGINATION_PER_PAGE = 50

        /**
         *  Retrieves all buses [Operator] of the selected country: [API_COUNTRY]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param success the callback method whenever the operators has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operators
         */
        fun retrieveOperators(success: (List<Operator>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            return retrievePaginatedObject(API_PAGINATION_PER_PAGE, retrofitInstance::operators)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

        /**
         *  Retrieves all of the [OperatorFeed] for the given [Operator]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param operator the bus operator to retrieve the all of the feeds
         *  @param success the callback method whenever the operator feeds has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feeds
         */
        fun retrieveOperatorFeed(operator: Operator, success: (List<OperatorFeed>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            return retrieveOperatorFeed(mutableListOf(operator), success, error, activity)
        }

        /**
         *  Retrieves all of the [OperatorFeed] for the given [Operator]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param operators the bus operators to retrieve the all of the feeds
         *  @param success the callback method whenever the operator feeds has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feeds
         */
        fun retrieveOperatorFeed(operators: List<Operator>, success: (List<OperatorFeed>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            val feedOneStopIds = operators.asSequence().map { it.representedInFeedOneStopIds.joinToString(",") }.joinToString(",")
            return TransitLandApi.retrofitInstance.feed(feedOneStopIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(OperatorFeedsResponse::response)
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

        /**
         *  Retrieves the [OperatorFeedVersion] for the given [OperatorFeed]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param operatorFeed the operator feed to retrieve the feed version
         *  @param success the callback method whenever the operator feed version has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feed version
         */
        fun retrieveOperatorFeedVersion(operatorFeed: OperatorFeed, success: (List<OperatorFeedVersion>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            return retrieveOperatorFeedVersion(mutableListOf(operatorFeed), success, error, activity)
        }

        /**
         *  Retrieves the [OperatorFeedVersion] for the given [OperatorFeed]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param operatorFeeds the operator feeds to retrieve the feed version
         *  @param success the callback method whenever the operator feed version has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feed version
         */
        fun retrieveOperatorFeedVersion(operatorFeeds: List<OperatorFeed>, success: (List<OperatorFeedVersion>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            val feedVersionIds = operatorFeeds.asSequence().map { it.activeFeedVersion }.joinToString(",")
            return retrofitInstance.feedVersion(feedVersionIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(OperatorFeedVersionsResponse::response)
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

        /**
         *  Downloads the feed of the [OperatorFeedVersion]
         *
         *  @param operatorFeedVersion the operator feed version to download the feed
         *  @return the observable response body of the feed
         */
        fun downloadOperatorFeed(operatorFeedVersion: OperatorFeedVersion): Observable<Response<ResponseBody>> {
            return retrofitInstance.downloadFeed(operatorFeedVersion.downloadUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}
