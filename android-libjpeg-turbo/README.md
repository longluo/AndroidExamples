# Sample libjpeg-turbo integration for Android

Sample project for integrating libjpeg-turbo as a library on Android.

Since AWT is not supported on Android, only a subset of the TurboJPEG API can be used.

# Binaries
`.aar` binaries for integrating libjpeg-turbo directly into your app are available on the [releases](https://github.com/hfhchan/libjpeg-turbo-android-sample/releases) page.

## Installation
1. Unzip the file `libjpeg-turbo-2.1.1-***-release.aar.zip`.
2. Place the `.aar` file into the `app/libs` folder.
3. Add the following snippet to the `dependencies` section of `app/build.gradle`:  
   `implementation files('libs/libjpeg-turbo-2.1.1-***-release.aar.zip')`

# Licenses
This sample is licensed under the MIT License.

libjpeg-turbo is covered by three compatible BSD-style open source licenses.  Please see the license text for libjpeg-turbo [here](libjpeg-turbo/src/main/cpp/libjpeg-turbo-2.1.1/LICENSE.md).

Sample image Bird Shore Animal licensed under the CC0 license from https://negativespace.co/bird-shore-animal/.


https://github.com/hfhchan/libjpeg-turbo-android-sample



