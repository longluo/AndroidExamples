# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("chopper")

# Need to create the .dylib and .jnilib files in order to run JUnit tests
# https://sureshjoshi.com/mobile/android-junit-native-libraries
#if (APPLE)
#    # Ensure jni.h is found
#
#    set(M_JAVA_HOME "~/Library/Java/JavaVirtualMachines/corretto-1.8.0_312/Contents/Home")
#
#    set(JAVA_AWT_LIBRARY "${M_JAVA_HOME}/lib/libjawt.so")
#    set(JAVA_JVM_LIBRARY "${M_JAVA_HOME}/lib/server/libjvm.so")
#    set(JAVA_INCLUDE_PATH2 "${M_JAVA_HOME}/include/darwin")
#    set(JAVA_AWT_INCLUDE_PATH "${M_JAVA_HOME}/include")
#    set(JAVA_INCLUDE_PATH "${M_JAVA_HOME}/include")
#
#    find_package(JNI REQUIRED)
#    include_directories(${JAVA_INCLUDE_PATH})
#endif()

# configure opencv.
set(OPENCV_DIRS ${CMAKE_SOURCE_DIR}/../../../../third_party/opencv)

include_directories(${OPENCV_DIRS}/include)

set(
        # List variable name
        opencv_libs_names
        # Values in the list
        opencv_core opencv_dnn opencv_highgui opencv_imgcodecs opencv_imgproc opencv_ml
)

foreach (opencv_libs_name ${opencv_libs_names})
    add_library(
            ${opencv_libs_name}
            SHARED
            IMPORTED)
    set_target_properties(
            ${opencv_libs_name}
            PROPERTIES
            IMPORTED_LOCATION
            ${OPENCV_DIRS}/lib/${ANDROID_ABI}/lib${opencv_libs_name}.so)
endforeach ()

# Automatically all files in a directory to a target
file (GLOB_RECURSE CHOPPER_SRCS CONFIGURE_DEPENDS
    "src/*.cpp"
    "src/*.h"
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        chopper

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${CHOPPER_SRCS})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        chopper

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}

        #opencv
        ${opencv_libs_names}
)
