package com.rodolfonavalon.canadatransit.model.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.rodolfonavalon.canadatransit.model.database.converter.room.TransitLandConverter
import org.joda.time.DateTime

@Entity
@TypeConverters(TransitLandConverter::class)
class OperatorFeedVersion(
        @PrimaryKey
        @SerializedName("feed") val feedOneStopId: String,

        @SerializedName("sha1") val sha1: String,
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
)
