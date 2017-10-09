package com.rodolfonavalon.canadatransit.model.transit.Response;

import com.google.gson.annotations.SerializedName;
import com.rodolfonavalon.canadatransit.model.database.Operator;

import java.util.List;

public class OperatorsResponse extends MetaResponse {

    @SerializedName("operators")
    private List<Operator> operators;

    public List<Operator> getOperators() {
        return operators;
    }
}
