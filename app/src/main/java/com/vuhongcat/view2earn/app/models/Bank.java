package com.vuhongcat.view2earn.app.models;

import com.google.gson.annotations.SerializedName;

public class Bank {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("bin")
    private String bin;

    @SerializedName("shortName")
    private String shortName;

    @SerializedName("logo")
    private String logo;

    public Bank() {}

    public Bank(String bin, String code, String shortName, String name) {
        this(bin, code, shortName, name, "https://cdn.vietqr.io/img/" + code + ".png");
    }

    public Bank(String bin, String code, String shortName, String name, String logo) {
        this.bin = bin;
        this.code = code;
        this.shortName = shortName;
        this.name = name;
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getBin() {
        return bin;
    }

    public String getShortName() {
        return shortName != null ? shortName : code;
    }

    public String getLogo() {
        return logo;
    }

    public String getDisplayName() {
        String shortN = getShortName();
        if (shortN != null && !shortN.isEmpty() && name != null && !name.isEmpty()) {
            return shortN + " - " + name;
        } else if (shortN != null && !shortN.isEmpty()) {
            return shortN;
        }
        return name != null ? name : "";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
