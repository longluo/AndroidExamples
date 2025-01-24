//
// Created by Anshul Saraf on 28/06/22.
//

#include "ImageSteg.h"
#include "opencv2/imgcodecs.hpp"
#include "logger.h"
#include "constants.h"

namespace ChopperJNI {

    using namespace cv;
    int Encode(const std::string& source, std::string blob, const std::string& out) {
        ALOGD("input : %s", source.c_str());
        Mat image = imread(source);
        if(image.empty()) {
            ALOGE("Image Error");
            return RETURN_ERROR;
        }
        char ch;
        int idx = 0, length = blob.size();

        if( (long long )(image.rows * image.cols * 3) + PADDING < length*8 )
            return RETURN_ERROR_INSUFFICIENT_SPACE;

        // reads the first char from the file
        ch = blob[idx];
        // contains information about which bit of char to work on
        int bit_count = 0;
        // to check whether file has ended
        bool last_null_char = false;
        // to check if the whole message is encoded or not
        bool encoded = false;

        /*
        To hide text into images. We are taking one char (8 bits) and each of the 8 bits are stored
        in the Least Significant Bits (LSB) of the pixel values (Red,Green,Blue).
        We are manipulating bits in such way that changing LSB of the pixel values will not make a huge difference.
        The image will still look similiar to the naked eye.
        */

        for(int row=0; row < image.rows; row++) {
            for(int col=0; col < image.cols; col++) {
                for(int color=0; color < 3; color++) {

                    // stores the pixel details
                    Vec3b pixel = image.at<Vec3b>(Point(row,col));

                    // if bit is 1 : change LSB of present color value to 1.
                    // if bit is 0 : change LSB of present color value to 0.
                    if( CHECK_BIT(ch,7-bit_count) )
                        pixel.val[color] |= 1;
                    else
                        pixel.val[color] &= ~1;

                    // update the image with the changed pixel values
                    image.at<Vec3b>(Point(row,col)) = pixel;

                    // increment bit_count to work on next bit
                    bit_count++;

                    // if last_null_char is true and bit_count is 8, then our message is successfully encode.
                    if(last_null_char && bit_count == 8) {
                        encoded  = true;
                        goto OUT;
                    }

                    // if bit_count is 8 we pick the next char from the file and work on it
                    if(bit_count == 8) {
                        bit_count = 0;
                        ch = blob[++idx];

                        // if EndOfFile(EOF) is encountered insert NULL char to the image
                        if(idx == length) {
                            last_null_char = true;
                            ch = '\0';
                        }
                    }

                }
            }
        }
        OUT:;

        // whole message was not encoded
        if(!encoded) {
            ALOGE("Message too big. Try with larger image.");
            return RETURN_ERROR_INSUFFICIENT_SPACE;
        }

        // Writes the stegnographic image
        imwrite(out,image);

        return RETURN_SUCCESS;
    }

    std::string Decode(const std::string& source) {
        Mat image = imread(source);
        if(image.empty()) {
            ALOGE("Image Error");
            return "";
        }

        std::string ret;
        char ch=0;
        // contains information about which bit of char to work on
        int bit_count = 0;

        /*
        To extract the message from the image, we will iterate through the pixels and extract the LSB of
        the pixel values (RGB) and this way we can get our message.
        */
        for(int row=0; row < image.rows; row++) {
            for(int col=0; col < image.cols; col++) {
                for(int color=0; color < 3; color++) {

                    // stores the pixel details
                    Vec3b pixel = image.at<Vec3b>(Point(row,col));

                    // manipulate char bits according to the LSB of pixel values
                    if(CHECK_BIT(pixel.val[color],0))
                        ch |= 1;

                    // increment bit_count to work on next bit
                    bit_count++;

                    // bit_count is 8, that means we got our char from the encoded image
                    if(bit_count == 8) {

                        // NULL char is encountered
                        if(ch == '\0')
                            goto OUT;

                        bit_count = 0;
                        ret += ch;
                        ch = 0;
                    }
                    else {
                        ch = ch << 1;
                    }
                }
            }
        }
        OUT:;

        return ret;
    }

    jint m_Encode(JNIEnv *env, jobject object, jstring s, jstring b, jstring o) {
        return Encode(jString2String(env, s), jString2String(env, b), jString2String(env, o));
    }

    jstring m_Decode(JNIEnv *env, jobject object, jstring s) {
        return env->NewStringUTF( Decode(jString2String(env, s)).c_str() );
    }

}
