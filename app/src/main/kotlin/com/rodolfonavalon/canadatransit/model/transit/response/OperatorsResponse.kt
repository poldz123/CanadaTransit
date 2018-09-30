package com.rodolfonavalon.canadatransit.model.transit.response

import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.model.transit.Meta
import com.squareup.moshi.Json

class OperatorsResponse(
    @field:Json(name = "operators") override val response: List<Operator>,
    @field:Json(name = "meta") override val meta: Meta
) : MetaResponse<Operator>()
