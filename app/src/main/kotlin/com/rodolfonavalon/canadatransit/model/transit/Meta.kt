package com.rodolfonavalon.canadatransit.model.transit

import com.google.gson.annotations.SerializedName

class Meta(
        @SerializedName("sort_key") val sortKey: String,
        @SerializedName("sort_order") val sortOrder: String,
        @SerializedName("next") val next: String?,
        @SerializedName("prev") val prev: String?,
        @SerializedName("per_page") val perPage: Int,
        @SerializedName("offset") val offset: Int
) {

    operator fun hasNext(): Boolean {
        return next != null
    }
}
