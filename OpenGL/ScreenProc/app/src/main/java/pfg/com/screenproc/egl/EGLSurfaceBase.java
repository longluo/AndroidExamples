package pfg.com.screenproc.egl;

import android.opengl.EGL14;
import android.opengl.EGLSurface;

/**
 * Created by FPENG3 on 2018/7/27.
 */

public abstract class EGLSurfaceBase {

    EGLCore mEglCore;
    EGLSurface mEglSurface = EGL14.EGL_NO_SURFACE;
    private int mWidth = -1;
    private int mHeight = -1;

    public EGLSurfaceBase(EGLCore eglCore) {
        mEglCore = eglCore;
    }

    public EGLSurface createWindowSurface(Object surface) {
        mEglSurface = mEglCore.createWindowSurface(surface);
        return mEglSurface;
    }

    public EGLSurface createOffscreenSurface(int width, int height) {
        if (mEglSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        mEglSurface = mEglCore.createOffscreenSurface(width, height);
        mWidth = width;
        mHeight = height;
        return mEglSurface;
    }

    public void makeCurrent() {
        mEglCore.makeCurrent(mEglSurface);
    }

    public void makeCurrentReadFrom(EGLSurfaceBase readSurface) {
        mEglCore.makeCurrent(mEglSurface, readSurface.mEglSurface);
    }

    public boolean swapBuffers() {
        return mEglCore.swapBuffers(mEglSurface);
    }

    public int getWidth() {
        if(mWidth < 0) {
            return mEglCore.querySurface(mEglSurface, EGL14.EGL_WIDTH);
        }
        return mWidth;
    }

    public int getHeight() {
        if(mHeight < 0) {
            return mEglCore.querySurface(mEglSurface, EGL14.EGL_HEIGHT);
        }
        return mHeight;
    }

    public void releaseEglSurface() {
        mEglCore.releaseSurface(mEglSurface);
        mEglSurface = EGL14.EGL_NO_SURFACE;
        mWidth = mHeight = -1;
    }

    public void setPresentationTime(long nsecs) {
        mEglCore.setPresentationTime(mEglSurface, nsecs);
    }

}
