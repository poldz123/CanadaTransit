package com.rodolfonavalon.canadatransit.model.database.transit

import androidx.room.*
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Downloadable
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.controller.converter.room.TransitLandConverter
import com.squareup.moshi.Json
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Response

@Entity(indices = [
            Index(value = ["sha1"], unique = true),
            Index(value = ["feedOneStopId"], unique = true)
        ],
        foreignKeys = [
                ForeignKey(entity = OperatorFeed::class,
                parentColumns = ["feedOneStopId"],
                childColumns = ["feedOneStopId"],
                onUpdate = ForeignKey.CASCADE)
        ])
@TypeConverters(TransitLandConverter::class)
data class OperatorFeedVersion(
    @PrimaryKey
    @field:Json(name = "sha1") val sha1: String,
    @field:Json(name = "feed") val feedOneStopId: String,

    @field:Json(name = "earliest_calendar_date") val earliestCalendarDate: DateTime,
    @field:Json(name = "latest_calendar_date") val latestCalendarDate: DateTime,
    @field:Json(name = "md5") val md5: String,
    @field:Json(name = "fetched_at") val fetchedAt: DateTime,
    @field:Json(name = "imported_at") val importedAt: DateTime,
    @field:Json(name = "created_at") val createdAt: DateTime,
    @field:Json(name = "updated_at") val updatedAt: DateTime,
    @field:Json(name = "import_status") val importStatus: String,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "download_url") val downloadUrl: String,
    @field:Json(name = "import_level") val importLevel: Int,
    @field:Json(name = "is_active_feed_version") val isActiveFeedVersion: Boolean
) : Downloadable {

        override fun trackingId(): String {
            return sha1
        }

        override fun transferObservable(): Observable<Response<ResponseBody>> {
            return TransitLandApi.downloadOperatorFeed(this)
        }

        override fun transferDirectoryPath(): String {
            return "feed/transitland/feed-version/"
        }

        override fun download() {
            // TODO Transfer Manager download
            TransferManager.download(this)
        }
}
