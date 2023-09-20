package com.maciekjanusz.draglayoutdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maciekjanusz.draglayout.DragLayout;

public class DemoActivity extends AppCompatActivity implements DragLayout.DragListener {

    private boolean firstStart = true;
    private boolean firstFinish = true;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        DragLayout dragLayout = (DragLayout) findViewById(R.id.drag_layout);
        Log.i(DemoActivity.class.getSimpleName(), "Intercepting touch events? " + dragLayout.isIntercepting());
        dragLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DemoActivity.this, "Clickety click", Toast.LENGTH_SHORT).show();
            }
        });

        dragLayout.setDragListener(this);

        textView = (TextView) findViewById(R.id.text_view);
    }

    @Override
    public void onDragFinished(float x, float y) {
        if (firstFinish) {
            Toast.makeText(this, "And that's it.", Toast.LENGTH_SHORT).show();
            firstFinish = false;
        }
    }

    @Override
    public void onDrag(float x, float y) {

    }

    @Override
    public void onDragStarted(float x, float y) {
        if (firstStart) {
            Toast.makeText(this, "Cool, now drag!", Toast.LENGTH_SHORT).show();
            firstStart = false;

            textView.setVisibility(View.INVISIBLE);
        }
    }

    private static double distance(float fromX, float fromY, float toX, float toY) {
        return Math.sqrt(Math.pow(fromX - toX, 2) + Math.pow(fromY - toY, 2));
    }
}
