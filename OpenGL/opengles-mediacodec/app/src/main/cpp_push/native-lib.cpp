#include <jni.h>
#include <string>
#include "DZLivePush.h"

DZLivePush *pLivePush = NULL;
DZJNICall *pJniCall = NULL;
JavaVM *pJavaVM = NULL;

// 重写 so 被加载时会调用的一个方法
// 小作业，去了解动态注册
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javaVM, void *reserved) {
    pJavaVM = javaVM;
    JNIEnv *env;
    if (javaVM->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_longluo_gldemo_livepush_LivePush_nativeInitConnect(JNIEnv *env, jobject instance, jstring liveUrl_) {
    const char *liveUrl = env->GetStringUTFChars(liveUrl_, 0);

    pJniCall = new DZJNICall(pJavaVM, env, instance);
    pLivePush = new DZLivePush(liveUrl, pJniCall);
    pLivePush->initConnect();

    env->ReleaseStringUTFChars(liveUrl_, liveUrl);
}