export ANDROID_SDK_ROOT=/Users/$USER/Library/Android/sdk
export ANDROID_NDK=${ANDROID_SDK_ROOT}/ndk/23.1.7779620
# export ANDROID_NATIVE_API_LEVEL=24

rm -rf build/

declare -a arr=("arm64-v8a" "x86" "x86_64" "armeabi-v7a")

mkdir android_libs

for i in "${arr[@]}"
do
   echo "building for $i"

   mkdir -p build
   cd build/

   cmake \
   	-DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake \
   	-DANDROID_TOOLCHAIN=clang++ \
   	-DANDROID_ABI=$i \
   	-D CMAKE_BUILD_TYPE=Debug \
   	-D ANDROID_NATIVE_API_LEVEL=24 \
   	-D WITH_CUDA=OFF \
   	-D WITH_MATLAB=OFF \
   	-D BUILD_ANDROID_EXAMPLES=OFF \
   	-D BUILD_DOCS=OFF \
   	-D BUILD_PERF_TESTS=OFF \
   	-D BUILD_TESTS=OFF \
   	-D ANDROID_STL=c++_shared \
   	-D BUILD_SHARED_LIBS=ON \
   	-D BUILD_opencv_objdetect=OFF \
   	-D BUILD_opencv_video=OFF \
   	-D BUILD_opencv_videoio=OFF \
   	-D BUILD_opencv_features2d=OFF \
   	-D BUILD_opencv_flann=OFF \
   	-D BUILD_opencv_highgui=ON \
   	-D BUILD_opencv_ml=ON \
   	-D BUILD_opencv_photo=OFF \
   	-D BUILD_opencv_python=OFF \
   	-D BUILD_opencv_shape=OFF \
   	-D BUILD_opencv_stitching=OFF \
   	-D BUILD_opencv_superres=OFF \
   	-D BUILD_opencv_ts=OFF \
   	-D BUILD_opencv_videostab=OFF \
   	-DBUILD_TESTING=OFF \
   	-DBUILD_PERF_TESTS=OFF \
   	-DBUILD_TESTS=OFF \
   	-DCMAKE_INSTALL_PREFIX:PATH=/Users/anshulsaraf/Documents/Personal/opencv-android/opencv/build/${i} \
   	-DBUILD_ANDROID_PROJECTS=OFF ..

   # make -j nproc
   	make
	make install

	cd ..
	cp -R build/${i}/ android_libs/${i}

	rm -rf build/

done
