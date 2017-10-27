package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import com.google.gson.GsonBuilder
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.database.converter.gson.DateConverter
import com.rodolfonavalon.canadatransit.model.transit.response.OperatorsResponse

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface TransitLandApi {

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
        private const val TRANSIT_LAND_API = "http://transit.land/api/v1/"

        /**
         * The country to retrieve bus operators (http://transit.land)
         */
        private const val API_COUNTRY = "CA"

        /**
         * The max pagination data objects (http://transit.land)
         */
        private const val API_PAGINATION_PER_PAGE = 50

        /**
         *  Retrieves the {@link CompositeDisposable} singleton instance
         */
        private val retrofitInstance: TransitLandApi by lazy {
            val gson = GsonBuilder()
                    .registerTypeAdapter(Date::class.java, DateConverter())
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
        private val disposableInstance: CompositeDisposable = CompositeDisposable()

        /**
         *  Retrieves all buses {@link Operator} of the selected country: {@link #API_COUNTRY}
         *
         *  @param success
         *              The callback method whenever the operators has successfully retrieved
         *  @param error
         *              The callback method when something went wrong during retrieval of the operators
         */
        fun retrieveOperators(activity: Activity, success: (MutableList<Operator>) -> Unit, error: (Throwable) -> Unit) {
            val operators = mutableListOf<Operator>()
            val disposable = retrieveOperators(0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(OperatorsResponse::operators)
                    .subscribeBy(
                            onNext = { apiOperators -> operators.addAll(apiOperators) },
                            onError = error,
                            onComplete = { success.invoke(operators) }
                    )
            attachRetrofitDisposable(activity, disposable)
        }

        private fun retrieveOperators(offset: Int): Observable<OperatorsResponse> {
            return TransitLandApi.retrofitInstance
                    .operators(API_COUNTRY, offset, API_PAGINATION_PER_PAGE)
                    .concatMap { operatorsResponse ->
                        val meta = operatorsResponse.meta
                        if (meta.hasNext()) {
                            // Lets do another api call to retrieve operators
                            Observable.just(operatorsResponse)
                                    .concatWith(retrieveOperators(meta.offset + API_PAGINATION_PER_PAGE))
                        } else {
                            Observable.just(operatorsResponse)
                        }
                    }
        }

        /**
         *  Retrieves all of the {@link OperatorFeed} for the given {@link Operator}
         *
         *  @param operator
         *              The bus operator to retrieve the all of the feeds
         *  @param success
         *              The callback method whenever the operator feeds has successfully retrieved
         *  @param error
         *              The callback method when something went wrong during retrieval of the operator feeds
         */
        fun retrieveOperatorFeed(activity: Activity, operator: Operator, success: (MutableList<OperatorFeed>) -> Unit, error: (Throwable) -> Unit) {
            val feedOneStopIds = operator.representedInFeedOneStopIds
            val operatorFeeds = mutableListOf<OperatorFeed>()
            val disposable = TransitLandApi.retrofitInstance
                    .feed(feedOneStopIds[0])
                    .concatMap { operatorFeed ->
                        var observable = Observable.just(operatorFeed)
                        for (i in 1 until feedOneStopIds.size) {
                            observable = observable.concatWith(TransitLandApi.retrofitInstance
                                    .feed(feedOneStopIds[i]))
                        }
                        observable
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = { apiOperatorFeeds -> operatorFeeds.add(apiOperatorFeeds) },
                            onError = error,
                            onComplete = { success.invoke(operatorFeeds) }
                    )
            attachRetrofitDisposable(activity, disposable)
        }

        /**
         *  Retrieves the {@link OperatorFeedVersion} for the given {@link OperatorFeed}
         *
         *  @param operatorFeed
         *              The operator feed to retrieve the feed version
         *  @param success
         *              The callback method whenever the operator feed version has successfully retrieved
         *  @param error
         *              The callback method when something went wrong during retrieval of the operator feed version
         */
        fun retrieveOperatorFeedVersion(activity: Activity, operatorFeed: OperatorFeed, success: (OperatorFeedVersion) -> Unit, error: (Throwable) -> Unit) {
            val disposable = TransitLandApi.retrofitInstance
                    .feedVersion(operatorFeed.activeFeedVersion)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success, error)
            attachRetrofitDisposable(activity, disposable)
        }

        /**
         * Attaches the disposable within the composite to safely dispose the retrofit observers
         * whenever the activity is destroyed.
         *
         * This is to prevent executing subscribe callbacks after the activity has been destroyed
         * since retrofit is subject to execute within the background thread.
         *
         * @param activity
         * The activity where the retrofit is executed
         * @param disposable
         * The disposable of the retrofit observers
         */
        private fun attachRetrofitDisposable(activity: Activity, disposable: Disposable) {
            // Attaches the disposable retrofit to the pool
            TransitLandApi.disposableInstance.add(disposable)
            // Detaches the disposable from the composite whenever the activity is destroyed
            LifecycleManager.watchActivity(activity) { stage ->
                val isDestroyCallback = (stage === LifecycleManager.LifecycleStage.DESTROYED || disposable.isDisposed)
                if (isDestroyCallback) {
                    disposableInstance.remove(disposable)
                }
                isDestroyCallback
            }
        }
    }
}
