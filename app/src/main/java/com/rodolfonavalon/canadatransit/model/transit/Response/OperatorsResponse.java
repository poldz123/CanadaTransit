package com.rodolfonavalon.canadatransit.model.transit.Response;

import com.google.gson.annotations.SerializedName;
import com.rodolfonavalon.canadatransit.model.database.Operator;

import java.util.List;

import lombok.Getter;

public class OperatorsResponse extends MetaResponse {

    @SerializedName("operators")
    @Getter private List<Operator> operators;
}
