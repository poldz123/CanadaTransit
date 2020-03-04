package com.rodolfonavalon.canadatransit.model.transit.response

import com.rodolfonavalon.canadatransit.model.database.transit.FeedVersion
import com.rodolfonavalon.canadatransit.model.transit.Meta
import com.squareup.moshi.Json

class FeedVersionsResponse(
    @field:Json(name = "feed_versions") override val response: List<FeedVersion>,
    @field:Json(name = "meta") override val meta: Meta
) : MetaResponse<FeedVersion>()
