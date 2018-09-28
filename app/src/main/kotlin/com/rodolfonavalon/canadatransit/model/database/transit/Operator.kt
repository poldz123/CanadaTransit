package com.rodolfonavalon.canadatransit.model.database.transit

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import androidx.annotation.NonNull
import com.rodolfonavalon.canadatransit.controller.manager.update.Updatable
import com.rodolfonavalon.canadatransit.controller.converter.room.TransitLandConverter
import com.squareup.moshi.Json
import org.joda.time.DateTime

@Entity
@TypeConverters(TransitLandConverter::class)
data class Operator(
    @PrimaryKey
    @field:Json(name = "onestop_id") val operatorOneStopId: String,

    @field:Json(name = "name") val name: String,
    @field:Json(name = "state") val state: String,
    @field:Json(name = "timezone") val timezone: String,
    @field:Json(name = "created_at") val createdAt: DateTime,
    @field:Json(name = "updated_at") val updatedAt: DateTime,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "website") val website: String?,
    @field:Json(name = "metro") val metro: String?,
    @field:Json(name = "short_name") val shortName: String?,
    @field:Json(name = "represented_in_feed_onestop_ids") val representedInFeedOneStopIds: List<String>,

    @Embedded
    @NonNull
    @field:Json(name = "tags") val tags: Tags?
) : Updatable {

        override fun trackingId(): String {
            return operatorOneStopId
        }

        override fun update() {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }
}

data class Tags(
    @field:Json(name = "agency_id") val agencyId: String?,
    @field:Json(name = "agency_lang") val agencyLang: String?,
    @field:Json(name = "agency_phone") val agencyPhone: String?
)
