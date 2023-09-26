title: Camera Filter 美颜相机的实现
date: 2017-11-22 22:36:23
tags: android camera filter recoeder
comments: true
---

# 前言

当你需要实现Camera 添加滤镜的预览以及录制的时候，那么你可以看过来了！<br>
在这里会给你讲解基于opengl的Camera滤镜的实现。

之前已经介绍过了视频编解码以及渲染的相关概念了，详见
[Android视频滤镜添加硬解码方案](http://blog.csdn.net/zhs4430169/article/details/76502217)

当然如果你只是想要简单的进行Camera预览以及拍照的话，推荐google的CameraView，简单兼容性好。
[轻松玩转Camera,使用CameraView来拍照，修改CameraView 实现自定义拍照分辨率](http://blog.csdn.net/zhs4430169/article/details/76502217)

如果需要Camera的预览以及录制，并不需要添加滤镜特效的话，那么原生API，Camera + MediaRecorder就能搞定。<br>
推荐开源项目[opencamera](https://opencamera.sourceforge.io/)

对于Camera Filter的添加以及MP4视屏Filter的添加，自己这里写了一个开源项目
[PinFilter](https://github.com/DiskyZhs/PinFilter)
你可以直接使用。

<!--more-->

# 原理

Camera的setPreview设置Preview输出的时候可以是SurfaceHolder也可以是SurfaceTexture，必须使用SurfaceTexture，
只有通过SurfaceTexture获取到Camera输出数据然后一方面通过OpenGl对输出的图像进行加工然后渲染，这一部分的实现
可以参考Grafika的MoviePlayer的实现。
当你需要录制添加滤镜的视频的时候，你必须在同一个Opengl环境下也就是EGl对象下面将SurfaceTexture中的texture通过
opengl渲染然后送给MediaCodec（Encoder）进行编码然后送入MediaMuxer重新生成MP4视频文件。


为什么不用SurfaceHolder作为输出？
SurfaceHolder是关联SurfaceView，Camera的数据会直接送给SurfaceView的Surface
进行渲染，无法取得图像数据进行再处理。


为什么不在Camera的onPreviewFrame(byte[] data, Camera camera)取得图像数据，然后进行处理图像添加滤镜再送去渲染？
原因是效率问题，onPreviewFrame回调中取得的是Yuv格式的图像，而送入渲染的无论是SurfaceView还是Canvas绘制等
方法需要的是RGB的颜色格式，这也就意味着要实现这些必须先将Yuv颜色格式转化为RGB，然后将RGB进行添加滤镜处理
然后渲染，无论这部分转化处理操作是Java实现还是利用Jni，C来实现（事实上快一点）都会比较慢，同时这些处理是
跑在Cpu上面的，内存里面的，也就意味着更大的资源消耗。
而利用Opengl对SurfaceTexture中的图像数据进行处理采用GLSL语言，这一切都是在gpu中完成的，所以这无疑是效率
最高的方式。

为什么使用MediaCodec以及MediaMuxer？
因为MediaCodec是属于硬件编解码，效率最高！但是需要Android 4.3+支持，当然你也可以使用ffmpeg等软编码方案来
替代后半部分的录制功能！

SurfaceView，GlSurfaceView还是TextureView作为Camera Preview的渲染显示？
首先由于需要支持Opengl所以GlSurfaceView无疑是最优先的选择，但是TextureView拥有View的平移旋转等特性，更关
键的是在ScrollView，ViewPager等控件中只有TextureView才不会出现滑动黑边等问题，所以TextureView才是视频渲染
最好的载体。
为了使TextureView支持OpenGl，就必须仿照GlSurfaceView自己进行创建EGL，创建GlThread进行渲染，具体的实现可以
参考
[GlTextureView](https://github.com/DiskyZhs/PinFilter/blob/master/libRecorderEditor/src/main/java/com/pinssible/librecorder/view/GLTextureView.java)
[gles](https://github.com/DiskyZhs/PinFilter/tree/master/libRecorderEditor/src/main/java/com/pinssible/librecorder/gles)
或者开源项目[android-openGL-canvas](https://github.com/ChillingVan/android-openGL-canvas)

# 实现

无论是GlSurfaceView还是GlTextureView的核心都是实现Render接口，即
```java
         void onSurfaceCreated();
         void onSurfaceChanged();
         void onDrawFrame();
```

也就是在**onSurfaceCreated**与**onSurfaceChanged**进行初始化操作，在**onDrawFrame**
中完成对与图像的绘制，滤镜添加以及视频录制功能的实现。

话不多说，直接上Render的代码核心代码，要想了解全部的代码可以去
[PinFilter](https://github.com/DiskyZhs/PinFilter) 找。


# 最后
感觉非常抱歉，本来是将相关的核心的代码贴出来，逐一进行分析的，实际写的时候发现，要贴出的代码过多，而以我苍白的语言
怎么也解释不完全，雾里看花水里看月！所以推荐大家还是分析代码吧！关键部分都有注释！

Camera这一部分的实现实际上是对开源项目
[kickflip-android-sdk](https://github.com/Kickflip/kickflip-android-sdk)以及
[android-gpuimage](https://github.com/CyberAgent/android-gpuimage)的借鉴和再封装，
特别感谢！