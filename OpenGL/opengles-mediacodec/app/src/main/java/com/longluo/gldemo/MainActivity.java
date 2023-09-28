package com.longluo.gldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.longluo.gldemo.livepush.camera.CameraRenderActivity;
import com.longluo.gldemo.livepush.picture.PictureActivity;

public class MainActivity extends AppCompatActivity {

    private Button mBtnCamera;

    private Button mBtnPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
        init();
    }

    private void initView() {
        mBtnCamera = findViewById(R.id.btn_camera);
        mBtnPicture = findViewById(R.id.btn_picture);
    }

    private void init() {
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraRenderActivity.class);
                startActivity(intent);
            }
        });

        mBtnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PictureActivity.class);
                startActivity(intent);
            }
        });
    }
}
