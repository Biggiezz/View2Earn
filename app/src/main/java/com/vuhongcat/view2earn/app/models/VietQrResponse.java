package com.vuhongcat.view2earn.app.models;

import com.google.gson.annotations.SerializedName;

public class VietQrResponse<T> {
    @SerializedName("code")
    private String code;

    @SerializedName("desc")
    private String desc;

    @SerializedName("data")
    private T data;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return "00".equals(code);
    }
}
