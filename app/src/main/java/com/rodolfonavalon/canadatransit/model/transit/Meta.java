package com.rodolfonavalon.canadatransit.model.transit;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class Meta {

    @SerializedName("sort_key")
    @Getter private String sortKey;
    @SerializedName("sort_order")
    @Getter private String sortOrder;

    @Nullable
    @SerializedName("next")
    @Getter private String next;
    @Nullable
    @SerializedName("prev")
    @Getter private String prev;

    @SerializedName("per_page")
    @Getter private int perPage;
    @SerializedName("offset")
    @Getter private int offset;

    public boolean hasNext() {
        return next != null;
    }
}
