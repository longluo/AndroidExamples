package com.longluo.gldemo.livepush.opengl;

import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * 扩展 GLSurfaceView ，暴露 EGLContext
 */
public class BaseGLSurfaceView extends GLSurfaceView {
    /**
     * EGL环境上下文
     */
    protected EGLContext mEglContext;

    public BaseGLSurfaceView(Context context) {
        this(context, null);
    }

    public BaseGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 利用 setEGLContextFactory 这种扩展方式把 EGLContext 暴露出去
        setEGLContextFactory(new EGLContextFactory() {
            @Override
            public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
                int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
                mEglContext = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
                return mEglContext;
            }

            @Override
            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                if (!egl.eglDestroyContext(display, context)) {
                    Log.e("BaseGLSurfaceView", "display:" + display + " context: " + context);
                }
            }
        });
    }

    /**
     * 通过此方法可以获取 EGL环境上下文，可用于共享渲染同一个纹理
     *
     * @return EGLContext
     */
    public EGLContext getEglContext() {
        return mEglContext;
    }
}
