package com.rodolfonavalon.canadatransit.model.transit.Response;

import com.google.gson.annotations.SerializedName;
import com.rodolfonavalon.canadatransit.model.transit.Meta;

import lombok.Getter;

class MetaResponse {

    @SerializedName("meta")
    @Getter private Meta meta;
}
