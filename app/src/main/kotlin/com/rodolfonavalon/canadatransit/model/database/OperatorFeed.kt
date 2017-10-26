package com.rodolfonavalon.canadatransit.model.database

import com.google.gson.annotations.SerializedName

import org.joda.time.DateTime

class OperatorFeed(
        @SerializedName("onestop_id") val feedOneStopId: String, // TODO: primary key
        @SerializedName("name") val name: String?,
        @SerializedName("created_at") val createdAt: DateTime,
        @SerializedName("updated_at") val updatedAt: DateTime,
        @SerializedName("url") val url: String,
        @SerializedName("feed_format") val feedFormat: String,
        @SerializedName("license_use_without_attribution") val licenseUseWithoutAttribution: String,
        @SerializedName("license_create_derived_product") val licenseCreatedDerivedProduct: String,
        @SerializedName("license_redistribute") val licenseRedistribute: String,
        @SerializedName("license_name") val licenseName: String?,
        @SerializedName("license_url") val licenseUrl: String?,
        @SerializedName("license_attribution_text") val licenseAttributionText: String?,
        @SerializedName("last_fetched_at") val lastFetchAt: DateTime,
        @SerializedName("last_imported_at") val lastImportedAt: DateTime,
        @SerializedName("import_status") val importStatus: String,
        @SerializedName("active_feed_version") val activeFeedVersion: String,
        @SerializedName("feed_versions_url") val feedVersionUrl: String,
        @SerializedName("feed_versions_count") val feedVersionCount: Int,
        @SerializedName("created_or_updated_in_changeset_id") val createdOrUpdatedInChangesetId: Int,
        @SerializedName("import_level_of_active_feed_version") val importLevelActiveFeedVersion: Int,
        @SerializedName("feed_versions") val feedVersion: ArrayList<String>,
        @SerializedName("changesets_imported_from_this_feed") val changesetsImportedFromThisFeed: ArrayList<Int>,
        @SerializedName("operators_in_feed") val operatorsInFeed: ArrayList<OperatorInFeed>
) {

    inner class OperatorInFeed(
            @SerializedName("gtfs_agency_id") val gtfsAgencyId: String?,
            @SerializedName("operator_onestop_id") val operatorOneStopId: String,
            @SerializedName("feed_onestop_id") val feedOneStopId: String,
            @SerializedName("operator_url") val operatorUrl: String,
            @SerializedName("feed_url") val feedUrl: String
    )
}
