package com.rodolfonavalon.canadatransit.controller.transit;

import com.rodolfonavalon.canadatransit.model.database.Operator;
import com.rodolfonavalon.canadatransit.model.transit.Meta;
import com.rodolfonavalon.canadatransit.model.transit.Response.OperatorsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
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

    private static final String API_COUNTRY = "CA";
    private static final int API_PAGINATION_PER_PAGE = 50;

    /**
     *  Retrieves all buses {@link Operator} of the selected country: {@link #API_COUNTRY}
     *
     *  @param success
     *              The callback method whenever the operators has successfully retrieved
     *  @param error
     *              The callback method when something went wrong during retrieval of the operators
     */
    public static void retrieveOperators(Consumer<List<Operator>> success, Consumer<Throwable> error) {
        // TODO: Context to attach activity life cycle
        // TODO: Attach the observable to CompositeDisposable

        List<Operator> operators = new ArrayList<>();
        retrieveOperators(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(OperatorsResponse::getOperators)
                .subscribe(operators::addAll, error, () -> success.accept(operators));
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

    private static final class RetrofitSingleton {

        private static Retrofit rInstance;
        private static CompositeDisposable cdInstance;

        /**
         *  Retrieves the {@link Retrofit} singleton instance
         */
        private static Retrofit getInstance() {
            if (rInstance == null) {
                rInstance = new Retrofit.Builder()
                        .baseUrl(TRANSIT_LAND_API)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }
            return rInstance;
        }

        /**
         *  Retrieves the {@link CompositeDisposable} singleton instance
         */
        public static CompositeDisposable getDisposableInstance() {
            if (cdInstance == null) {
                cdInstance = new CompositeDisposable();
            }
            return cdInstance;
        }

        /**
         *  Retrieves the {@link TransitLandApi} instance
         */
        private static TransitLandApi getTransitLandApi() {
            return getInstance().create(TransitLandApi.class);
        }
    }
}
