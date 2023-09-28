package com.longluo.gldemo.livepush.mcamera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.longluo.gldemo.R;
import com.longluo.gldemo.livepush.camera.widget.CameraFocusView;
import com.longluo.gldemo.livepush.mcamera.widget.MyCameraView;


public class MyCameraActivity extends AppCompatActivity {
    private MyCameraView mCameraView;
    CameraFocusView cameraFocusView;

    @Override
    protected void onResume() {
        super.onResume();
//        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mCameraView.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycamera_render);
        mCameraView = (MyCameraView) findViewById(R.id._mysurface_view);
        cameraFocusView = findViewById(R.id.camera_focus_view);

        mCameraView.setOnFocusListener(new MyCameraView.FocusListener() {
            @Override
            public void beginFocus(int x, int y) {
                cameraFocusView.beginFocus(x, y);
            }

            @Override
            public void endFocus() {
                cameraFocusView.endFocus(true);
            }
        });

    }

    @Override
    protected void onDestroy() {
        mCameraView.onDestroy();
        super.onDestroy();
    }

}