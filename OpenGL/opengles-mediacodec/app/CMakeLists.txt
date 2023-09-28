# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

# 需要引入我们头文件,以这个配置的目录为基准
include_directories(src/main/jniLibs/include)

AUX_SOURCE_DIRECTORY(${CMAKE_SOURCE_DIR}/src/main/cpp SRC_LIST)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# 添加共享库搜索路径
LINK_DIRECTORIES(${CMAKE_SOURCE_DIR}/src/main/jniLibs/arm64-v8a)

add_library(
        # Sets the name of the library.
        video-record
        # Sets the library as a shared library.
        SHARED
        ${SRC_LIST}
)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        # 链接额外的 ffmpeg 的编译
        video-record
        # 编解码(最重要的库)
        avcodec
        # 滤镜特效处理库
        avfilter
        # 封装格式处理库
        avformat
        # 工具库(大部分库都需要这个库的支持)
        avutil
        # 后期处理
        postproc
        # 音频采样数据格式转换库
        swresample
        # 视频像素数据格式转换
        swscale
        music-player
        # 链接 android ndk 自带的一些库
        android
        # 链接 OpenSLES
        OpenSLES
        # Links the target library to the log library
        # included in the NDK.
        log)