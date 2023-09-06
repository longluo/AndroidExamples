package com.longluo.demo.okhttpthree.eiken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by iwata-PC on 15/05/01.
 * <p>
 * 英ナビAPI
 * <p>
 * アプリ内課金レシート情報取得リクエストクラス
 * 英ナビ!アプリのアプリ内課金のレシート情報を受け取り、プレミアムのステータスを適用する
 */
public class ENRequestGetReceipt extends ENRequestInner {

    /**
     * チケット種類の列挙
     */
    public enum ReceiptKind {
        UPGRADE("02"),
        NORMAL("20");

        private final String kind;

        ReceiptKind(String val) {
            kind = val;
        }

        public String getRawValue() {
            return kind;
        }
    }

    /**
     * 個人ID
     */
    @Expose
    @SerializedName("personal_id")
    private String personalId;

    /**
     * セキュリティ文字列
     */
    @Expose
    private String attestation;

    /**
     * アプリ内課金レシート Base64エンコード文字列
     */
    @Expose
    @SerializedName("receipt_data")
    private String receiptData;

    /**
     * base64 encoded signature
     */
    @Expose
    @SerializedName("signature")
    private String signature;

    /**
     * チケット種類
     */
    @Expose
    @SerializedName("ticket_kind")
    private String ticketKind;

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public String getReceiptData() {
        return receiptData;
    }

    public void setReceiptData(String receiptData) {
        this.receiptData = receiptData;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTicketKind() {
        return ticketKind;
    }

    public void setTicketKind(String ticketKind) {
        this.ticketKind = ticketKind;
    }

    /**
     * コンストラクタ
     *
     * @param builder
     */
    private ENRequestGetReceipt(Builder builder) {
        super();
        personalId = builder.getPersonalId();
        attestation = builder.getAttestation();
        receiptData = builder.getReceiptData();
        signature = builder.getSignature();
        ticketKind = builder.getTicketKind();
    }

    /**
     * パラメータビルダー
     */
    public static class Builder {

        private String personalId;

        private String attestation;

        private String receiptData;

        private String signature;

        private String ticketKind;

        public Builder personalId(String val) {
            personalId = val;
            return this;
        }

        public Builder attestation(String val) {
            attestation = val;
            return this;
        }

        public Builder receiptData(String val) {
            receiptData = val;
            return this;
        }

        public Builder signature(String val) {
            signature = val;
            return this;
        }

        public Builder ticketKind(ReceiptKind val) {
            ticketKind = val.getRawValue();
            return this;
        }

        public ENRequestGetReceipt build() {
            ENRequestGetReceipt param = new ENRequestGetReceipt(this);
            return param;
        }

        public String getPersonalId() {
            return personalId;
        }

        public void setPersonalId(String personalId) {
            this.personalId = personalId;
        }

        public String getAttestation() {
            return attestation;
        }

        public void setAttestation(String attestation) {
            this.attestation = attestation;
        }

        public String getReceiptData() {
            return receiptData;
        }

        public void setReceiptData(String receiptData) {
            this.receiptData = receiptData;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getTicketKind() {
            return ticketKind;
        }

        public void setTicketKind(String ticketKind) {
            this.ticketKind = ticketKind;
        }
    }
}
