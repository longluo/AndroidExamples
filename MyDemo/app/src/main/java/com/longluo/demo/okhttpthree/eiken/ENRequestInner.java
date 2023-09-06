package com.longluo.demo.okhttpthree.eiken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by iwata-PC on 15/04/24.
 * <p>
 * 英ナビAPI
 * <p>
 * JSONパラメータの内部情報クラス
 * ENREquestBaseオブジェクトのbkeapiパラメータに相当する
 */
public class ENRequestInner {

    /**
     * 通信用ハッシュ
     */
    @SerializedName("crypt_key")
    private String cryptKey;

    /**
     * 処理日時
     */
    @Expose
    @SerializedName("proc_day")
    private String procDay;

    /**
     * オーナーID
     */
    @Expose
    @SerializedName("owner_id")
    private final String ownerId;

    /**
     * アプリケーションID
     */
    @Expose
    @SerializedName("app_id")
    private final String appId;


    public String getCryptKey() {
        return cryptKey;
    }

    public void setCryptKey(String cryptKey) {
        this.cryptKey = cryptKey;
    }

    public String getProcDay() {
        return procDay;
    }

    public void setProcDay(String procDay) {
        this.procDay = procDay;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getAppId() {
        return appId;
    }

    /**
     * コンストラクタ
     */
    public ENRequestInner() {
        ownerId = "GEAR";
        appId = "GEAR";
    }

}
