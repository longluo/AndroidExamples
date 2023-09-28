package com.longluo.gldemo.livepush;

public class LivePush {

    static {
        System.loadLibrary("live-push");
    }

    private String mLiveUrl;

    public LivePush(String liveUrl) {
        this.mLiveUrl = liveUrl;
    }

    private ConnectListener mConnectListener;

    public void setOnConnectListener(ConnectListener connectListener) {
        this.mConnectListener = connectListener;
    }

    public interface ConnectListener {
        void connectError(int errorCode, String errorMsg);

        void connectSuccess();
    }

    /**
     * 初始化連接
     */
    public void initConnect() {
        nativeInitConnect(mLiveUrl);
    }

    private native void nativeInitConnect(String liveUrl);

    // 連接的回調
    // called from jni
    private void onConnectError(int errorCode, String errorMsg) {
        if (mConnectListener != null) {
            mConnectListener.connectError(errorCode, errorMsg);
        }
    }

    // 連接的回調
    // called from jni
    private void onConnectSuccess() {
        if (mConnectListener != null) {
            mConnectListener.connectSuccess();
        }
    }
}
