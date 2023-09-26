package pfg.com.screenproc;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import pfg.com.screenproc.egl.EGLCore;
import pfg.com.screenproc.egl.WindowSurface;
import pfg.com.screenproc.objects.Square;
import pfg.com.screenproc.objects.Triangle;
import pfg.com.screenproc.util.MyLog;

/**
 * Created by FPENG3 on 2018/8/15.
 */

public class TextureViewGLActivity extends Activity {

    TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view_gl);
        mTextureView = (TextureView) findViewById(R.id.glTextureView);

        RendererThread renderer = new RendererThread();

        mTextureView.setSurfaceTextureListener(renderer);
        renderer.setPriority(Thread.MIN_PRIORITY+1);
        renderer.start();
    }

    private class RendererThread extends Thread implements TextureView.SurfaceTextureListener {

        private static final String TAG = "GLRenderer";
        private Object mLock = new Object();
        private SurfaceTexture mSurfaceTexture = null;
        private int mWidth, mHeight;

        @Override
        public void run() {
            SurfaceTexture surfaceTexture;

            // 这里之前有问题，范围扩大了，包括了windowSurface.release()前面的代码了，
            // 导致destory时无法获得锁导致主线程卡死，出现ANR。
            // log详见有道云笔记ANR目录
            synchronized (mLock) {
                if ((surfaceTexture = mSurfaceTexture) == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Surface surface = new Surface(mSurfaceTexture);
            EGLCore eglCore = new EGLCore(null, EGLCore.FLAG_TRY_GLES3 | EGLCore.FLAG_RECORDABLE);
            WindowSurface windowSurface = new WindowSurface(eglCore, surface, true);
            windowSurface.makeCurrent();
            float squareCoords[] = {
                    -0.25f,  0.5f, 0f,   // top left
                    -0.25f, -0.5f, 0f,   // bottom left
                     0.25f, -0.5f, 0f,   // bottom right
                     0.25f,  0.5f, 0f}; // top right

            float[] color = {1f, 0f, 0f, 1.0f};
            Square square = new Square(squareCoords, color);
            float clearColor = 0f;
            float xpos = -1.25f;
            while (true) {

                synchronized (mLock) {
                    if (mSurfaceTexture == null) {
                        break;
                    }
                }

                GLES30.glClearColor(clearColor, clearColor, clearColor, 1f);
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
                float[] model = new float[16];
                Matrix.setIdentityM(model, 0);
                Matrix.translateM(model, 0, xpos, 0, 0);

                square.draw(model);
                windowSurface.swapBuffers();

                clearColor += 0.05f;
                if (1.0f - clearColor < 0.000001f) {
                    clearColor = 0f;
                }

                xpos += 0.05f;
                if (1.25f - xpos < 0.000001f) {
                    MyLog.logd(TAG, "It had happened");
                    xpos = -1.25f;
                }

                try {
                    sleep(300);
                } catch (InterruptedException e) {

                }

            }
            windowSurface.release();
            eglCore.release();
        }


        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            mWidth = width;
            mHeight = height;
            synchronized (mLock) {
                mSurfaceTexture = surfaceTexture;
                mLock.notify();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            // main thread
            synchronized (mLock) {
                mSurfaceTexture = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }
}
