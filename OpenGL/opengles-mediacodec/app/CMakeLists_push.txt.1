# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)
# 添加 define  -DNO_CRYPTO

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# 指定源文件目录
AUX_SOURCE_DIRECTORY(${CMAKE_SOURCE_DIR}/src/main/cpp SRC_LIST)
AUX_SOURCE_DIRECTORY(${CMAKE_SOURCE_DIR}/src/main/cpp/librtmp RTMP_LIST)
# FILE(GLOB SRC_LIST "src/main/cpp/*.cpp")

add_library(
        # Sets the name of the library.
        live-push
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${SRC_LIST}
        ${RTMP_LIST}
)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        # 链接额外的 ffmpeg 的编译
        live-push
        # 链接 android ndk 自带的一些库
        android
        # 链接 OpenSLES
        OpenSLES
        # Links the target library to the log library
        # included in the NDK.
        log)