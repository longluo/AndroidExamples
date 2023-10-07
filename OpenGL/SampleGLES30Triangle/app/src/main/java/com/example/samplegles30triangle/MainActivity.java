package com.example.samplegles30triangle;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    private GLSurfaceView glSurfaceView;
    private SampleGLES30Renderer glRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Check GLES version */
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        final boolean supportES3 = ci.reqGlEsVersion >= 0x30000;

        if (!supportES3) {
            Log.e(SampleGLES30Triangle.class.getName(), "GLES30 is not supported!");
            return;
        }

        /* Create GLSurfaceWiew object and configure it to ES 3.x */
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(3);
        glRenderer = new SampleGLES30Renderer(getResources());
        glSurfaceView.setRenderer(glRenderer);

        /* Set GLSurfaceView as view of application*/
        setContentView(glSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
