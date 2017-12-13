package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface TransitLandApi {

    @GET("operators?exclude_geometry=true&country=${TransitLandApi.API_COUNTRY}")
    fun operators(@Query("offset") offset: Int, @Query("per_page") perPage: Int): Observable<OperatorsResponse>

    @GET("feeds/{onestop_id}?exclude_geometry=true")
    fun feed(@Path("onestop_id") oneStopId: String): Observable<OperatorFeed>

    @GET("feed_versions/{active_feed_version}")
    fun feedVersion(@Path("active_feed_version") activeFeedVersion: String): Observable<OperatorFeedVersion>

    @GET @Streaming
    fun downloadFeed(@Url url: String): Observable<Response<ResponseBody>>

    companion object: TransitApi<TransitLandApi>() {
        /**
         * The country to retrieve bus operators (http://transit.land)
         */
        private const val API_COUNTRY = "CA"

        /**
         * The max pagination data objects (http://transit.land)
         */
        private const val API_PAGINATION_PER_PAGE = 50

        /**
         * API endpoint for the bus transits (http://transit.land)
         */
        override val apiUrl: String = "http://transit.land/api/v1/"

        /**
         * API class that holds the endpoint interface for the target transit
         */
        override val apiClass: Class<TransitLandApi> = TransitLandApi::class.java

        /**
         *  Retrieves all buses [Operator] of the selected country: [API_COUNTRY]
         *
         *  @param activity the activity to attached the life cycle for the disposable
         *  @param success the callback method whenever the operators has successfully retrieved
         *  @param error the callback method when something went wrong during retrieval of the operators
         */
        fun retrieveOperators(success: (List<Operator>) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            val operators = mutableListOf<Operator>()
            return retrievePaginatedObject(API_PAGINATION_PER_PAGE, retrofitInstance::operators)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(OperatorsResponse::operators)
                    .subscribeBy(
                            onNext = { apiOperators -> operators.addAll(apiOperators) },
                            onError = error,
                            onComplete = { success.invoke(operators) }
                    )
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
            val feedOneStopIds = operator.representedInFeedOneStopIds
            val operatorFeeds = mutableListOf<OperatorFeed>()
            return retrieveListObject(feedOneStopIds, TransitLandApi.retrofitInstance::feed)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = { apiOperatorFeeds -> operatorFeeds.add(apiOperatorFeeds) },
                            onError = error,
                            onComplete = { success.invoke(operatorFeeds) }
                    )
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
        fun retrieveOperatorFeedVersion(operatorFeed: OperatorFeed, success: (OperatorFeedVersion) -> Unit, error: (Throwable) -> Unit, activity: Activity? = null): Disposable {
            return retrofitInstance.feedVersion(operatorFeed.activeFeedVersion)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
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
