package com.longluo.dragdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DragTest1Activity extends AppCompatActivity {

    private static final String TAG = DragTest1Activity.class.getSimpleName();

    DragView mDragView;

    MoveView mMoveView;

    StreamStatusView mStatusView;

    RemovableView mRemovableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_test1);

//        mDragView = findViewById(R.id.iv_drag);
//        mDragView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "DragView click");
//
//                if (!mDragView.isDrag()) {
//                    Toast.makeText(DragTest1Activity.this, "mDragView Click", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        mMoveView = findViewById(R.id.move_view);
        mMoveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MoveView click");

                Toast.makeText(DragTest1Activity.this, "MoveView Click", Toast.LENGTH_LONG).show();
            }
        });

        mStatusView = findViewById(R.id.status_view);
        mStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "StatusView click");

                Toast.makeText(DragTest1Activity.this, "StatusView Click", Toast.LENGTH_LONG).show();
            }
        });

        mRemovableView = findViewById(R.id.removable_view);
    }
}
