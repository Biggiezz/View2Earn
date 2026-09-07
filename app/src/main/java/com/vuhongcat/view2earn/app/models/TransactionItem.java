package com.vuhongcat.view2earn.app.models;

import com.google.gson.annotations.SerializedName;

public class TransactionItem {
    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("type")
    private String type;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    public TransactionItem() {
    }

    public TransactionItem(String id, String userId, double amount, String type, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
