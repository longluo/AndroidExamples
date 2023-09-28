package com.eurigo.websocketlib;

import com.eurigo.websocketlib.util.ThreadUtils;
import com.eurigo.websocketlib.util.WsLogUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author Eurigo
 * Created on 2022/3/30 15:07
 * desc   :
 */
public class ReConnectTask extends ThreadUtils.SimpleTask<Void> {

    private final WsClient wsClient;
    private int reConnectCount;
    private final long reConnectInterval;

    private int count = 1;

    public ReConnectTask(WsClient wsClient) {
        this.wsClient = wsClient;
        reConnectCount = wsClient.getReConnectCount();
        reConnectInterval = wsClient.getReConnectInterval();
    }

    @Override
    public Void doInBackground() throws Throwable {
        WsLogUtil.e("执行第" + count + "次重连");
        if (wsClient.isOpen()) {
            cancel();
            return null;
        }
        wsClient.reconnectBlocking();
        // 每次执行任务，重连次数递减，直到为0不再发起重连
        reConnectCount--;
        count++;
        return null;
    }

    @Override
    public void onSuccess(Void result) {
        if (reConnectCount == 0) {
            cancel();
        }
    }

    @Override
    protected void onDone() {
        super.onDone();
        wsClient.setReconnectTaskRun(false);
        WsLogUtil.e("重连任务执行完毕");
    }

    public void execute() {
        if (!WsManager.getInstance().isNetworkAvailable()) {
            WsLogUtil.e("网络不可用, 不执行重连");
            cancel();
            return;
        }
        if (wsClient.isOpen()) {
            WsLogUtil.e("已连接成功");
            cancel();
            return;
        }
        if (wsClient.isReconnectTaskRun()) {
            WsLogUtil.e("重连任务已在运行中");
            return;
        }
        wsClient.setReconnectTaskRun(true);
        ThreadUtils.executeByCachedAtFixRate(this, reConnectInterval, TimeUnit.MILLISECONDS);
    }
}
