package com.rodolfonavalon.canadatransit.model.database;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

public class OperatorFeed {

    @Nullable
    @SerializedName("name")
    @Getter private String name;
    @SerializedName("onestop_id")
    @Getter private String oneStopId;
    @SerializedName("created_at")
    @Getter private String createdAt;
    @SerializedName("updated_at")
    @Getter private String updatedAt;
    @SerializedName("url")
    @Getter private String url;
    @SerializedName("feed_format")
    @Getter private String feedFormat;

    @SerializedName("license_use_without_attribution")
    @Getter private String licenseUseWithoutAttribution;
    @SerializedName("license_create_derived_product")
    @Getter private String licenseCreatedDerivedProduct;
    @SerializedName("license_redistribute")
    @Getter private String licenseRedistribute;
    @Nullable
    @SerializedName("license_name")
    @Getter private String licenseName;
    @Nullable
    @SerializedName("license_url")
    @Getter private String licenseUrl;
    @Nullable
    @SerializedName("license_attribution_text")
    @Getter private String licenseAttributionText;

    @SerializedName("last_fetched_at")
    @Getter private String lastFetchAt;
    @SerializedName("last_imported_at")
    @Getter private String lastImportedAt;
    @SerializedName("import_status")
    @Getter private String importStatus;
    @SerializedName("active_feed_version")
    @Getter private String activeFeedVersion;
    @SerializedName("feed_versions_url")
    @Getter private String feedVersionUrl;

    @SerializedName("feed_versions_count")
    @Getter private int feedVersionCount;
    @SerializedName("created_or_updated_in_changeset_id")
    @Getter private int createdOrUpdatedInChangesetId;
    @SerializedName("import_level_of_active_feed_version")
    @Getter private int importLevelActiveFeedVersion;

    @SerializedName("feed_versions")
    @Getter private List<String> feedVersion;
    @SerializedName("changesets_imported_from_this_feed")
    @Getter private List<Integer> changesetsImportedFromThisFeed;
    @SerializedName("operators_in_feed")
    @Getter private List<OperatorInFeed> operatorsInFeed;

    private static class OperatorInFeed {

        @Nullable
        @SerializedName("gtfs_agency_id")
        @Getter private String gtfsAgencyId;
        @SerializedName("operator_onestop_id")
        @Getter private String operatorOneStopId;
        @SerializedName("feed_onestop_id")
        @Getter private String feedOneStopId;
        @SerializedName("operator_url")
        @Getter private String operatorUrl;
        @SerializedName("feed_url")
        @Getter private String feedUrl;

    }
}
