package com.rodolfonavalon.canadatransit.model.database.transit

import androidx.room.*
import com.rodolfonavalon.canadatransit.controller.converter.moshi.adapter.FeedCurrentFeedVersion
import com.rodolfonavalon.canadatransit.controller.manager.update.Updatable
import com.rodolfonavalon.canadatransit.controller.converter.moshi.adapter.FeedForeignKey
import com.rodolfonavalon.canadatransit.controller.converter.room.TransitLandConverter
import com.squareup.moshi.Json
import org.joda.time.DateTime

@Entity
@TypeConverters(TransitLandConverter::class)
data class Feed(
    @PrimaryKey
    @field:Json(name = "onestop_id") val feedOneStopId: String,

    @field:FeedForeignKey
    @field:Json(name = "operators_in_feed") val operatorOneStopId: String,
    @field:FeedCurrentFeedVersion
    @field:Json(name = "feed_versions") val currentFeedVersion: String,
    @field:Json(name = "active_feed_version") val activeFeedVersion: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "created_at") val createdAt: DateTime,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "feed_format") val feedFormat: String,
    @field:Json(name = "import_status") val importStatus: String,
    @field:Json(name = "feed_versions_url") val feedVersionUrl: String
) {
    @Transient
    val updatedAt: DateTime = DateTime.now()
}

data class OperatorInFeed(
    @field:Json(name = "operator_onestop_id") val operatorOneStopId: String
)
