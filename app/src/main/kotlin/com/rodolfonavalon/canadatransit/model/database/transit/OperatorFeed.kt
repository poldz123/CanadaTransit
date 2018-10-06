package com.rodolfonavalon.canadatransit.model.database.transit

import androidx.room.*
import com.rodolfonavalon.canadatransit.controller.converter.moshi.adapter.OperatorFeedCurrentFeedVersion
import com.rodolfonavalon.canadatransit.controller.manager.update.Updatable
import com.rodolfonavalon.canadatransit.controller.converter.moshi.adapter.OperatorFeedForeignKey
import com.rodolfonavalon.canadatransit.controller.converter.room.TransitLandConverter
import com.squareup.moshi.Json
import org.joda.time.DateTime

@Entity
@TypeConverters(TransitLandConverter::class)
data class OperatorFeed(
    @PrimaryKey
    @field:Json(name = "onestop_id") val feedOneStopId: String,

    @field:OperatorFeedForeignKey
    @field:Json(name = "operators_in_feed") val operatorOneStopId: String,
    @field:OperatorFeedCurrentFeedVersion
    @field:Json(name = "feed_versions") val currentFeedVersion: String,
    @field:Json(name = "active_feed_version") val activeFeedVersion: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "created_at") val createdAt: DateTime,
    @field:Json(name = "updated_at") val updatedAt: DateTime,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "feed_format") val feedFormat: String,
    @field:Json(name = "import_status") val importStatus: String,
    @field:Json(name = "feed_versions_url") val feedVersionUrl: String
) : Updatable {

    override fun trackingId(): String {
        return feedOneStopId
    }

    override fun update() {
    }
}

data class OperatorInFeed(
    @field:Json(name = "operator_onestop_id") val operatorOneStopId: String
)
