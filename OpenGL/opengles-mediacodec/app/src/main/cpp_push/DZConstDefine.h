//
// Created by hcDarren on 2019/6/15.
//

#ifndef MUSICPLAYER_DZCONSTDEFINE_H
#define MUSICPLAYER_DZCONSTDEFINE_H

#include <android/log.h>

#define TAG "JNI_TAG"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

// ----------  错误码 start ----------
#define INIT_RTMP_CONNECT_ERROR_CODE -0x10
#define INIT_RTMP_CONNECT_STREAM_ERROR_CODE -0x11
// ----------  错误码 end ----------

#endif //MUSICPLAYER_DZCONSTDEFINE_H
