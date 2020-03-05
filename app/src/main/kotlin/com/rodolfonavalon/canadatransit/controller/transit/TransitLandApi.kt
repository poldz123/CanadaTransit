package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.transit.response.FeedVersionsResponse
import com.rodolfonavalon.canadatransit.model.transit.response.FeedsResponse
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

// TODO (Increase the timeout of each of the API call)
interface TransitLandApi {

    @GET("operators?exclude_geometry=true&without_feed=false&country=${API_COUNTRY}")
    fun operators(@Query("offset") offset: Int): Observable<OperatorsResponse>

    @GET("feeds?exclude_geometry=true")
    fun feed(@Query("onestop_id") feedOneStopIds: String, @Query("offset") offset: Int): Observable<FeedsResponse>

    @GET("feed_versions?exclude_geometry=true")
    fun feedVersion(@Query("sha1") feedVersionIds: String, @Query("offset") offset: Int): Observable<FeedVersionsResponse>

    @GET @Streaming
    fun downloadFeed(@Url url: String): Observable<Response<ResponseBody>>

    companion object : AbstractTransitApi<TransitLandApi>(

            /**
             * API endpoint for the bus transits (http://transit.land)
             */
            apiUrl = "https://transit.land/api/v1/",

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
         *  Retrieves all buses [Operator] of the selected country: [API_COUNTRY]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param success the callback method whenever the operators has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operators
         */
        fun retrieveOperators(success: (List<Operator>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            // TODO(return observable instead??)
            return retrievePaginatedObject(retrofitInstance::operators)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

        /**
         *  Retrieves all of the [Feed] for the given [Operator]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param operator the bus operator to retrieve the all of the feeds
         *  @param success the callback method whenever the operator feeds has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feeds
         */
        fun retrieveFeeds(
            operator: Operator,
            success: (List<Feed>) -> Unit,
            error: (Throwable) -> Unit,
            activity: Activity? = null
        ): Disposable {
            return retrieveFeeds(mutableListOf(operator), success, error, activity)
        }

        /**
         *  Retrieves all of the [Feed] for the given [Operator]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param operators the bus operators to retrieve the all of the feeds
         *  @param success the callback method whenever the operator feeds has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feeds
         */
        fun retrieveFeeds(
            operators: List<Operator>,
            success: (List<Feed>) -> Unit,
            error: (Throwable) -> Unit,
            activity: Activity? = null
        ): Disposable {
            val feedOneStopIds = operators.asSequence().map { it.representedInFeedOneStopIds.joinToString(",") }.joinToString(",")
            return retrievePaginatedObject { retrofitInstance.feed(feedOneStopIds, it) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

        /**
         *  Retrieves the [FeedVersion] for the given [Feed]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param feed the operator feed to retrieve the feed version
         *  @param success the callback method whenever the operator feed version has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feed version
         */
        fun retrieveFeedVersion(
            feed: Feed,
            success: (List<FeedVersion>) -> Unit,
            error: (Throwable) -> Unit,
            activity: Activity? = null
        ): Disposable {
            return retrieveFeedVersion(mutableListOf(feed), success, error, activity)
        }

        /**
         *  Retrieves the [FeedVersion] for the given [Feed]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param feeds the operator feeds to retrieve the feed version
         *  @param success the callback method whenever the operator feed version has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operator feed version
         */
        fun retrieveFeedVersion(
            feeds: List<Feed>,
            success: (List<FeedVersion>) -> Unit,
            error: (Throwable) -> Unit,
            activity: Activity? = null
        ): Disposable {
            val feedVersionIds = feeds.asSequence().map { it.activeFeedVersion ?: it.currentFeedVersion }.joinToString(",")
            return retrievePaginatedObject { retrofitInstance.feedVersion(feedVersionIds, it) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

        /**
         *  Downloads the feed of the [FeedVersion]
         *
         *  @param feedVersion the operator feed version to download the feed
         *  @return the observable response body of the feed
         */
        fun downloadFeedVersion(feedVersion: FeedVersion): Observable<Response<ResponseBody>> {
            return retrofitInstance.downloadFeed(feedVersion.downloadUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}
