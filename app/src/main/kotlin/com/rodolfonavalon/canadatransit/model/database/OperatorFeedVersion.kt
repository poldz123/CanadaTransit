package com.rodolfonavalon.canadatransit.model.database

import com.google.gson.annotations.SerializedName
import java.util.*

class OperatorFeedVersion(
        @SerializedName("feed") val feedOneStopId: String,  // TODO: primary key
        @SerializedName("sha1") val sha1: String,
        @SerializedName("earliest_calendar_date") val earliestCalendarDate: Date,
        @SerializedName("latest_calendar_date") val latestCalendarDate: Date,
        @SerializedName("md5") val md5: String,
        @SerializedName("fetched_at") val fetchedAt: Date,
        @SerializedName("imported_at") val importedAt: Date,
        @SerializedName("created_at") val createdAt: Date,
        @SerializedName("updated_at") val updatedAt: Date,
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
