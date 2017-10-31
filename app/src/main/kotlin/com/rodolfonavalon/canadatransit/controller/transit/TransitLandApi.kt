package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TransitLandApi {

    @GET("operators?exclude_geometry=true")
    fun operators(@Query("country") country: String, @Query("offset") offset: Int, @Query("per_page") perPage: Int): Observable<OperatorsResponse>

    @GET("feeds/{onestop_id}?exclude_geometry=true")
    fun feed(@Path("onestop_id") oneStopId: String): Observable<OperatorFeed>

    @GET("feed_versions/{active_feed_version}")
    fun feedVersion(@Path("active_feed_version") activeFeedVersion: String): Observable<OperatorFeedVersion>

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
        fun retrieveOperators(activity: Activity, success: (MutableList<Operator>) -> Unit, error: (Throwable) -> Unit) {
            val operators = mutableListOf<Operator>()
            retrievePaginatedObject(API_PAGINATION_PER_PAGE, { offset -> retrofitInstance.operators(API_COUNTRY, offset, API_PAGINATION_PER_PAGE) })
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
        fun retrieveOperatorFeed(activity: Activity, operator: Operator, success: (MutableList<OperatorFeed>) -> Unit, error: (Throwable) -> Unit) {
            val feedOneStopIds = operator.representedInFeedOneStopIds
            val operatorFeeds = mutableListOf<OperatorFeed>()
            retrieveListObject(feedOneStopIds, TransitLandApi.retrofitInstance::feed)
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
        fun retrieveOperatorFeedVersion(activity: Activity, operatorFeed: OperatorFeed, success: (OperatorFeedVersion) -> Unit, error: (Throwable) -> Unit) {
            retrofitInstance.feedVersion(operatorFeed.activeFeedVersion)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
                    .attachCompositeDisposable(activity)
        }

    }
}
