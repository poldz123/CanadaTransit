package com.rodolfonavalon.canadatransit.model.transit.Response;

import com.google.gson.annotations.SerializedName;
import com.rodolfonavalon.canadatransit.model.transit.Meta;

class MetaResponse {

    @SerializedName("meta")
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }
}
