package com.longluo.dragdemo;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DragTest2Activity extends AppCompatActivity {

    private static final String TAG = DragTest2Activity.class.getSimpleName();

    DragView mDragView;

    MoveView mMoveView;

    StreamStatusView mStatusView;

    FloatBallView mFloatBallView;

    RemovableView mRemovableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_test2);

//        mDragView = findViewById(R.id.iv_drag);
//        mDragView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "DragView click");
//
//                if (!mDragView.isDrag()) {
//                    Toast.makeText(DragTest2Activity.this, "mDragView Click", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        mMoveView = findViewById(R.id.move_view);
//
//        mMoveView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "MoveView click");
//
//                Toast.makeText(DragTest2Activity.this, "MoveView Click", Toast.LENGTH_LONG).show();
//            }
//        });

        mStatusView = findViewById(R.id.stream_status);

        mStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "StatusView click");

                Toast.makeText(DragTest2Activity.this, "StatusView Click", Toast.LENGTH_LONG).show();
            }
        });

        mFloatBallView = findViewById(R.id.float_ball);
        mFloatBallView.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FloatBall click");

                Toast.makeText(DragTest2Activity.this, "StatusView Click", Toast.LENGTH_LONG).show();
            }
        });

        mRemovableView = findViewById(R.id.removable_view);
    }
}
