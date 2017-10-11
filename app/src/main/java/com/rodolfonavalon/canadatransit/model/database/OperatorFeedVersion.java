package com.rodolfonavalon.canadatransit.model.database;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

public class OperatorFeedVersion {

    @SerializedName("sha1")
    @Getter private String sha1;
    @SerializedName("earliest_calendar_date")
    @Getter private String earliestCalendarDate;
    @SerializedName("latest_calendar_date")
    @Getter private String latestCalendarDate;
    @SerializedName("md5")
    @Getter private String md5;
    @SerializedName("fetched_at")
    @Getter private String fetchedAt;
    @SerializedName("imported_at")
    @Getter private String importedAt;
    @SerializedName("created_at")
    @Getter private String createdAt;
    @SerializedName("updated_at")
    @Getter private String updatedAt;
    @SerializedName("feed_version_imports_url")
    @Getter private String feedVersionImportsUrl;
    @SerializedName("import_status")
    @Getter private String importStatus;
    @SerializedName("feed")
    @Getter private String feed;
    @SerializedName("url")
    @Getter private String url;
    @SerializedName("download_url")
    @Getter private String downloadUrl;
    @SerializedName("feedvalidator_url")
    @Getter private String feedValidatorUrl;

    @SerializedName("import_level")
    @Getter private int importLevel;

    @SerializedName("is_active_feed_version")
    @Getter private boolean isActiveFeedVersion;

    @SerializedName("feed_version_infos")
    @Getter private List<Integer> feedVersionInfos;
    @SerializedName("feed_version_imports"  )
    @Getter private List<Integer> feedVersionImports;
    @SerializedName("changesets_imported_from_this_feed_version")
    @Getter private List<Integer> changesetImportedFromThisFeedVersion;

}
