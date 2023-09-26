package pfg.com.screenproc.egl;

import android.graphics.SurfaceTexture;
import android.view.Surface;

/**
 * Created by FPENG3 on 2018/7/27.
 */

public class WindowSurface extends EGLSurfaceBase {

    boolean releaseSurface;
    Surface mSurface;

    public WindowSurface(EGLCore eglCore, Surface surface, boolean releaseSurface) {
        super(eglCore);
        createWindowSurface(surface);
        mSurface = surface;
    }

    public WindowSurface(EGLCore eglCore, int width, int height) {
        super(eglCore);
        createOffscreenSurface(width, height);
    }

    public WindowSurface(EGLCore eglCore, SurfaceTexture surfaceTexture) {
        super(eglCore);
        createWindowSurface(surfaceTexture);
    }

    public void release() {
        releaseEglSurface();
        if(mSurface != null) {
            if(releaseSurface) {
                mSurface.release();
            }
            mSurface = null;
        }
    }

    public void recreate(EGLCore newEglCore) {
        if (mSurface == null) {
            throw new RuntimeException("not yet implemented for SurfaceTexture");
        }
        mEglCore = newEglCore;          // switch to new context
        createWindowSurface(mSurface);  // create new surface
    }
}
