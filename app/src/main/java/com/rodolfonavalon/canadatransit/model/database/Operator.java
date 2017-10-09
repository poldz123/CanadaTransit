package com.rodolfonavalon.canadatransit.model.database;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Operator {

    @SerializedName("name")
    private String name;
    @SerializedName("website")
    private String website;
    @SerializedName("country")
    private String country;
    @SerializedName("state")
    private String state;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("onestop_id")
    private String onestopId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    @Nullable
    @SerializedName("metro")
    private String metro;
    @Nullable
    @SerializedName("short_name")
    private String shortName;

    @SerializedName("tags")
    private Tags tags;
    @SerializedName("represented_in_feed_onestop_ids")
    private List<String> feedIds;

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getOnestopId() {
        return onestopId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Nullable
    public String getMetro() {
        return metro;
    }

    @Nullable
    public String getShortName() {
        return shortName;
    }

    public Tags getTags() {
        return tags;
    }

    public List<String> getFeedIds() {
        return feedIds;
    }

    private static class Tags {

        @SerializedName("agency_id")
        private String agencyId;
        @SerializedName("agency_lang")
        private String agencyLang;
        @SerializedName("agency_phone")
        private String agencyPhone;

        public String getAgencyId() {
            return agencyId;
        }

        public String getAgencyLang() {
            return agencyLang;
        }

        public String getAgencyPhone() {
            return agencyPhone;
        }
    }
}
