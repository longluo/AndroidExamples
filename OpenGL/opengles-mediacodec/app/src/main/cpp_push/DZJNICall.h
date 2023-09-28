//
// Created by hcDarren on 2019/6/16.
//

#ifndef MUSICPLAYER_DZJNICALL_H
#define MUSICPLAYER_DZJNICALL_H

#include <jni.h>

enum ThreadMode{
    THREAD_CHILD,THREAD_MAIN
};

class DZJNICall {
public:
    JavaVM *javaVM;
    JNIEnv *jniEnv;
    jmethodID jConnectErrorMid;
    jmethodID jConnectSuccessMid;
    jobject jLiveObj;
public:
    DZJNICall(JavaVM *javaVM, JNIEnv *jniEnv, jobject jLiveObj);
    ~DZJNICall();

public:
    void callConnectError(ThreadMode threadMode,int code, char *msg);

    void callConnectSuccess(ThreadMode mode);



};


#endif //MUSICPLAYER_DZJNICALL_H
