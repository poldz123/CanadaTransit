package com.rodolfonavalon.canadatransit.model.database.transit

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.rodolfonavalon.canadatransit.model.database.converter.room.TransitLandConverter
import org.joda.time.DateTime

@Entity
@TypeConverters(TransitLandConverter::class)
class Operator(
        @PrimaryKey
        @SerializedName("onestop_id") val operatorOneStopId: String,

        @SerializedName("name") val name: String,
        @SerializedName("website") val website: String,
        @SerializedName("country") val country: String,
        @SerializedName("state") val state: String,
        @SerializedName("timezone") val timezone: String,
        @SerializedName("created_at") val createdAt: DateTime,
        @SerializedName("updated_at") val updatedAt: DateTime,
        @SerializedName("metro") val metro: String?,
        @SerializedName("short_name") val shortName: String?,
        @SerializedName("represented_in_feed_onestop_ids") val representedInFeedOneStopIds: List<String>,

        @Embedded
        @SerializedName("tags") val tags: Tags
)

class Tags(
        @SerializedName("agency_id") val agencyId: String,
        @SerializedName("agency_lang") val agencyLang: String,
        @SerializedName("agency_phone") val agencyPhone: String
)
