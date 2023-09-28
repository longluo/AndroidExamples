package com.eurigo.websocketlib;

import org.java_websocket.framing.CloseFrame;

/**
 * @author Eurigo
 * Created on 2022/3/29 10:11
 * desc   : 断线原因
 */
public class DisConnectReason {

    public DisConnectReason(int code, String reason, boolean remote) {
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    /**
     * 断线码{@link CloseFrame}
     */
    private final int code;

    /**
     * 断线原因
     */
    private final String reason;

    /**
     * 是否由服务端发起的断线
     */
    private final boolean remote;

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public boolean isRemote() {
        return remote;
    }

    @Override
    public String toString() {
        return "DisConnectReason{" +
                "code=" + code +
                ", reason='" + reason + '\'' +
                ", remote=" + remote +
                '}';
    }
}
