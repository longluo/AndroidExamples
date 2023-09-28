package com.longluo.gldemo.livepush.mrecord;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.longluo.gldemo.R;
import com.longluo.gldemo.livepush.camera.widget.CameraFocusView;
import com.longluo.gldemo.livepush.camera.widget.RecordProgressButton;
import com.longluo.gldemo.livepush.mcamera.widget.MyCameraView;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = RecordActivity.class.getSimpleName();

    private MyCameraView mCameraView;
    private DefaultVideoRecorder mVideoRecorder;
    private CameraFocusView mFocusView;
    private RecordProgressButton mRecordButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mycamera_render);

        mCameraView = findViewById(R.id._mysurface_view);
        mFocusView = findViewById(R.id.camera_focus_view);
        mRecordButton = findViewById(R.id.record);
        mRecordButton.setMaxProgress(60 * 1000);// 最大60秒

//        mVideoRecorder.startRecord();

        mRecordButton.setOnRecordListener(new RecordProgressButton.RecordListener() {
            @Override
            public void onStart() {
                String outPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/live_pusher.mp4";
                String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yue.mp3";

                mVideoRecorder = new DefaultVideoRecorder(RecordActivity.this, mCameraView.getEglContext());
                mVideoRecorder.initMediaParams(audioPath, outPath, 720, 1280);

                // getTextureId获取会有延后，所以需要在这里设置
                int textureId = mCameraView.getTextureId();
                Log.e(TAG, "onStart，textureId=" + textureId);

                mVideoRecorder.setRenderId(textureId);
                mVideoRecorder.setRecordInfoListener(new BaseVideoRecorder.RecordInfoListener() {
                    @Override
                    public void onTime(long times) {
                        mRecordButton.setCurrentProgress((int) times);
                    }
                });

                mVideoRecorder.startRecord();
            }

            @Override
            public void onEnd() {
                mVideoRecorder.stopRecord();
            }
        });

        mCameraView.setOnFocusListener(new MyCameraView.FocusListener() {
                                           @Override
                                           public void beginFocus(int x, int y) {
                                               mFocusView.beginFocus(x, y);
                                           }

                                           @Override
                                           public void endFocus() {
                                               mFocusView.endFocus(true);
                                           }
                                       }
        );
    }
}
