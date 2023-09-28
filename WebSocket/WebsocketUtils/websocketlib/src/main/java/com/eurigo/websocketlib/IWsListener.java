package com.eurigo.websocketlib;

import org.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;

/**
 * @author Eurigo
 * Created on 2022/3/29 10:07
 * desc   :
 */
public interface IWsListener {

    /**
     * 连接成功
     *
     * @param client 客户端
     */
    void onConnected(WsClient client);

    /**
     * 连接已断开
     *
     * @param client 客户端
     * @param reason 断开原因
     */
    void onDisconnect(WsClient client, DisConnectReason reason);

    /**
     * 连接失败,调用后会调用onClose
     *
     * @param client 客户端
     * @param ex     异常
     */
    default void onError(WsClient client, Exception ex) {

    }

    /**
     * 接收到消息
     *
     * @param client  客户端
     * @param message 消息
     */
    void onMessage(WsClient client, String message);

    /**
     * 接收到消息
     *
     * @param client 客户端
     * @param bytes  消息
     */
    default void onMessage(WsClient client, ByteBuffer bytes) {

    }

    /**
     * 响应服务端ping时触发
     * 此默认实现将发送一个 pong 以响应收到的 ping。 pong 帧将具有与 ping 帧相同的有效负载
     *
     * @param wsClient  客户端
     * @param frameData 帧数据
     */
    void onPing(WsClient wsClient, Framedata frameData);

    /**
     * 响应服务端pong时触发
     *
     * @param client    客户端
     * @param frameData 帧数据
     */
    void onPong(WsClient client, Framedata frameData);

    /**
     * 发送消息成功
     *
     * @param client  客户端
     * @param message 发送的消息
     */
    void onSendMessage(WsClient client, String message);

    default void onSendMessage(WsClient client, byte[] message) {

    }
}
