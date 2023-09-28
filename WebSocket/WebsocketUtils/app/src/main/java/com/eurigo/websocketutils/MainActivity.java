package com.eurigo.websocketutils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eurigo.websocketlib.DisConnectReason;
import com.eurigo.websocketlib.IWsListener;
import com.eurigo.websocketlib.WsClient;
import com.eurigo.websocketlib.WsManager;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eurigo
 * Created on 2022/3/28 10:35
 * desc   :
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , IWsListener {

    private LogAdapter mAdapter;

    private List<WebSocket> serverWebSocketList = new ArrayList<>();
    private String ipAddress = NetworkUtils.getIPAddress(true);
    private static final int PORT = 8800;
    private static final String REGEX = "ws:/";

    private EditText etAddress, etMsg;
    private Button btnClose, btnConnect, btnSend;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startLocalServer();
        connectWebSocket("ws://".concat(ipAddress).concat(":" + PORT));
    }

    private void initView() {

        RecyclerView mRecyclerView = findViewById(R.id.rcv_ap_log);
        mAdapter = new LogAdapter(new ArrayList<>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        btnConnect = findViewById(R.id.btn_connect);
        btnClose = findViewById(R.id.btn_close);
        btnSend = findViewById(R.id.btn_send);
        btnConnect.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        etAddress = findViewById(R.id.et_address);
        etMsg = findViewById(R.id.et_message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ipAddress = NetworkUtils.getIPAddress(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WsManager.getInstance().destroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                connectWebSocket(getEditText(etAddress));
                break;
            case R.id.btn_close:
                WsManager.getInstance().disConnect();
                break;
            case R.id.btn_send:
                String msg = getEditText(etMsg);
                if (TextUtils.isEmpty(msg)) {
                    ToastUtils.showShort("请输入消息");
                    return;
                }
                WsManager.getInstance().send(getEditText(etMsg));
                break;
            default:
                break;
        }
    }

    private String getEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     * 连接WebSocket
     */
    private void connectWebSocket(String address) {
        // 构造一个默认WebSocket客户端
        WsClient wsClient = new WsClient.Builder()
                .setServerUrl(address)
                .setListener(this)
                .setPingInterval(30)
                .build();
        // 初始化并启动连接
        WsManager.getInstance()
                .init(wsClient)
                .start();
    }

    /**
     * 开启本地WebSocket服务
     */
    private void startLocalServer() {
        WebSocketServer webSocketServer = new WebSocketServer(new InetSocketAddress(PORT)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                serverWebSocketList.add(conn);
                LogUtils.e("服务端日志", this.getLocalSocketAddress(conn).toString() + "已连接");
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                LogUtils.e("服务端日志", conn + "已关闭"
                        , new DisConnectReason(code, reason, remote).toString());
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                runOnUiThread(() -> {
                    mAdapter.addDataAndScroll(message, false);
                });
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                LogUtils.e("服务端日志", "异常", ex);
            }

            @Override
            public void onStart() {
                LogUtils.e("服务端日志", "服务器已启动", "地址：" + ipAddress + ":" + PORT);
            }
        };

        webSocketServer.start();
    }

    /**
     * 客户端回调start
     */
    @Override
    public void onConnected(WsClient client) {
        runOnUiThread(() -> {
            etAddress.setText(REGEX.concat(client.getRemoteSocketAddress().toString()));
            mAdapter.addDataAndScroll("连接成功", true);
            btnConnect.setEnabled(false);
            btnClose.setEnabled(true);
        });
    }

    @Override
    public void onDisconnect(WsClient client, DisConnectReason reason) {
        runOnUiThread(() -> {
            mAdapter.addDataAndScroll("连接断开", true);
            btnConnect.setEnabled(true);
            btnClose.setEnabled(false);
        });
    }

    @Override
    public void onError(WsClient webSocketClient, Exception ex) {
        LogUtils.e("客户端日志", "连接失败", ex.getMessage());
    }

    @Override
    public void onMessage(WsClient webSocketClient, String message) {
        runOnUiThread(() -> {
            mAdapter.addDataAndScroll(message, true);
        });
    }

    @Override
    public void onPing(WsClient webSocketClient, Framedata frameData) {
        LogUtils.e("客户端日志", "收到Ping");
    }

    @Override
    public void onPong(WsClient webSocketClient, Framedata frameData) {
        LogUtils.e("客户端日志", "收到Pong");
    }

    @Override
    public void onSendMessage(WsClient client, String message) {
        LogUtils.e("客户端日志", "发送消息", message);
    }
}
