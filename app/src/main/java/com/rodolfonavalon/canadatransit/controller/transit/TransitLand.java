package com.rodolfonavalon.canadatransit.controller.transit;

import android.app.Activity;

import com.rodolfonavalon.canadatransit.controller.util.LifecycleManager;
import com.rodolfonavalon.canadatransit.model.database.Operator;
import com.rodolfonavalon.canadatransit.model.database.OperatorFeed;
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion;
import com.rodolfonavalon.canadatransit.model.transit.Meta;
import com.rodolfonavalon.canadatransit.model.transit.Response.OperatorsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransitLand {

    /**
     *  API endpoint for the bus transits (http://transit.land)
     */
    private static final String TRANSIT_LAND_API = "http://transit.land/api/v1/";

    /**
     *  The country to retrieve bus operators (http://transit.land)
     */
    private static final String API_COUNTRY = "CA";

    /**
     *  The max pagination data objects (http://transit.land)
     */
    private static final int API_PAGINATION_PER_PAGE = 50;

    /**
     *  Retrieves all buses {@link Operator} of the selected country: {@link #API_COUNTRY}
     *
     *  @param success
     *              The callback method whenever the operators has successfully retrieved
     *  @param error
     *              The callback method when something went wrong during retrieval of the operators
     */
    public static void retrieveOperators(Activity activity, Consumer<List<Operator>> success, Consumer<Throwable> error) {
        // TODO: Attach the observable to CompositeDisposable

        List<Operator> operators = new ArrayList<>();
        Disposable disposable = retrieveOperators(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(OperatorsResponse::getOperators)
                .subscribe(operators::addAll, error, () -> success.accept(operators));
        attachRetrofitDisposable(activity, disposable);
    }

    private static Observable<OperatorsResponse> retrieveOperators(int offset) {
        return RetrofitSingleton.getTransitLandApi()
                .operators(API_COUNTRY, offset, API_PAGINATION_PER_PAGE)
                .concatMap(operatorsResponse -> {
                    Meta meta = operatorsResponse.getMeta();
                    if (meta.hasNext()) {
                        // Lets do another api call to retrieve operators
                        return Observable.just(operatorsResponse)
                                .concatWith(retrieveOperators(meta.getOffset() + operatorsResponse.getOperators().size()));
                    } else {
                        return Observable.just(operatorsResponse);
                    }
                });
    }

    /**
     *  Retrieves all of the {@link OperatorFeed} for given {@link Operator}
     *
     *  @param operator
     *              The bus operator to retrieve the all of the feeds
     *  @param success
     *              The callback method whenever the operator feeds has successfully retrieved
     *  @param error
     *              The callback method when something went wrong during retrieval of the operator feeds
     */
    public static void retrieveOperatorFeed(Activity activity, Operator operator, Consumer<OperatorFeed> success, Consumer<Throwable> error) {
        // TODO: Handle multiple represented_in_feed_onestop_ids in Operator, EG: "operators_in_feed" -> "operator_onestop_id" && "feed_onestop_id"

        Disposable disposable = RetrofitSingleton.getTransitLandApi()
                .feed(operator.getRepresentedInFeedOneStopIds().get(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success, error);
        attachRetrofitDisposable(activity, disposable);
    }

    /**
     *  Retrieves the {@link OperatorFeedVersion} for given {@link OperatorFeed}
     *
     *  @param operatorFeed
     *              The operator feed to retrieve the feed version
     *  @param success
     *              The callback method whenever the operator feed version has successfully retrieved
     *  @param error
     *              The callback method when something went wrong during retrieval of the operator feed version
     */
    public static void retrieveOperatorFeedVersion(Activity activity, OperatorFeed operatorFeed, Consumer<OperatorFeedVersion> success, Consumer<Throwable> error) {
        // TODO: Attach the observable to CompositeDisposable

        Disposable disposable = RetrofitSingleton.getTransitLandApi()
                .feedVersion(operatorFeed.getActiveFeedVersion())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success, error);
        attachRetrofitDisposable(activity, disposable);
    }

    /**
     *  Attaches the disposable within the composite to safely dispose the retrofit observers
     *  whenever the activity is destroyed.
     *
     *  This is to prevent executing the subscribe callbacks after the activity has been destroyed
     *  since retrofit is subscribed in the background thread.
     *
     *  @param activity
     *              The activity where the retrofit is executed
     *  @param disposable
     *              The disposable of the retrofit observers
     */
    private static void attachRetrofitDisposable(Activity activity, Disposable disposable) {
        // Attaches the disposable retrofit to the pool
        RetrofitSingleton.getDisposableInstance().add(disposable);
        // Detaches the disposable from the composite whenever the activity is destroyed
        LifecycleManager.watchActivity(activity, stage -> {
            if (stage == LifecycleManager.LifecycleStage.DESTROYED) {
                RetrofitSingleton.getDisposableInstance().remove(disposable);
            }
        });
    }

    /**
     * Singleton class that handles the {@link Retrofit} and {@link CompositeDisposable} instances.
     */
    private static final class RetrofitSingleton {

        private static Retrofit retrofitInstance;
        private static CompositeDisposable disposableInstance;

        /**
         *  Retrieves the {@link Retrofit} singleton instance
         */
        static Retrofit getRetrofitInstance() {
            if (retrofitInstance == null) {
                retrofitInstance = new Retrofit.Builder()
                        .baseUrl(TRANSIT_LAND_API)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }
            return retrofitInstance;
        }

        /**
         *  Retrieves the {@link CompositeDisposable} singleton instance
         */
        static CompositeDisposable getDisposableInstance() {
            if (disposableInstance == null) {
                disposableInstance = new CompositeDisposable();
            }
            return disposableInstance;
        }

        /**
         *  Retrieves the {@link TransitLandApi} instance
         */
        private static TransitLandApi getTransitLandApi() {
            return getRetrofitInstance().create(TransitLandApi.class);
        }
    }
}
