package com.rodolfonavalon.canadatransit.model.transit.response

import com.google.gson.annotations.SerializedName
import com.rodolfonavalon.canadatransit.model.database.Operator
import com.rodolfonavalon.canadatransit.model.transit.Meta

class OperatorsResponse(
        @SerializedName("operators") val operators: List<Operator>,
        @SerializedName("meta") override val meta: Meta
): MetaResponse()