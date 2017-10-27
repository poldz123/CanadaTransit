package com.rodolfonavalon.canadatransit.model.database

import com.google.gson.annotations.SerializedName

import org.joda.time.DateTime

// TODO: primary key
class Operator(
        @SerializedName("onestop_id") val onestopId: String, // TODO: primary key
        @SerializedName("name") val name: String,
        @SerializedName("website") val website: String,
        @SerializedName("country") val country: String,
        @SerializedName("state") val state: String,
        @SerializedName("timezone") val timezone: String,
        @SerializedName("created_at") val createdAt: DateTime,
        @SerializedName("updated_at") val updatedAt: DateTime,
        @SerializedName("metro") val metro: String?,
        @SerializedName("short_name") val shortName: String?,
        @SerializedName("tags") val tags: Tags,
        @SerializedName("represented_in_feed_onestop_ids") val representedInFeedOneStopIds: ArrayList<String>
) {

    inner class Tags(
            @SerializedName("agency_id") val agencyId: String,
            @SerializedName("agency_lang") val agencyLang: String,
            @SerializedName("agency_phone") val agencyPhone: String
    )
}