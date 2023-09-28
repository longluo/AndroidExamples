package com.eurigo.websocketlib;

import static com.eurigo.websocketlib.WsManager.DEFAULT_WEBSOCKET;

import com.eurigo.websocketlib.util.ThreadUtils;
import com.eurigo.websocketlib.util.WsLogUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eurigo
 * Created on 2022/3/29 17:14
 * desc   : WebSocket客户端
 */
public class WsClient extends WebSocketClient {

    public WsClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout
            , Builder builder) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
        if (builder.listener == null) {
            throw new IllegalArgumentException("WsListener must not be null");
        }
        if (builder.serverUrl == null) {
            throw new IllegalArgumentException("serverUrl must not be null");
        }
        this.listener = builder.listener;
        this.serverUrl = builder.serverUrl;
        this.draft = builder.draft;
        this.connectTimeout = builder.connectTimeout;
        this.httpHeaders = builder.httpHeaders;
        this.pingInterval = builder.pingInterval;
        this.wsKey = builder.wsKey;
        this.reConnectCount = builder.reConnectCount;
        this.reConnectInterval = builder.reConnectInterval;
        this.reConnectWhenNetworkAvailable = builder.reConnectWhenNetworkAvailable;
        setConnectionLostTimeout(pingInterval);
    }

    /**
     * WebSocket回调
     */
    private IWsListener listener;

    /**
     * 服务端地址
     */
    private final String serverUrl;

    /**
     * Websocket协议，默认6455
     */
    private final Draft draft;

    /**
     * 连接超时时间，默认值：0
     */
    private int connectTimeout;

    /**
     * 初始化时设置的标识，不设置，自动使用默认WebSocket
     */
    private final String wsKey;

    /**
     * 心跳时间，单位秒，默认60。
     * 如果小于等于0，则关闭心跳功能。
     * 如果开启，若1.5倍间隔时间未收到服务端的pong响应，则自动断开连接
     */
    private final int pingInterval;

    /**
     * 重连次数，默认10，大于0开启重连功能
     */
    private final int reConnectCount;

    /**
     * 自动重连间隔, 单位毫秒，默认值1000
     */
    private final long reConnectInterval;

    /**
     * 是否正在执行重连任务
     */
    private boolean isReconnectTaskRun = false;

    /**
     * 重连任务
     */
    private ReConnectTask task;

    public ReConnectTask getTask() {
        return task;
    }

    public void runReconnectTask() {
        if (isReconnectTaskRun()) {
            WsLogUtil.e(wsKey + "的重连任务已开启");
            return;
        }
        task = new ReConnectTask(this);
        task.execute();
    }

    /**
     * 网络可用时是否自动重连，默认值true
     */
    private final boolean reConnectWhenNetworkAvailable;

    private final Map<String, String> httpHeaders;

    public IWsListener getListener() {
        return listener;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getWsKey() {
        return wsKey;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    public int getReConnectCount() {
        return reConnectCount;
    }

    public long getReConnectInterval() {
        return reConnectInterval;
    }

    public boolean isReConnectWhenNetworkAvailable() {
        return reConnectWhenNetworkAvailable;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setReconnectTaskRun(boolean reconnectTaskRun) {
        isReconnectTaskRun = reconnectTaskRun;
    }

    public boolean isReconnectTaskRun() {
        return isReconnectTaskRun;
    }

    @Override
    public void send(String text) {
        super.send(text);
        listener.onSendMessage(this, text);
    }

    @Override
    public void send(byte[] data) {
        super.send(data);
        listener.onSendMessage(this, data);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        ThreadUtils.cancel(task);
        listener.onConnected(this);
    }

    @Override
    public void onMessage(String message) {
        listener.onMessage(this, message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        listener.onDisconnect(this, new DisConnectReason(code, reason, remote));
        if (reConnectCount > 0) {
            runReconnectTask();
        }
    }

    @Override
    public void onError(Exception ex) {
        listener.onError(this, ex);
        if (reConnectCount > 0) {
            runReconnectTask();
        }
    }

    @Override
    public void onWebsocketPing(WebSocket conn, Framedata frameData) {
        super.onWebsocketPing(conn, frameData);
        listener.onPing(this, frameData);
    }

    @Override
    public void onWebsocketPong(WebSocket conn, Framedata frameData) {
        listener.onPong(this, frameData);
    }

    public static final class Builder {

        private String serverUrl;

        private IWsListener listener;

        private String wsKey = DEFAULT_WEBSOCKET;

        private Draft draft = new Draft_6455();

        private int connectTimeout = 0;

        private int pingInterval = 60;

        private int reConnectCount = 10;

        private long reConnectInterval = 1000;

        private boolean reConnectWhenNetworkAvailable = true;

        private Map<String, String> httpHeaders = new HashMap<>();

        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder setListener(IWsListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setDraft(Draft draft) {
            this.draft = draft;
            return this;
        }

        public Builder setWsKey(String wsKey) {
            this.wsKey = wsKey;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;

        }

        public Builder setPingInterval(int pingInterval) {
            this.pingInterval = pingInterval;
            return this;
        }

        public Builder setReConnectCount(int reConnectCount) {
            this.reConnectCount = reConnectCount;
            return this;
        }

        public Builder setReConnectInterval(long reConnectInterval) {
            this.reConnectInterval = reConnectInterval;
            return this;
        }

        public Builder setHttpHeaders(Map<String, String> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder setReConnectWhenNetworkAvailable(boolean reConnectWhenNetworkAvailable) {
            this.reConnectWhenNetworkAvailable = reConnectWhenNetworkAvailable;
            return this;
        }

        public WsClient build() {
            return new WsClient(URI.create(serverUrl), draft
                    , httpHeaders, connectTimeout, this);
        }
    }
}
