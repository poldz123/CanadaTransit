package com.rodolfonavalon.canadatransit.controller.transit;

import com.rodolfonavalon.canadatransit.model.transit.Response.OperatorsResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface TransitLandApi {

    @GET("operators?exclude_geometry=true")
    Observable<OperatorsResponse> operators(@Query("country") String country, @Query("offset") int offset, @Query("per_page") int perPage);
}
