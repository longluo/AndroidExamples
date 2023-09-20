package com.longluo.demo.progressbar;

import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.longluo.demo.R;

import java.io.IOException;

public class ProgressBarActivity extends AppCompatActivity {

    private ImageView mLoadingView;

    private ProgressBar mProgressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progressbar);

        try {
            initView();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initView() throws IOException {
        mLoadingView = findViewById(R.id.iv_loading);
        mProgressBar = findViewById(R.id.pb_loading);

        showLoading(mLoadingView);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {

                    progressStatus += 1;

                    handler.post(new Runnable() {

                        public void run() {
                            mProgressBar.setProgress(progressStatus);
                        }
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showLoading(ImageView loading) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(getResources(), R.drawable.loading);
            Drawable decodedDrawable = ImageDecoder.decodeDrawable(source);
            if (decodedDrawable instanceof AnimatedImageDrawable) {
                AnimatedImageDrawable animatedDrawable = (AnimatedImageDrawable) decodedDrawable;
                animatedDrawable.setRepeatCount(-1);
                animatedDrawable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        super.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        super.onAnimationEnd(drawable);
                    }
                });

                loading.setImageDrawable(animatedDrawable);
                animatedDrawable.start();
            } else {
                loading.setImageDrawable(decodedDrawable);
            }
        }
    }
}
