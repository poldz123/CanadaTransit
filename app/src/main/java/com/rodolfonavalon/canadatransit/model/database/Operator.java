package com.rodolfonavalon.canadatransit.model.database;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

import lombok.Getter;

public class Operator {

    @SerializedName("onestop_id") // TODO: primary key
    @Getter private String onestopId;

    @SerializedName("name")
    @Getter private String name;
    @SerializedName("website")
    @Getter private String website;
    @SerializedName("country")
    @Getter private String country;
    @SerializedName("state")
    @Getter private String state;
    @SerializedName("timezone")
    @Getter private String timezone;
    @SerializedName("created_at")
    @Getter private DateTime createdAt;
    @SerializedName("updated_at")
    @Getter private DateTime updatedAt;

    @Nullable
    @SerializedName("metro")
    @Getter private String metro;
    @Nullable
    @SerializedName("short_name")
    @Getter private String shortName;

    @SerializedName("tags")
    @Getter private Tags tags;
    @SerializedName("represented_in_feed_onestop_ids")
    @Getter private List<String> representedInFeedOneStopIds;

    private static class Tags {

        @SerializedName("agency_id")
        @Getter private String agencyId;
        @SerializedName("agency_lang")
        @Getter private String agencyLang;
        @SerializedName("agency_phone")
        @Getter private String agencyPhone;
    }
}
