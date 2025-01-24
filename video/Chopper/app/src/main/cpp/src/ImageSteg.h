//
// Created by Anshul Saraf on 28/06/22.
//

#ifndef CHOPPER_IMAGESTEG_H
#define CHOPPER_IMAGESTEG_H

#include <jni.h>
#include <string>

#define PADDING 100LL
#define CHECK_BIT(var,pos) (((var)>>(pos)) & 1)

#define RETURN_SUCCESS 1
#define RETURN_ERROR -1
#define RETURN_ERROR_INSUFFICIENT_SPACE -2

namespace ChopperJNI{

    int Encode(const std::string& source, std::string blob, const std::string& out);

    std::string Decode(const std::string& source);

    jint m_Encode(JNIEnv *env, jobject object, jstring s, jstring b, jstring o);

    jstring m_Decode(JNIEnv *env, jobject object, jstring s);

    static const char *image_steg_class = "com/projectdelta/chopper/util/cipher/ImageSteganography";

    static JNINativeMethod image_steg_methods[] = {
            {"nativeEncode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void *) m_Encode},
            {"nativeDecode", "(Ljava/lang/String;)Ljava/lang/String;", (void *) m_Decode}
    };

}

#endif //CHOPPER_IMAGESTEG_H
