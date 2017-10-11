package com.rodolfonavalon.canadatransit.controller.transit;

import com.rodolfonavalon.canadatransit.model.database.OperatorFeed;
import com.rodolfonavalon.canadatransit.model.database.OperatorFeedVersion;
import com.rodolfonavalon.canadatransit.model.transit.Response.OperatorsResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface TransitLandApi {

    @GET("operators?exclude_geometry=true")
    Observable<OperatorsResponse> operators(@Query("country") String country, @Query("offset") int offset, @Query("per_page") int perPage);

    // TODO: object model
    @GET("feeds/{onestop_id}?exclude_geometry=true")
    Observable<OperatorFeed> feed(@Path("onestop_id") String oneStopId);

    // TODO: object model
    @GET("feed_versions/{active_feed_version}")
    Observable<OperatorFeedVersion> feedVersion(@Path("active_feed_version") String activeFeedVersion);

}
