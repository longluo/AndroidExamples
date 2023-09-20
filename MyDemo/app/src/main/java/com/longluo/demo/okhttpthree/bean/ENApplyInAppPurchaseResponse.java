package com.longluo.demo.okhttpthree.bean;

import com.google.gson.annotations.SerializedName;

public class ENApplyInAppPurchaseResponse extends ENResponseInner {

    @SerializedName("receipt")
    private Receipt receipt;

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}


