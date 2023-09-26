package pfg.com.screenproc;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.graphics.SurfaceTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by FPENG3 on 2018/8/8.
 */

public class VideoDialRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES30.glClearColor(1.0f, 0f, 0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
}
