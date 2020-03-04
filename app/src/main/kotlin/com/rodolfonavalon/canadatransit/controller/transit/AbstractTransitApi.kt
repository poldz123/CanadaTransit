package com.rodolfonavalon.canadatransit.controller.transit

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import com.rodolfonavalon.canadatransit.controller.database.converter.moshi.adapter.DateTimeAdapter
import com.rodolfonavalon.canadatransit.controller.database.converter.moshi.adapter.FeedCurrentFeedVersionAdapter
import com.rodolfonavalon.canadatransit.controller.database.converter.moshi.adapter.FeedForeignKeyAdapter
import com.rodolfonavalon.canadatransit.controller.manager.LifecycleManager
import com.rodolfonavalon.canadatransit.model.transit.response.MetaResponse
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

abstract class AbstractTransitApi<API : Any>(apiUrl: String, val apiClass: Class<API>) {

    /**
     * The max pagination per page of objects (http://transit.land)
     */
    private val defaultApiPaginationPerPage = 50

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
                .add(FeedForeignKeyAdapter())
                .add(FeedCurrentFeedVersionAdapter())
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
     *  Retrieves the [observable] using a paginated strategy, this is used to retrieve [observable]
     *  from the API through chaining the observer using its [MetaResponse].
     *
     *  @param OBSERVER the type of the object to be retrieve
     *  @param observable the lambda that execute the observable, Parameter: [0] -> Offset
     */
    protected fun <MODEL : Any, OBSERVER : MetaResponse<MODEL>> retrievePaginatedObject(
        observable: (Int) -> Observable<OBSERVER>
    ): Single<List<MODEL>> {
        return Single.create { observer ->
            val result = mutableListOf<MODEL>()
            // Recursion makes everything easier to do the paginated observable objects
            fun retrievePaginatedObject(offset: Int): Observable<OBSERVER> {
                return observable.invoke(offset).concatMap { metaResponse ->
                    val meta = metaResponse.meta
                    if (meta.hasNext()) {
                        // Lets do another api call to retrieve operators
                        Observable.just(metaResponse).concatWith(retrievePaginatedObject(meta.offset + defaultApiPaginationPerPage))
                    } else {
                        Observable.just(metaResponse)
                    }
                }
            }
            // Lets do a recursion when retrieving the paginated objects
            val disposable = retrievePaginatedObject(0)
                    .map { it.response }
                    .subscribe({ result.addAll(it) }, { observer.onError(it) }, { observer.onSuccess(result) })
            // Make sure that we set the disposable to this single observable
            observer.setDisposable(disposable)
        }
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
