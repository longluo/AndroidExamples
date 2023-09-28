package com.eurigo.websocketlib;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import com.eurigo.websocketlib.util.AppUtils;
import com.eurigo.websocketlib.util.ThreadUtils;
import com.eurigo.websocketlib.util.WsLogUtil;

import java.util.HashMap;

/**
 * @author Eurigo
 * Created on 2022/3/29 9:58
 * desc   : WebSocket管理器
 */
public class WsManager {

    /**
     * 所有WebSocket的合集
     */
    private final HashMap<String, WsClient> clientMap = new HashMap<>();

    public HashMap<String, WsClient> getClientMap() {
        return clientMap;
    }

    public static final String DEFAULT_WEBSOCKET = "DEFAULT_WEBSOCKET";
    public static final String NO_INIT = "没有初始化";

    private NetworkChangedReceiver networkChangedReceiver;

    public WsManager() {
    }

    public static WsManager getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private final static WsManager INSTANCE = new WsManager();
    }

    public boolean isNetworkAvailable() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConnectivityManager mConnectivityManager = (ConnectivityManager) AppUtils.getInstance().getApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager
                .getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }


    /**
     * 注册网络变化监听广播，网络由不可用变为可用时会重新连接 WebSocket
     * 调用后会立即触发一次OnReceive
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private void registerNetworkChangedReceiver() {
        if (networkChangedReceiver != null) {
            WsLogUtil.e("广播已注册");
            return;
        }
        if (checkPermission()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            networkChangedReceiver = new NetworkChangedReceiver();
            AppUtils.getInstance().getApp()
                    .getApplicationContext().registerReceiver(networkChangedReceiver, filter);
        } else {
            WsLogUtil.e("未获取到网络状态权限，广播监听器无法注册");
        }
    }

    /**
     * 解除网络状态广播
     */
    private void unRegisterNetworkChangedReceiver() {
        if (networkChangedReceiver == null) {
            WsLogUtil.d("网络状态广播未注册");
            return;
        }
        AppUtils.getInstance().getApp()
                .getApplicationContext().unregisterReceiver(networkChangedReceiver);
        networkChangedReceiver = null;
    }

    /**
     * 判断是否有网络权限{@link Manifest.permission#ACCESS_NETWORK_STATE}
     */
    private boolean checkPermission() {
        Context context = AppUtils.getInstance().getApp().getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PackageManager.PERMISSION_GRANTED == context.getPackageManager()
                    .checkPermission(ACCESS_NETWORK_STATE, context.getPackageName());
        }
        return true;
    }

    private void addClient(WsClient wsClient) {
        if (TextUtils.equals(wsClient.getWsKey(), DEFAULT_WEBSOCKET)) {
            clientMap.put(DEFAULT_WEBSOCKET, wsClient);
            return;
        }
        if (clientMap.containsKey(wsClient.getWsKey())) {
            WsLogUtil.e("初始化失败,已存在" + wsClient.getWsKey()
                    + ",请勿重复初始化,多个请设置WsKey");
            return;
        }
        clientMap.put(wsClient.getWsKey(), wsClient);
    }

    /**
     * 是否关闭日志输出，默认开启
     *
     * @param isClose 是否关闭
     */
    public void closeLog(boolean isClose) {
        WsLogUtil.closeLog(isClose);
    }

    /**
     * 获取默认的WebSocket
     */
    public WsManager init(WsClient wsClient) {
        addClient(wsClient);
        return this;
    }

    /**
     * 开始执行连接，每个WebSocket都会创建对应的重连任务
     */
    public void start() {
        registerNetworkChangedReceiver();
        for (WsClient ws : clientMap.values()) {
            if (ws.isOpen()) {
                WsLogUtil.e("请勿重复连接, key = " + ws.getWsKey());
                continue;
            }
            if (ws.isClosed()) {
                WsLogUtil.e("不能重复使用已关闭的WebSocket, key = " + ws.getWsKey());
                continue;
            }
            ws.connect();
        }
    }

    public WsClient getDefault() {
        return getWsClient(DEFAULT_WEBSOCKET);
    }

    public WsClient getWsClient(String wsKey) {
        if (!clientMap.containsKey(wsKey)) {
            WsLogUtil.e(NO_INIT + wsKey);
            return null;
        }
        return clientMap.get(wsKey);
    }

    /**
     * 使用默认WebSocket发送信息
     *
     * @param message 消息
     */
    public void send(String message) {
        send(DEFAULT_WEBSOCKET, message);
    }

    /**
     * 使用指定的WebSocket发送信息
     *
     * @param wsKey   webSocket Key
     * @param message 消息
     */
    public void send(String wsKey, String message) {
        getWsClient(wsKey).send(message);
    }

    /**
     * 使用默认WebSocket发送ping
     */
    public void sendPing() {
        send(DEFAULT_WEBSOCKET);
    }

    /**
     * 使用指定的WebSocket发送ping
     *
     * @param wsKey webSocket Key
     */
    public void sendPing(String wsKey) {
        getWsClient(wsKey).sendPing();
    }

    /**
     * @return 默认WebSocket是否连接
     */
    public boolean isConnected() {
        if (getDefault() == null) {
            return false;
        }
        return isConnected(DEFAULT_WEBSOCKET);
    }

    /**
     * @param wsKey wsKey
     * @return WebSocket是否连接
     */
    public boolean isConnected(String wsKey) {
        if (getWsClient(wsKey) == null) {
            return false;
        }
        return getWsClient(wsKey).isOpen();
    }

    /**
     * 断开默认WebSocket连接
     */
    public void disConnect() {
        disConnect(DEFAULT_WEBSOCKET);
    }

    /**
     * 断开指定WebSocket
     *
     * @param wsKey wsKey
     */
    public void disConnect(String wsKey) {
        getWsClient(wsKey).close();
    }

    /**
     * 销毁资源, 销毁后Websocket需要重新初始化
     */
    public void destroy() {
        // 关闭连接
        for (WsClient ws : clientMap.values()) {
            try {
                ws.closeBlocking();
                ThreadUtils.cancel(ws.getTask());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clientMap.clear();
        // 解除广播
        unRegisterNetworkChangedReceiver();
    }

}
