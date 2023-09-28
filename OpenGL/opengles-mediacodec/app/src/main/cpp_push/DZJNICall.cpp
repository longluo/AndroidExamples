//
// Created by hcDarren on 2019/6/16.
//

#include "DZJNICall.h"
#include "DZConstDefine.h"

DZJNICall::DZJNICall(JavaVM *javaVM, JNIEnv *jniEnv, jobject jLiveObj) {
    this->javaVM = javaVM;
    this->jniEnv = jniEnv;
    this->jLiveObj = jniEnv->NewGlobalRef(jLiveObj);

    jclass jPlayerClass = jniEnv->GetObjectClass(jLiveObj);
    jConnectErrorMid = jniEnv->GetMethodID(jPlayerClass, "onConnectError", "(ILjava/lang/String;)V");
    jConnectSuccessMid = jniEnv->GetMethodID(jPlayerClass, "onConnectSuccess", "()V");
}

DZJNICall::~DZJNICall() {
    jniEnv->DeleteGlobalRef(jLiveObj);
}

void DZJNICall::callConnectError(ThreadMode threadMode, int code, char *msg) {
    // 子线程用不了主线程 jniEnv （native 线程）
    // 子线程是不共享 jniEnv ，他们有自己所独有的
    if (threadMode == THREAD_MAIN) {
        jstring jMsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jLiveObj, jConnectErrorMid, code, jMsg);
        jniEnv->DeleteLocalRef(jMsg);
    } else if (threadMode == THREAD_CHILD) {
        // 获取当前线程的 JNIEnv， 通过 JavaVM
        JNIEnv *env;
        if (javaVM->AttachCurrentThread(&env, 0) != JNI_OK) {
            LOGE("get child thread jniEnv error!");
            return;
        }

        jstring jMsg = env->NewStringUTF(msg);
        env->CallVoidMethod(jLiveObj, jConnectErrorMid, code, jMsg);
        env->DeleteLocalRef(jMsg);

        javaVM->DetachCurrentThread();
    }
}

/**
 * 回调到 java 层告诉准备好了
 * @param threadMode
 */
void DZJNICall::callConnectSuccess(ThreadMode threadMode) {
    // 子线程用不了主线程 jniEnv （native 线程）
    // 子线程是不共享 jniEnv ，他们有自己所独有的
    if (threadMode == THREAD_MAIN) {
        jniEnv->CallVoidMethod(jLiveObj, jConnectSuccessMid);
    } else if (threadMode == THREAD_CHILD) {
        // 获取当前线程的 JNIEnv， 通过 JavaVM
        JNIEnv *env;
        if (javaVM->AttachCurrentThread(&env, 0) != JNI_OK) {
            LOGE("get child thread jniEnv error!");
            return;
        }
        env->CallVoidMethod(jLiveObj, jConnectSuccessMid);
        javaVM->DetachCurrentThread();
    }
}
