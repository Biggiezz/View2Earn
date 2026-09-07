package com.vuhongcat.view2earn.app.models;

import com.google.gson.annotations.SerializedName;

public class ReferralData {
    @SerializedName("referralCode")
    private String referralCode;

    @SerializedName("shareLink")
    private String shareLink;

    @SerializedName("totalInvited")
    private int totalInvited;

    @SerializedName("qualified")
    private int qualified;

    @SerializedName("pending")
    private int pending;

    @SerializedName("totalEarned")
    private double totalEarned;

    public ReferralData() {
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public int getTotalInvited() {
        return totalInvited;
    }

    public void setTotalInvited(int totalInvited) {
        this.totalInvited = totalInvited;
    }

    public int getQualified() {
        return qualified;
    }

    public void setQualified(int qualified) {
        this.qualified = qualified;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public double getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(double totalEarned) {
        this.totalEarned = totalEarned;
    }
}
