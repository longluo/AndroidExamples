package pfg.com.screenproc;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import pfg.com.screenproc.util.MyLog;

/**
 * Created by FPENG3 on 2018/8/14.
 */


// 问题：
// 为什么要加锁？

public class TextureViewCanvasActivity extends Activity {

    TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view_canvas);
        mTextureView = (TextureView) findViewById(R.id.canvasTextureView);
        RendererThread thread = new RendererThread();
        mTextureView.setSurfaceTextureListener(thread);
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class RendererThread extends Thread implements TextureView.SurfaceTextureListener {
        private static final String TAG = "CanvasRenderer";

        SurfaceTexture mSurfaceTexture;
        Object mLock = new Object();
        int mWidth, mHeight;

        @Override
        public void run() {
            // wait for surfacetexture

            synchronized (mLock) {
                if (mSurfaceTexture == null) { //这行代码为什么放在同步里面而不是放在外面？
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            while(true) {
                if(mSurfaceTexture == null) {
                    break;
                }

                doAnimation();
            }
        }

        private void doAnimation() {
            MyLog.logd(TAG, "doAnimation");
            final int BLOCK_WIDTH = 80;
            final int BLOCK_SPEED = 2;
            int clearColor = 0;
            int xpos = -BLOCK_WIDTH / 2;
            int xdir = BLOCK_SPEED;

            SurfaceTexture surfaceTexture;
            Surface surface;

            synchronized (mLock) {
                surfaceTexture = mSurfaceTexture;
                surface = new Surface(surfaceTexture);
            }

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            Canvas canvas = null;
            boolean partial = false;

            while(true) {
                Rect dirty = null;
                if (partial) {
                    // 仅仅为SurfaceTexture高度的1/4
                    dirty = new Rect(0, mHeight * 3 / 8, mWidth, mHeight * 5 / 8);
                }

                synchronized (mLock) {
                    if(mSurfaceTexture == null) {
                        break;
                    }
                }

                // 即使dirty为null，但是canvas不会为null,
                // dirty为null时canvas的width和height由谁决定==>等于surfaceTexture的width和height
                // dirty不为null时canvas的width和height又由谁决定==>还是等于surfaceTexture的width和height
                // 那么问题来了，dirty不为null时为什么canvas真正只画dirty区域呢？
                canvas = surface.lockCanvas(dirty);

                // partial为false，那么dirty就为null
                if(dirty == null) {
                    MyLog.logd(TAG, "===dirty is null!");
                    MyLog.logd(TAG, "canvas's width:"+canvas.getWidth()+" height:"+canvas.getHeight());
                    MyLog.logd(TAG, "surfaceTexture's width:"+mWidth+" height:"+mHeight);
                } else {
                    MyLog.logd(TAG, "***dirty is not null!");
                    MyLog.logd(TAG, "canvas's width:"+canvas.getWidth()+" height:"+canvas.getHeight());
                    MyLog.logd(TAG, "surfaceTexture's width:"+mWidth+" height:"+mHeight);
                }

                if (canvas == null) {
                    MyLog.logd(TAG, "canvas is null!");
                    break;
                }

                try {
                    canvas.drawRGB(clearColor, clearColor, clearColor);
                    MyLog.logd(TAG, "left="+xpos+" top="+mHeight / 4+" right="+xpos + BLOCK_WIDTH+" bottom="+mHeight * 3 / 4);
                    // 假如partial为true，dirty区域高度仅仅为SurfaceTexture高度的1/4，
                    // 而这里要描会区域高度为SurfaceTexture高度的1/2,所以有一部分是不会显示的。
                    canvas.drawRect(xpos, mHeight / 4, xpos + BLOCK_WIDTH, mHeight * 3 / 4, paint);

                } finally {
                    try {
                        surface.unlockCanvasAndPost(canvas);
                    } catch (IllegalArgumentException iae) {
                        break;
                    }
                }

                clearColor += 4;
                if(clearColor > 255) {
                    clearColor = 0;
                    partial = !partial;
                }

                xpos += xdir;
                if (xpos <= -BLOCK_WIDTH / 2 || xpos >= mWidth - BLOCK_WIDTH / 2) {
                    MyLog.logd(TAG, "change direction");
                    xdir = -xdir;
                }
                /*try {
                    sleep(500);
                } catch (InterruptedException e) {

                }*/
            }
            surface.release();
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            MyLog.logd(TAG, "onSurfaceTextureAvailable width:"+width+" height:"+height);
            mWidth = width;
            mHeight = height;
            synchronized (mLock) {
                mSurfaceTexture = surfaceTexture;
                mLock.notify();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            MyLog.logd(TAG, "onSurfaceTextureSizeChanged width:"+width+" height:"+height);
            mWidth = width;
            mHeight = width;
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            // main thread
            synchronized (mLock) {
                mSurfaceTexture.release();
                mSurfaceTexture = null;
            }

            MyLog.logd(TAG, "onSurfaceTextureDestroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }

}
