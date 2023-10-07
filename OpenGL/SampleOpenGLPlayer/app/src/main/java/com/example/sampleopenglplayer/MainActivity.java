package com.example.sampleopenglplayer;

import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Surface;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {

    private GLSurfaceView glSurfaceView;

    private SampleGLRenderer glRenderer;

    private SampleMediaCodec sampleMediaCodec;

    private SurfaceTexture surfaceTexture;

    private Surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sampleMediaCodec = new SampleMediaCodec();
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glRenderer = new SampleGLRenderer();
        glSurfaceView.setRenderer(glRenderer);
        surfaceTexture = null;

        setContentView(glSurfaceView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_play) {
            // Create SurfaceTexture and set callback for frame update event triggered by
            // MediaCodec releaseOutputBuffer()
            if (surfaceTexture == null) {
                surfaceTexture = new SurfaceTexture(glRenderer.getTextureHandle());
                surfaceTexture.setOnFrameAvailableListener(this);
                surface = new Surface(surfaceTexture);
            }
            sampleMediaCodec.play(this, surface, "sdcard/Movies/h264_720p.mp4");
        } else if (id == R.id.action_screenshot) {
            String fileName = "sdcard/Pictures/screenshot.jpg";
            glRenderer.screenshot(fileName);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // SurfaceTexture.updateTexImage() cannot be called in non OpenGL context.
        glRenderer.updateTexture(surfaceTexture);
    }
}
