package com.vuhongcat.view2earn.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryData {
    @SerializedName("transactions")
    private List<TransactionItem> transactions;

    public HistoryData() {
    }

    public List<TransactionItem> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionItem> transactions) {
        this.transactions = transactions;
    }
}
