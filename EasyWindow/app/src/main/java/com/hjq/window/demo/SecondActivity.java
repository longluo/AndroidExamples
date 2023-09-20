package com.hjq.window.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.window.EasyWindow;
import com.hjq.window.draggable.MovingDraggable;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SecondActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_second);

        findViewById(R.id.btn_main_float_ball).setOnClickListener(this);
        findViewById(R.id.btn_main_lock).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.btn_main_float_ball) {

            EasyWindow.with(this)
                    .setContentView(R.layout.float_ball)
                    .setAnimStyle(R.style.TopAnimStyle)
                    .setDraggable(new MovingDraggable())
                    .show();

        } else if (viewId == R.id.btn_main_lock) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_lock_screen)
                    .setAnimStyle(R.style.TopAnimStyle)
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }

}
