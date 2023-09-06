package com.longluo.demo.okhttpthree.eiken;

import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;


public class ENRequestBase {

    /**
     * OkHttp用リクエストクラス
     */
    private Request httpRequest;


    /**
     * 英検ナビAPI名 列挙
     */
    public enum API {
        CHECK_LOGIN_USER("CheckLoginUser"), LOGOUT("Logout"), SET_AUTH_MAIL("SetAuthMail"), CHECK_PIN_CODE("CheckPinCode"), CHECK_USER_ID("CheckUserId"), SET_USER_DATA("SetUserData"), REQUEST_CHANGE_PASSWORD("RequestChangePassword"), CHECK_CHANGE_PASSWORD("CheckChangePassword"), CHANGE_PASSWORD("ChangePassword"), GET_STUDY_GEAR_COUPON_TICKET("GetStudyGearCouponTicket"), CHECK_BASIC_COUPON("CheckBasicCoupon"), GET_IN_APP_PURCHASE_RECEIPT("ApplyInAppPurchaseAndroidTicket");

        private final String name;

        API(String val) {
            name = val;
        }

        public String getRawValue() {
            return name;
        }
    }

    /**
     * APIのバージョン
     */
    private String version;

    /**
     * API名文字列
     */
    private String c;

    /**
     * 連想配列化した[入力データ項目]をJSON形式に変換した文字列
     * 注意: 連想配列オブジェクトのままだと、不正パラメータとして処理されてしまう。。。
     */
    private String bkeapi;

    public Request getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(Request httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getBkeapi() {
        return bkeapi;
    }

    public void setBkeapi(String bkeapi) {
        this.bkeapi = bkeapi;
    }

    /**
     * コンストラクタ
     *
     * @param builder
     */
    private ENRequestBase(Builder builder) {
        version = builder.getVersion();
        c = builder.getC();
        bkeapi = builder.getBkeapi();

        MediaType type = MediaType.parse("application/json; charset=UTF-8");
        String jsonStr = getJson();
        RequestBody body = RequestBody.create(type, jsonStr);
        httpRequest = new Request.Builder().post(body).url("https://dev-bke.ei-navi.jp/bke/api/index.php").build();
    }

    public String getJson() {
//        return new Gson().toJson(this);
        return new GsonBuilder().setPrettyPrinting()
//                .excludeFieldsWithoutExposeAnnotation()
                .create().toJson(this);
    }

    /**
     * パラメータビルドクラス
     */
    public static class Builder {
        private String version;
        private String c;
        private String bkeapi;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getBkeapi() {
            return bkeapi;
        }

        public void setBkeapi(String bkeapi) {
            this.bkeapi = bkeapi;
        }

        public Builder() {
            version = "1.00";
        }

        public Builder c(API api) {
            c = api.getRawValue();
            return this;
        }

        public Builder bkeapi(ENRequestInner val) {
//            bkeapi = new Gson().toJson(val, val.getClass());
            bkeapi = new GsonBuilder().setPrettyPrinting().create().toJson(val, val.getClass());
            return this;
        }

        public ENRequestBase build() {
            return new ENRequestBase(this);
        }
    }
}