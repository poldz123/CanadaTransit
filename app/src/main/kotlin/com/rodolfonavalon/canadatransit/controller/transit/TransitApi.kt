package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import android.support.annotation.VisibleForTesting
import android.support.annotation.VisibleForTesting.PRIVATE
import com.google.gson.GsonBuilder
import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager
import com.rodolfonavalon.canadatransit.model.database.converter.gson.DateTimeConverter
import com.rodolfonavalon.canadatransit.model.transit.response.MetaResponse
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

abstract class TransitApi<API> {
    /**
     * API endpoint of the target transit
     */
    abstract protected val apiUrl: String

    /**
     * API class that holds the endpoint interface for the target transit
     */
    abstract protected val apiClass: Class<API>

    /**
     * API test endpoint of the target transit
     */
    @VisibleForTesting(otherwise = PRIVATE)
    var apiTestUrl: String? = null

    /**
     *  Retrieves the [Retrofit] instance, that handles the api networking and
     *  properly converts the json as an object model.
     */
    protected val retrofitInstance: API by lazy {
        val baseUrl = apiTestUrl ?: apiUrl
        val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, DateTimeConverter())
                .setPrettyPrinting()
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        retrofit.create(apiClass)
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
        if (feeds.isEmpty()) {
            throw IllegalArgumentException("Feeds list is empty.")
        }
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
    protected fun <OBSERVER: MetaResponse> retrievePaginatedObject(perPage: Int, observer: (Int, Int) -> Observable<OBSERVER>): Observable<OBSERVER> {
        if (perPage <= 0) {
            throw IllegalArgumentException("Per page is not valid: $perPage")
        }
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