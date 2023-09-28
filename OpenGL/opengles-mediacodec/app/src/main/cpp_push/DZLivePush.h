//
// Created by hcDarren on 2019/7/7.
//

#ifndef LIVEPUSH_DZLIVEPUSH_H
#define LIVEPUSH_DZLIVEPUSH_H

#include "DZJNICall.h"
#include "DZPacketQueue.h"
#include <malloc.h>
#include <string.h>
#include "DZConstDefine.h"
extern "C"{
#include "librtmp/rtmp.h"
}
class DZLivePush {
public:
    DZJNICall *pJniCall = NULL;
    char *liveUrl = NULL;
    DZPacketQueue *pPacketQueue;
    RTMP *pRtmp = NULL;
public:
    DZLivePush(const char *liveUrl, DZJNICall *pJniCall);

    ~DZLivePush();

    void initConnect();
};


#endif //LIVEPUSH_DZLIVEPUSH_H
