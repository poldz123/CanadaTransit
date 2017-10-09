package com.rodolfonavalon.canadatransit.model.transit;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Meta {

    @SerializedName("sort_key")
    private String sortKey;
    @SerializedName("sort_order")
    private String sortOrder;

    @Nullable
    @SerializedName("next")
    private String next;
    @Nullable
    @SerializedName("prev")
    private String prev;

    @SerializedName("per_page")
    private int perPage;
    @SerializedName("offset")
    private int offset;

    public String getSortKey() {
        return sortKey;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    @Nullable
    public String getNext() {
        return next;
    }

    @Nullable
    public String getPrev() {
        return prev;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getOffset() {
        return offset;
    }

    public boolean hasNext() {
        return next != null;
    }
}
