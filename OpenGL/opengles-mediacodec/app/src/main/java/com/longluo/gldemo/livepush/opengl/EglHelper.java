package com.longluo.gldemo.livepush.opengl;

import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;

import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglHelper {
    private static final String TAG = "EglHelper";
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;
    private EGLConfig mEGLConfig;

    public EGLConfig getEGLConfig() {
        return mEGLConfig;
    }

    public EGL10 getEgl() {
        return mEgl;
    }

    public void initCreateEgl(Surface surface, EGLContext eglContext) {
        /*
         * Get an EGL instance
         */
        mEgl = (EGL10) EGLContext.getEGL();

        /*
         * Get to the default display.
         */
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        /*
         * We can now initialize EGL for that display
         */
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        int[] attributes = new int[]{
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL10.EGL_NONE
        };

        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, attributes, null, 1, num_config)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException(
                    "No configs match configSpec");
        }

        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!mEgl.eglChooseConfig(mEglDisplay, attributes, configs, numConfigs,
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        mEGLConfig = configs[0];
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};

        if (eglContext != null) {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], eglContext, attrib_list);
        } else {
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, attrib_list);
        }

        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], surface, null);
        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public boolean swapBuffers() {
        if (mEgl != null) {
            return mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
        }
        Log.e(TAG, "egl is null");
        return false;
    }

    public EGLContext getEglContext() {
        return mEglContext;
    }

    public void destroy() {
        if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            mEgl.eglTerminate(mEglDisplay);
        }
    }
}
