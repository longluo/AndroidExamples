package pfg.com.screenproc;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pfg.com.screenproc.util.MyLog;

/**
 * Created by FPENG3 on 2018/8/8.
 */

public class VideoDialActivity extends Activity {

    private static final String TAG = "VideoDialActivity";

    private GLSurfaceView mSurfaceView;
    private Button mBtn;
    VideoDialRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video_dial);
        mSurfaceView = (GLSurfaceView) findViewById(R.id.video_dial_surface_view);
        mBtn = (Button) findViewById(R.id.btn_start_record);
        mBtn.setVisibility(View.GONE);

        mRenderer = new VideoDialRenderer();
        mSurfaceView.setRenderer(mRenderer);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }
}
