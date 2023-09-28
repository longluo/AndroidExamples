# Android Websocket [![](https://jitpack.io/v/eurigo/WebsocketUtils.svg)](https://jitpack.io/#eurigo/WebsocketUtils)

基于Java-websocket，在Android下的websocket。

* 具有断网重连和自动重连功能。

* 支持管理多个Websocket
* 支持搭建本地Websocket调试

---

### 快速使用

#### 1.在项目级 `build.gradle`添加：

```java
allprojects {
   repositories {
      maven { url 'https://jitpack.io' }
	}
}
```

#### 2.在app模块下的`build.gradle`文件中加入：

```java
dependencies {
    implementation 'com.github.eurigo:WebsocketUtils:1.1.72'
}
```

#### 3.在Manifest添加权限：

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

#### 4.初始化

```java
	// 构造一个默认WebSocket客户端
	WsClient wsClient = new WsClient.Builder()
          .setServerUrl(address)
          .setListener(new IWsListener() {
                    @Override
                    public void onConnected(WsClient client) {
                        // 连接成功
                    }

                    @Override
                    public void onDisconnect(WsClient client, DisConnectReason reason) {
                         // 连接断开
                    }

                    @Override
                    public void onMessage(WsClient client, String message) {
                         // 接收到消息
                    }

                    @Override
                    public void onPing(WsClient wsClient, Framedata frameData) {
                         // ping frame
                    }

                    @Override
                    public void onPong(WsClient client, Framedata frameData) {
                         // pong frame
                    }

                    @Override
                    public void onSendMessage(WsClient client, String message) {
                         // 发送数据
                    }
                })
          .setPingInterval(30)
          .build();
	// 初始化并启动连接
	WsManager.getInstance()
          .init(wsClient)
          .start();
```

#### 5.在onDestory销毁

```java
   @Override
    protected void onDestroy() {
        super.onDestroy();
        WsManager.getInstance().destroy();
    }
```

---

### WsManager API介绍

| WsManager.getInstance() |               说明                |
| :---------------------- | :-------------------------------: |
| isNetworkAvailable()    |           网络是否可用            |
| closeLog()              |    是否显示内部日志，默认true     |
| getDefault()            |        获取默认的websocket        |
| send()                  |   用（指定的）websocket发送消息   |
| sendPing()              | 用（指定的）websocket发送心跳ping |
| disconnect()            |    断开（指定的）websocket连接    |
| destroy()               |       销毁所有websocket资源       |

### WsClient 属性

| 属性                          |                        说明                         |
| ----------------------------- | :-------------------------------------------------: |
| serverUrl（必须）             |                     服务端地址                      |
| IWsListener（必须）           |                        回调                         |
| wsKey                         |  初始化时设置的标识，不设置，自动使用默认websocket  |
| draft                         |               Websocket协议，默认6455               |
| connectTimeout                |               连接超时时间，默认值：0               |
| pingInterval                  | 心跳时间，单位秒，默认60。小于等于0，则关闭心跳功能 |
| reConnectCount                |        重连次数，默认10，大于0才开启重连功能        |
| isReconnectTaskRun            |                是否正在执行重连任务                 |
| reConnectWhenNetworkAvailable |         网络可用时是否自动重连，默认值true          |
| httpHeaders                   |                  要使用的附加标头                   |

### 更多

请参考demo
