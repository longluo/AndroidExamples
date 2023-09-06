package com.longluo.demo.okhttpthree.bean;

import com.google.gson.annotations.SerializedName;


public class Receipt {
    @SerializedName("error_code")
    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}

