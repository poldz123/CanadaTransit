package com.rodolfonavalon.canadatransit.model.database.transit

import android.arch.persistence.room.*
import com.rodolfonavalon.canadatransit.model.database.converter.moshi.adapter.OperatorFeedForeignKey
import com.rodolfonavalon.canadatransit.model.database.converter.room.TransitLandConverter
import com.squareup.moshi.Json
import org.joda.time.DateTime

@Entity(indices = [
            (Index(value = ["feedOneStopId"], unique = true)),
            (Index(value = ["operatorOneStopId"], unique = true))
        ],
        foreignKeys = [
                ForeignKey(entity = Operator::class,
                        parentColumns = ["operatorOneStopId"],
                        childColumns = ["operatorOneStopId"],
                        onDelete = ForeignKey.CASCADE)
        ])
@TypeConverters(TransitLandConverter::class)
data class OperatorFeed(
        @PrimaryKey
        @field:Json(name = "onestop_id") val feedOneStopId: String,
        @field:OperatorFeedForeignKey
        @field:Json(name = "operators_in_feed") val operatorOneStopId: String,

        @field:Json(name ="name") val name: String?,
        @field:Json(name ="created_at") val createdAt: DateTime,
        @field:Json(name ="updated_at") val updatedAt: DateTime,
        @field:Json(name ="url") val url: String,
        @field:Json(name ="feed_format") val feedFormat: String,
        @field:Json(name ="last_fetched_at") val lastFetchAt: DateTime,
        @field:Json(name ="last_imported_at") val lastImportedAt: DateTime,
        @field:Json(name ="import_status") val importStatus: String,
        @field:Json(name ="active_feed_version") val activeFeedVersion: String,
        @field:Json(name ="feed_versions_url") val feedVersionUrl: String
)

data class OperatorInFeed(
        @field:Json(name ="operator_onestop_id") val operatorOneStopId: String
)
