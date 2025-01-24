//
// Created by Anshul Saraf on 28/06/22.
//

#ifndef CHOPPER_CONSTANTS_H
#define CHOPPER_CONSTANTS_H

#include "opencv2/core/version.hpp"
#include <jni.h>


namespace ChopperJNI{

    static jfloat JniVersion(JNIEnv *env, jobject object) {
        return 1.4;
    }

    static jstring OpenCVVersion(JNIEnv *env, jobject object){
        return env->NewStringUTF(CV_VERSION);
    }

    static const char *constants = "com/projectdelta/chopper/util/Constants";

    static JNINativeMethod constants_methods[] = {
            {"nativeGetJniVersion", "()F", (void *) JniVersion},
            {"nativeGetOpenCVVersion", "()Ljava/lang/String;", (void *) OpenCVVersion}
    };

    static std::string jString2String(JNIEnv *env, jstring jStr) {
        if (!jStr)
            return "";

        const jclass stringClass = env->GetObjectClass(jStr);
        const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
        const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

        size_t length = (size_t) env->GetArrayLength(stringJbytes);
        jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

        std::string ret = std::string((char *)pBytes, length);
        env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

        env->DeleteLocalRef(stringJbytes);
        env->DeleteLocalRef(stringClass);
        return ret;
    }
}

#endif //CHOPPER_CONSTANTS_H
