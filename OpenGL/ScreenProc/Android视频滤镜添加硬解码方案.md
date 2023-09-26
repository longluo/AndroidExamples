title: Android 滤镜添加硬解码
date: 2017-11-21 15:03:33
tags: Android 音视频 滤镜 硬解码
comments: true
---

由于工作的需求，研究过了一段时间的Android 的音视频播放渲染以及编辑方面的知识，这里就自己一些浅薄的了解对所了解做一个简单的介绍和记录，如有不对的地方请指正!同时也会记录下硬件解码的情况下完成滤镜的添加。

这里以MP4格式的视频作为介绍，因为在实际的移动开发中，传输播放渲染的基本都是MP4文件。

这里先介绍一些基础概念，当理解了这些，再去查看以及学习关于音视频的一些著名开源项目，如Grafika，ExoPlayer，IJKPlayer，GPUImage等开源项目学习会更加方便！

安利一下自己的库，基于MediaCodec的硬件解码的添加滤镜的库
[PinFilter](https://github.com/DiskyZhs/PinFilter)
实现了滤镜的添加，预览，播放，录制功能。

<!--more-->

## 基础概念##
### 音视轨，编解码，软硬解码###
VideoTrack（视轨），AudioTrack（音轨）分别对应视频文件的画面和声音的数据存储，一般来说常见的MP4一般都是一条VideoTrack，一条AudioTrack。当然有一些左右声道，背景音或存在多条AudioTrack的情况存在。
<br/>
无论是声音信息还是图像信息储存在MP4中都是经过**编码（encode）**的，可以理解为按照一定规则压缩过的。所以你要进行MP4的播放，就需要将图像信息和音频信息取出来，然后经过**解码（decode）**还原成原始的可供渲染和播放的音视频单元。
<br/>
以上就是编解码的基础概念，事实上音视频数据在MP4中是以Box的形式存储的，感兴趣的可以自己搜索学习。而MP4无论播放还是处理音视轨都是单独分开的，所以这就需要你将音轨和视轨从MP4文件中分离出来，而这个分离的过程就称之为**解复用**，所以同理，将音视轨重新合在一起的过程就称之为**复用**。
<br/>
所以可以看出来，我要进行视频播放需要做的就是解复用取得音视轨，然后分别进行解码还原成原始可显示的数据，接着去渲染和播放，解复用 -》解码 -》播放。
我们如果要进行视频编辑就需要，解复用 -》解码 -》处理数据-》编码-》复用生成MP4。
<br/>
而使用代码运行，将Video/AudioTrack解码的方式称为**软解码**，软解码最出名的就是开源项目FFMPEG，基本上所有的视频领域都有它的影子。
同样，由于软解码/编码是运行在CPU中的，所以无论是在cpu，内存的使用率还是解码效率上面都可能存在一些限制和瓶颈。因此，ffmpeg的实际运用中的优化也是很重要的。
<br/>
事实上，除了软件码以外，手机，PC等视频播放设备上都存在**硬件解码器**（一种寄存器）来专门做视频的播放工作，我们把调用硬件解码器进行解码的方式称为**硬解码**。硬解码无疑在效率上更加高效，但是由于各大厂商的缘故，对于硬件解码器都有自己的调整，所以在硬解码的时候，各种机器的适配就显得尤为重要。
<br/>
实际情况下，真正成熟的视频SDK都是软硬结合同时在适配方面做了一些处理的。
这里只介绍Android 平台下的硬解码情况下，视频播放，编辑以及滤镜的添加。
着重说明VideoTrack的处理。
<br/>

### Extractor,Codec,Muxer###

Android 事实上提供了现成的API来进行视频编解码和复用的工作。这里只是对这些API做个简单介绍。

 - MediaExtractor  （Android 4.1+），用来进行MP4的解复用工作。
 
	` getTrackCount()` 与` getTrackFormat(index)` 来获取音视轨的数量和信息
	` selectTrack()` 函数选择当前音视轨

 - MediaCodec （Android 4.1+），用来进行MP4的解码/编码。
	 
	这是视频播放以及重新编辑的核心类，用来调用底层的硬件解码器来编解码。
	MediaCodec的设计实际上是生产者消费者模式。如下
	
	![这里写图片描述](http://img.blog.csdn.net/20171117112313637?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhzNDQzMDE2OQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


   通过` dequeueInputBuffer()` 函数来取得可被使用的寄存器Buffer，然后通过
   ` Extractor.readSampleData()`  将数据送入解码器，最后通过`    dequeueOutputBufferr()` 来取出解码/编码后的数据。由于是生产消费者模式，  故你需要不断的查询Buffer的状态直至整个的编解码结束。

  视频的编解码都是有MediaCodec这个类完成的，你可以创建不同的decoder和encoder。
  
  Codec作为Decoder（解码器）的时候，输出可以为Buffer或者Surface（后面会讲到，Surface其实也就是一个Raw data buffer），Surface作为输出的情况下，解码后的数据会直接送到Surface中，一般直接进行显示，这种情况下，你无法取得Buffer中的数据

	同上Codec作为Encoder（编码器）的时候，输入数据源也可以为Buffer或者Surface。

 - MediaMuxer  （Android 4.3+），用来进行重新生成MP4。
   
   ` addTrack()` 添加音视轨， ` writeSampleData()` 函数向音视轨中写入编码好的数据。

 -  所以硬解码的情况下：
	 
	视频播放：MediaExtractor（解复用） -》MediaCodec（Decoder解码） -》
	数据处理（如果添加滤镜）-》渲染显示

	视频编辑：MediaExtractor（解复用） -》MediaCodec（Decoder解码） -》
	数据处理（如果添加滤镜）-》MediaCodec（Encoder编码） -》 MediaMuxer（复用，生成MP4）
	
 -  只是视频播放的，Android 4.1以上的设备就可以采取硬解码方案，
   如果视频编辑，Android 4.3 以上的设备才可行。
   目前Android 4.3以上设备覆盖率已达到90%+，硬解码方案是比较可行的。
   同样是编解码，在高分辨率，高码率的情况下，硬解码有明显的速度优势。


### Frame，关键帧，H.264/AVC###

**H.264** 是视频编解码器标准，也就是说视频数据是以h264标准进行压缩的，
无论是本地视频还是网络数据流传输都是H264标准。

**帧（Frame）**就是video显示传输的数据单元，一帧可以理解为显示的完整的一副图像的数据。

**FPS（Frame Pre Second）**每秒显示帧数，一般人眼只能识别24帧左右，也就说只要FPS大于24一般画面看起来就是流畅的。

Extractor，Codec都是以一帧数据作为单位进行处理。当然帧与帧也有区别，根据h264标准，分别分为I帧，P帧，B帧。

I帧是一种自带全部信息的独立帧，无需参考其他图像便可独立进行解码，可以简单理解为一张静态画面。视频序列中的第一个帧始终都是I帧，因为它是关键帧。

P帧又称帧间预测编码帧，需要参考前面的I帧才能进行编码。表示的是当前帧画面与前一帧（前一帧可能是I帧也可能是P帧）的差别。解码时需要用之前缓存的画面叠加上本帧定义的差别，生成最终画面。

B帧又称双向预测编码帧，也就是B帧记录的是本帧与前后帧的差别。也就是说要解码B帧，不仅要取得之前的缓存画面，还要解码之后的画面，通过前后画面的与本帧数据的叠加取得最终的画面。

**关键帧** 及IDR帧，就是I帧，IDR帧的作用是立刻刷新,使错误不致传播,从IDR帧开始,重新算一个新的序列开始编码。所以在IDR帧之后的所有帧都不能引用任何IDR帧之前的帧的内容。


### Surface，SurfaceView，SurfaceTexure，TextureView###

 - Surface  
   surface可以简单的理解为装有当前显示frame buffer，内存中的一段绘图缓冲区。每个Activity每个Layer都有一个Surface。

 - SurfaceView
	 
	 SurfaceView内部自己管理生成一个Surface作为渲染，所有SurfaceView视频播放的View载体。
	 这样的好处是对这个Surface的渲染可以放到单独线程去做，渲染时可以有自己的GL context。这对于一些游戏、视频等性能相关的应用非常有益，因为它不会影响主线程对事件的响应。但它也有缺点，因为这个Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行平移，缩放等变换，也不能放在其它ViewGroup中，一些View中的特性也无法使用。
	 
   ![这里写图片描述](http://img.blog.csdn.net/20171117164305836?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhzNDQzMDE2OQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

 - GlSurfaceView
	
	GlSurfaceView作为SurfaceView的补充。它可以看作是SurfaceView的一种典型使用模式。在SurfaceView的基础上，它加入了EGL的管理，并自带了渲染线程。另外它定义了用户需要实现的Render接口，可以使用户调用OpenGl自定义渲染的过程。

 - SurfaceTexture
  
   SurfaceTexture对图像流的处理并不直接显示，而是转为GL外部纹理，因此可用于图像流数据的二次处理（如Camera滤镜，桌面特效等）。
   SurfaceTexture从图像流（来自Camera预览，视频解码，GL绘制场景等）中获得帧数据，当调用updateTexImage()时，根据内容流中最近的图像更新SurfaceTexture对应的GL纹理对象，接下来，就可以像操作普通GL纹理一样操作它了。
   SurfaceTexture.OnFrameAvailableListener用于让SurfaceTexture的使用者知道有新数据到来。
   SurfaceTexture中的attachToGLContext()和detachToGLContext()可以让多个GL context共享同一个内容源。
	SurfaceTexture对象可以在任何线程上创建。 updateTexImage（）只能在包含纹理对象的OpenGL ES上下文的线程上调用。 在任意线程上调用frame-available回调函数，不与updateTexImage（）在同一线程上出现。

 - TextureView
	 
	SurfaceView由于使用的是独立的绘图层，并且使用独立的线程去进行绘制。所以SurfaceView不能进行Transition，Rotation，Scale等变换，这就导致一个问题SurfaceView在滑动的时候，SurfaceView的刷新由于不受主线程控制导致SurfaceView在滑动的时候会出现黑边的情况。
	为了应对这种情况，所以Android 4.0 google推出了TextureView进行视频播放或者游戏渲染。
	和SurfaceView不同，它不会在WMS中单独创建窗口，而是作为View hierachy中的一个普通View，因此可以和其它普通View一样进行移动，旋转，缩放，动画等变化。值得注意的是TextureView必须在硬件加速的窗口中。


### Yuv，OpenGl，GLSL###

 - Yuv，RGB
 
   Yuv是一种颜色格式，VideoTrack 经过 MediaCodec，decode解码出来的图像的格式就是Yuv420格式，事实上Yuv420格式也分为YV12，NV21等4种，具体可以参考
[详解YUV数据格式](http://blog.csdn.net/beyond_cn/article/details/12998247)
	
  Codec解码出来的yuv格式其实4种都存在，具体信息可以在输出的MediaFormat中查到，造成这种差异也和Android厂商有一定的关系。
  
   RGB（ARGB8888或者RGB565）是被设备渲染所需要的颜色格式，所以当你Codec解码出来的数据需要转化为RGB然后进行显示.（当然你如果直接将Surface设置为Codec的输出的话，产生的yuv的数据会被系统直接转化为RGB格式(一般直接在GPU中直接处理了，更加高效)）
<br/>

 - OpenGl，Egl，GLSL

	这些都会涉及到计算机图形学的一些概念，个人知道的也很肤浅，只能简单的说说自己的理解。

	OpenGL是个与硬件无关的软件接口，定义了一系列图形渲染的操作，而嵌入式设备（Android 设备）都支持Opengl进行图形操作，同时Android sdk也为我们封装并实现了了OpenGl接口，这也就意味着我们可以通过调用Android原生API来自己修改预定义图像的渲染。（这些操作与修改是发生在GPU显存当中的，并不会大量的消耗cpu与内存，大大的优化了视频播放的体验），OpenGL ES是opengl 在Android平台上使用的版本

  Embedded Graphics Library (EGL)是连接OpenGL ES和本地窗口系统的接口，由于OpenGL ES是跨平台的，引入EGL就是为了屏蔽不同平台上的区别。本地窗口相关的API提供了访问本地窗口系统的接口，EGL提供了创建渲染表面，接下来OpenGL ES就可以在这个渲染表面上绘制，同时提供了图形上下文，用来进行状态管理。

   ![这里写图片描述](http://img.blog.csdn.net/20171117151212193?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhzNDQzMDE2OQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
	
   GLSL(GLslang)是官方的opengl着色语言的简称(OpenGL Shading Language)。     GLSL是类似于C/C++的高级语言，适用于一部分显卡。使用GLSL，你能够编写一些短小的程序，称为着色器(shader)，这些着色器在GPU上运行。
简单的理解就是GLSL就是加载在GPU中操作图形显示的操作语言。

   简单理解就是EGL是关联硬件设备，给opengl的运行创建环境的同时对opengl的生命周期进行管理。GLSL是被opengl调用运行在gpu中的图像渲染处理的程序语言，用来处理图像旋转，添加滤镜等一系列渲染特效。

   Opengl是一门复杂的知识体系，深入研究使用需要大量的时间和专业的人才，当然我们如果只是进行一些滤镜的开发等肤浅的应用并不需要了解的那么多，后续会向大家介绍这部分开发。

   最后大概知识路径如下

![这里写图片描述](http://img.blog.csdn.net/20171117191322676?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhzNDQzMDE2OQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

   