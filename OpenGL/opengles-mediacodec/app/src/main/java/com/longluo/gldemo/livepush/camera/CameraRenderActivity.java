package com.longluo.gldemo.livepush.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.longluo.gldemo.R;
import com.longluo.gldemo.livepush.camera.widget.CameraView;


public class CameraRenderActivity extends AppCompatActivity {
    private CameraView mCameraView;

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mCameraView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mCameraView.onPause();
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_render);
        mCameraView = findViewById(R.id.surface_view);

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mCameraView.onDestroy();
//    }
}