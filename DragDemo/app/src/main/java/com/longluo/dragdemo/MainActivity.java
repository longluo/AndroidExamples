package com.longluo.dragdemo;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private Button mBtnDragTest1;

    private Button mBtnDragTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mBtnDragTest1 = findViewById(R.id.btn_test1);
        mBtnDragTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DragTest1Activity.class);
                startActivity(intent);
            }
        });

        mBtnDragTest2 = findViewById(R.id.btn_test2);
        mBtnDragTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DragTest2Activity.class);
                startActivity(intent);
            }
        });
    }
}
