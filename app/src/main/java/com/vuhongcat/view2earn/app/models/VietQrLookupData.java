package com.vuhongcat.view2earn.app.models;

import com.google.gson.annotations.SerializedName;

public class VietQrLookupData {
    @SerializedName("accountName")
    private String accountName;

    @SerializedName("ownerName")
    private String ownerName;

    public VietQrLookupData() {}

    public String getAccountName() {
        if (accountName != null && !accountName.trim().isEmpty()) {
            return accountName.trim();
        }
        if (ownerName != null && !ownerName.trim().isEmpty()) {
            return ownerName.trim();
        }
        return "";
    }
}
