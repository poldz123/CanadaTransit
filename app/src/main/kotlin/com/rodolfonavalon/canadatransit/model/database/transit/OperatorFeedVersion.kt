package com.rodolfonavalon.canadatransit.model.database.transit

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import com.rodolfonavalon.canadatransit.controller.manager.transfer.TransferManager
import com.rodolfonavalon.canadatransit.controller.manager.transfer.Transferable
import com.rodolfonavalon.canadatransit.controller.transit.TransitLandApi
import com.rodolfonavalon.canadatransit.model.database.converter.room.TransitLandConverter
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
                onDelete = ForeignKey.CASCADE)
        ])
@TypeConverters(TransitLandConverter::class)
class OperatorFeedVersion(
        @PrimaryKey
        @SerializedName("sha1") val sha1: String,
        @SerializedName("feed") val feedOneStopId: String,

        @SerializedName("earliest_calendar_date") val earliestCalendarDate: DateTime,
        @SerializedName("latest_calendar_date") val latestCalendarDate: DateTime,
        @SerializedName("md5") val md5: String,
        @SerializedName("fetched_at") val fetchedAt: DateTime,
        @SerializedName("imported_at") val importedAt: DateTime,
        @SerializedName("created_at") val createdAt: DateTime,
        @SerializedName("updated_at") val updatedAt: DateTime,
        @SerializedName("feed_version_imports_url") val feedVersionImportsUrl: String,
        @SerializedName("import_status") val importStatus: String,
        @SerializedName("url") val url: String,
        @SerializedName("download_url") val downloadUrl: String,
        @SerializedName("feedvalidator_url") val feedValidatorUrl: String,
        @SerializedName("import_level") val importLevel: Int,
        @SerializedName("is_active_feed_version") val isActiveFeedVersion: Boolean,
        @SerializedName("feed_version_infos") val feedVersionInfos: List<Int>,
        @SerializedName("feed_version_imports") val feedVersionImports: List<Int>,
        @SerializedName("changesets_imported_from_this_feed_version") val changesetImportedFromThisFeedVersion: List<Int>
): Transferable.Downloadable {

        override fun transferObservable(): Observable<Response<ResponseBody>> {
            return TransitLandApi.downloadOperatorFeed(this)
        }

        override fun transferTrackingId(): String {
            return sha1
        }

        override fun transferDirectoryPath(): String {
            return "feed/transitland/feed-version/"
        }

        override fun download() {
            // TODO: Transfer Manager download
            TransferManager.download(this)
        }
}
