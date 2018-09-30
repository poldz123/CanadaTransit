package com.rodolfonavalon.canadatransit.model.transit.response

import com.rodolfonavalon.canadatransit.model.database.transit.OperatorFeedVersion
import com.rodolfonavalon.canadatransit.model.transit.Meta
import com.squareup.moshi.Json

class OperatorFeedVersionsResponse(
        @field:Json(name = "feed_versions") override val response: List<OperatorFeedVersion>,
        @field:Json(name = "meta") override val meta: Meta
) : MetaResponse<OperatorFeedVersion>()