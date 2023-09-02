package com.longluo.dragdemo;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    DragView mDragView;

    MoveView mMoveView;

    StreamStatusView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDragView = findViewById(R.id.iv_drag);
        mDragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "DragView click");

                if (!mDragView.isDrag()) {
                    Toast.makeText(MainActivity.this, "mDragView Click", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMoveView = findViewById(R.id.move_view);

        mMoveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MoveView click");

                Toast.makeText(MainActivity.this, "MoveView Click", Toast.LENGTH_LONG).show();
            }
        });

        mStatusView = findViewById(R.id.status_view);

        mStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "StatusView click");

                Toast.makeText(MainActivity.this, "StatusView Click", Toast.LENGTH_LONG).show();
            }
        });
    }
}
