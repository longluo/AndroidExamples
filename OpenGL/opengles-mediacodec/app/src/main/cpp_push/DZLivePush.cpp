//
// Created by hcDarren on 2019/7/7.
//
#include "DZLivePush.h"

DZLivePush::DZLivePush(const char *liveUrl, DZJNICall *pJniCall) {
    this->liveUrl = (char *) malloc(strlen(liveUrl) + 1);
    strcpy(this->liveUrl, liveUrl);
    this->pJniCall = pJniCall;
    pPacketQueue = new DZPacketQueue();
}

DZLivePush::~DZLivePush() {
    if (liveUrl != NULL) {
        free(liveUrl);
        liveUrl = NULL;
    }

    if (pPacketQueue != NULL) {
        delete (pPacketQueue);
        pPacketQueue = NULL;
    }
}

void *initConnectFun(void *context) {
    DZLivePush *pLivePush = (DZLivePush *) context;
    // 1. 创建 RTMP
    pLivePush->pRtmp = RTMP_Alloc();
    // 2. 初始化
    RTMP_Init(pLivePush->pRtmp);
    // 3. 设置参数，连接的超时时间等
    pLivePush->pRtmp->Link.timeout = 10;
    pLivePush->pRtmp->Link.lFlags |= RTMP_LF_LIVE;
    RTMP_SetupURL(pLivePush->pRtmp, pLivePush->liveUrl);
    RTMP_EnableWrite(pLivePush->pRtmp);
    // 开始连接
    if (!RTMP_Connect(pLivePush->pRtmp, NULL)) {
        // 回调到 java 层
        LOGE("rtmp connect error");
        pLivePush->pJniCall->callConnectError(THREAD_CHILD, INIT_RTMP_CONNECT_ERROR_CODE,
                "rtmp connect error");
        return (void *) INIT_RTMP_CONNECT_ERROR_CODE;
    }

    if (!RTMP_ConnectStream(pLivePush->pRtmp, 0)) {
        // 回调到 java 层
        LOGE("rtmp connect stream error");
        pLivePush->pJniCall->callConnectError(THREAD_CHILD, INIT_RTMP_CONNECT_STREAM_ERROR_CODE,
                "rtmp connect stream error");
        return (void *) INIT_RTMP_CONNECT_STREAM_ERROR_CODE;
    }

    LOGE("rtmp connect success");
    pLivePush->pJniCall->callConnectSuccess(THREAD_CHILD);
    return 0;
}

void DZLivePush::initConnect() {
    // 怎么连接？
    pthread_t initConnectTid;
    pthread_create(&initConnectTid, NULL, initConnectFun, this);
    pthread_detach(initConnectTid);
}
