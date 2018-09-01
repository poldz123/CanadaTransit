package com.rodolfonavalon.canadatransit.model.transit.response

import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeed
import com.rodolfonavalon.canadatransit.model.transit.Meta
import com.squareup.moshi.Json

class OperatorFeedsResponse(
        @field:Json(name ="feeds") val operatorFeeds: List<OperatorFeed>,
        @field:Json(name ="meta") override val meta: Meta
): MetaResponse()
