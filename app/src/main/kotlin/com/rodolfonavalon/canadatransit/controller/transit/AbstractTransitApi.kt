package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager
import com.rodolfonavalon.canadatransit.model.database.converter.moshi.adapter.DateTimeAdapter
import com.rodolfonavalon.canadatransit.model.database.converter.moshi.adapter.OperatorFeedForeignKeyAdapter
import com.rodolfonavalon.canadatransit.model.transit.response.MetaResponse
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

abstract class AbstractTransitApi<API : Any>(apiUrl: String, val apiClass: Class<API>) {

    /**
     *  Retrieves the [Retrofit] instance, that handles the api networking and
     *  properly converts the json as an object model.
     */
    lateinit var retrofitInstance: API

    init {
        initializeRetrofit(apiUrl)
    }

    /**
     * Initialized the [Retrofit] api instance with a valid url
     * that points to the API. This is used also in test to re-initialized
     * the API url every test cases.
     */
    @VisibleForTesting(otherwise = PRIVATE)
    fun initializeRetrofit(apiUrl: String) {
        val moshi = Moshi.Builder()
                .add(DateTimeAdapter())
                .add(OperatorFeedForeignKeyAdapter())
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        retrofitInstance = retrofit.create(apiClass)
    }

    /**
     *  Retrieves the [CompositeDisposable] instance, that handles the removal of the
     *  retrofit during activity's life cycle.
     */
    private val disposableInstance: CompositeDisposable = CompositeDisposable()

    /**
     *  Retrieves the [observer] from the list with chaining [Observable], this is used to
     *  retrieve [observer] from the API using the [feeds].
     *
     *  @param FEED the type of the list of object from the feed
     *  @param OBSERVER the type of the object to be retrieve
     *  @throws [IllegalArgumentException] if the feeds is empty
     */
    protected fun <FEED, OBSERVER> retrieveListObject(feeds: List<FEED>, observer: (FEED) -> Observable<OBSERVER>): Observable<OBSERVER> {
        // Return the result with chaining of the observable list feed
        return observer.invoke(feeds.first())
                .concatMap { operatorFeed ->
                    var observable = Observable.just(operatorFeed)
                    // Starts at index 1 since we already started at the index 0
                    for (i in 1 until feeds.size) {
                        observable = observable.concatWith(observer.invoke(feeds[i]))
                    }
                    observable
                }
    }

    /**
     *  Retrieves the [observer] using a paginated strategy, this is used to retrieve [observer]
     *  from the API through chaining the observer using its [MetaResponse].
     *
     *  @param OBSERVER the type of the object to be retrieve
     *  @param observer the lambda that execute the observer, Parameter: [0] -> Offset [1] -> Per page
     *  @throws [IllegalArgumentException] if per page is an invalid value
     */
    protected fun <OBSERVER : MetaResponse> retrievePaginatedObject(perPage: Int, observer: (Int, Int) -> Observable<OBSERVER>): Observable<OBSERVER> {
        // Recursion makes everything easier to do the paginated observable objects
        fun retrievePaginatedObject(offset: Int): Observable<OBSERVER> {
            return observer.invoke(offset, perPage).concatMap { metaResponse ->
                val meta = metaResponse.meta
                if (meta.hasNext()) {
                    // Lets do another api call to retrieve operators
                    Observable.just(metaResponse).concatWith(retrievePaginatedObject(meta.offset + perPage))
                } else {
                    Observable.just(metaResponse)
                }
            }
        }
        // Return the result while we start at the first offset
        return retrievePaginatedObject(0)
    }

    /**
     * Attaches the disposable within the composite to safely dispose the retrofit observers
     * whenever the activity is destroyed.
     *
     * This is to prevent executing subscribe callbacks after the activity has been destroyed
     * since retrofit is subject to execute within the background thread.
     *
     * @param activity The activity where the retrofit is executed
     */
    fun Disposable.attachCompositeDisposable(activity: Activity?): Disposable {
        if (activity == null) {
            // Do not attach disposable for null activity
            return this
        }
        // Attaches the disposable retrofit to the pool
        disposableInstance.add(this)
        // Detaches the disposable from the composite whenever the activity is destroyed
        LifecycleManager.watchActivity(activity) { stage ->
            val isDestroyCallback = (stage === LifecycleManager.LifecycleStage.DESTROYED || isDisposed)
            if (isDestroyCallback) {
                disposableInstance.remove(this)
            }
            isDestroyCallback
        }
        return this
    }
}
