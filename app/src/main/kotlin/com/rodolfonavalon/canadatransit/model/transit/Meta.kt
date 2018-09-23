package com.rodolfonavalon.canadatransit.model.transit

import com.squareup.moshi.Json

class Meta(
    @field:Json(name = "sort_key") val sortKey: String,
    @field:Json(name = "sort_order") val sortOrder: String,
    @field:Json(name = "next") val next: String?,
    @field:Json(name = "prev") val prev: String?,
    @field:Json(name = "per_page") val perPage: Int,
    @field:Json(name = "offset") val offset: Int
) {

    operator fun hasNext(): Boolean {
        return next != null
    }
}
