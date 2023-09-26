# Camera2MediaCodec
Android Video Recording options

There are three ways to record a video in Android from Camera2:
1) Attach MediaRecorder or MediaCodec surfaces to the Camera and render an image from the camera onto it
2) Render preview onto GLSurfaceView and use the same stream in MediaCodec letting shader convert data
3) Attach ImageReader to Camera2 and manually feed each frame into MediaCodec

The first approach is the most reliable and stable way to record. There are only a couple of drawbacks:
- you can not preprocess frames (e.g. add effects to the video before saving it in a video file)
- you have to attach a separate stream to the camera hardware. For most cases, it is absolutely fine, except the only one where you have to attach more than HAL can resolve. ([For me it was a problem with Samsung S21 and image analysis](https://stackoverflow.com/questions/67239752/android-camera2-imagereader-is-not-working-with-mediarecorder-on-samsung-s21-fam)
You can find an example of that approach in [Google's Camera2Video sample](https://github.com/android/camera-samples/blob/master/Camera2Video)

The second approach is the most customizable one. Having full control on the image before rendering it to preview and to MediaCodec, giving you the ability to apply transformations using shaders before rendering it. It is the most efficient way to transform an image. Also, it is dealing with colors, so there is a small chance that colors from the camera will be improperly encoded into the video. The drawback of that approach is the amount of code that you have to maintain on your own.
For a detailed example please see CameraFragmentGlSurface and grafika folder in the sources. The code is a mix of existing [Google's Camera2Video sample](https://github.com/android/camera-samples/blob/master/Camera2Video) and [Google's CameraCaptureActivity from experimental repo Grafika](https://github.com/google/grafika/blob/master/app/src/main/java/com/android/grafika/CameraCaptureActivity.java) to support Camera2 API.
The sample might help anyone willing to apply their video effects using OpenGL and then preview it to the user and record video into the file.

The third approach involves ImageReader class and works with YUV420 images. You have to read the Image object, convert it to ByteArray, then manually feed it into MediaCodec. I was not able to feed converted ByteArray into MediaCodec w/o losing colors, so I had to convert Image to NV21 then to Bitmap then back to NV21, and feed MediaCodec. It is not optimal completely, it is working fine on Pixels, but on Samsung S21, the codec does not encode colors properly (or maybe I decode YUV420 incorrectly). Anyway, you may see that the main drawback of that approach is the inability to guarantee that the image will be converted properly and the phone will have a sufficient codec. In case you decide to take that route look over the internet for better conversion algorithms that are implemented in C++.
For detailed example please look at CameraFragmentImageReader

In conclusion. If you do not need to modify the video stream then go with the first approach. If you are instagram or tiktok then start with the seconds (and dive into native ASAP) and good luck with supporting that code. The third approach is not reliable, at least for now.
