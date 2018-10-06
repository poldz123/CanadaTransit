package com.rodolfonavalon.canadatransit.model.transit.response

import com.rodolfonavalon.canadatransit.model.database.transit.Feed
import com.rodolfonavalon.canadatransit.model.transit.Meta
import com.squareup.moshi.Json

class FeedsResponse(
        @field:Json(name = "feeds") override val response: List<Feed>,
        @field:Json(name = "meta") override val meta: Meta

) : MetaResponse<Feed>()
