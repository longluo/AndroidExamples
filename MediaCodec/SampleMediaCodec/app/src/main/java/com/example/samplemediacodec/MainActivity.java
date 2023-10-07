package com.example.samplemediacodec;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button mBtnPlay;

    private SampleMediaCodec sampleMediaCodec;

    private SurfaceView videoSurfaceView;

    private SurfaceHolder surfaceHolder;

    private Surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sampleMediaCodec = new SampleMediaCodec();
        videoSurfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = videoSurfaceView.getHolder();
        surface = surfaceHolder.getSurface();

        mBtnPlay = findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleMediaCodec.play(MainActivity.this, surface);
            }
        });
    }
}
