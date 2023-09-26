package pfg.com.screenproc;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by FPENG3 on 2018/8/3.
 */

public class FBOActivity extends Activity implements View.OnClickListener{
    VideoGLSurfaceView mSurfaceView;
    FBORenderer mRenderer;
    Button btn_record;

    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        mSurfaceView = (VideoGLSurfaceView) findViewById(R.id.surface_view);
        mRenderer = new FBORenderer(this, mSurfaceView);

        mSurfaceView.setRenderer(mRenderer);

        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

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
            mSurfaceView.startRecord(false);
        } else {
            btn_record.setText("Start Record");
            mSurfaceView.stopRecord();
        }
    }
}
