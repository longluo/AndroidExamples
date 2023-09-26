package pfg.com.screenproc;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

/**
 * Created by FPENG3 on 2018/7/30.
 */

public class OpenGLVideoPlayer extends Activity implements View.OnClickListener{

    private final static String TAG = "OpenGLVideoPlayer";

    VideoGLSurfaceView mSurfaceView;
    VideoGLRenderer mRenderer;
    Button btn_record;
    private static final String VIDEO_FILE_PATH = Environment.getExternalStorageDirectory()+"/"+"test.mp4";
    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        mSurfaceView = (VideoGLSurfaceView) findViewById(R.id.surface_view);

        mRenderer = new VideoGLRenderer(this, mSurfaceView, VIDEO_FILE_PATH);
        mSurfaceView.setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
        btn_record = (Button) findViewById(R.id.btn_start_record);
        btn_record.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
        if(isRecording) {
            btn_record.setText("Start Record");
            mSurfaceView.shutdown();
            isRecording = !isRecording;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        isRecording = !isRecording;
        if(isRecording) {
            btn_record.setText("Stop Record");
            mSurfaceView.startRecord(true);
        } else {
            btn_record.setText("Start Record");
            mSurfaceView.stopRecord();
        }
    }
}
