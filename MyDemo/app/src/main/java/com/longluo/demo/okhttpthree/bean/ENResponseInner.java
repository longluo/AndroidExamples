package com.longluo.demo.okhttpthree.bean;

import com.google.gson.annotations.SerializedName;

public class ENResponseInner {

    /**
     * 処理結果
     */
    @SerializedName("result")
    protected boolean isSuccess;

    /**
     * 処理結果に対するメッセージ
     */
    protected String message;

    /**
     * 処理日時
     */
    @SerializedName("proc_day")
    protected String procDay;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProcDay() {
        return procDay;
    }

    public void setProcDay(String procDay) {
        this.procDay = procDay;
    }
}
