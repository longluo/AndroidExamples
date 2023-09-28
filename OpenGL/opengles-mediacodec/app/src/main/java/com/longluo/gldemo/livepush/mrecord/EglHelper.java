package com.longluo.gldemo.livepush.mrecord;

import android.opengl.EGL14;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * An EGL helper class.
 * 可参照：https://www.jianshu.com/p/d5ff1ff4ee2a，比较详细
 */
public class EglHelper {

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;

    /**
     * 用给的mEGLContext和mEGLSurface创建egl
     */
    public void eglSetup(EGLContext context, Surface surface) {
        try {
//            mEglContext = mEGLContext;
            // 获取egl实例
            mEgl = (EGL10) EGLContext.getEGL();

            // 返回默认的显示设备.
            mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            // 需判断是否成功获取EGLDisplay
            if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }

            // mEglDisplay进行初始化
            int[] version = new int[2];
            if (!mEgl.eglInitialize(mEglDisplay, version)) {
                throw new RuntimeException("eglInitialize failed");
            }
            // 已经将OpenGL ES的输出与设备的屏幕桥接起来，需指定一些配置项
            int[] attribList = {
                    EGL10.EGL_RED_SIZE, 8,//颜色缓冲区红色分量的位数为8
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,//模板缓冲区
//                    EGL10.EGL_RENDERABLE_TYPE, 4,//egl版本  2.0
                    EGL10.EGL_NONE //并以EGL_NONE标识结尾信息
            };
            // EGL_RENDERABLE_TYPE注释：
//        android不支持OpenGL ES 2.x，因此在EGL10中某些相关常量参数只能用手写硬编码代替，
//        例如EGL14.EGL_CONTEXT_CLIENT_VERSION以及EGL14.EGL_OPENGL_ES2_BIT等等
//        public static final int EGL14.EGL_OPENGL_ES2_BIT                 = 0x0004;

            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            // eglChooseConfig()方法得到配置选项信息
            if (!mEgl.eglChooseConfig(mEglDisplay, attribList, configs, configs.length, numConfigs)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            // 如果没有配置的Config
            if (numConfigs[0] < 0) {
                throw new RuntimeException("Unable to find any matching EGL config");
            }
            mEglConfig = configs[0];
            // 对应的Config不存在
            if (mEglConfig == null) {
                throw new RuntimeException("eglChooseConfig returned null");
            }

            // 指定OpenGL ES2版本
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL10.EGL_NONE
            };
            // 创建EGLContext上下文
            if (mEglContext != null)
                mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, mEglContext,
                        attrib_list);
            else
                mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT,
                        attrib_list);
            //需要检测Context是否存在
            if (mEglContext == EGL10.EGL_NO_CONTEXT) {
                throw new RuntimeException("Failed to create EGL context");
            }
            // 创建可显示的Surface
            // 第三个参数在EGL10中只支持SurfaceHolder和SurfaceTexture
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], surface,
                    null);
            if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
            }

        /*
         每个线程都需要绑定一个上下文，才可以开始执行OpenGL ES指令，
         我们可以通过eglMakeCurrent来为该线程绑定Surface和Context
         */
            if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
                Log.e("Egl", "eglSetup: " + Integer.toHexString(mEgl.eglGetError()));
                throw new RuntimeException("egl makeCurrent failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示渲染的surface
     *
     * @return the EGL error code from eglSwapBuffers.
     */
    public boolean swapBuffer() {
        try {
            if (mEgl != null) {
                return mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 销毁mEglDisplay和当前线程绑定
     */
    public void destroySurface() {
        if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
            // 需要将线程跟EGL环境解除绑定
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            // 要销毁EGLSurface
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;
            // 接着清理掉上下文环境
            mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            mEglContext = null;
            // 最终关闭掉显示设备
            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;
        }
    }

    public EGLConfig getEglConfig() {
        return mEglConfig;
    }

    public EGLSurface getEglSurface() {
        return mEglSurface;
    }

    public EGLContext getEGLContext() {
        return mEglContext;
    }
}