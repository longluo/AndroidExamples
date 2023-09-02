package com.longluo.drawables;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Drawables extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.basicShapes).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BasicShapes.callMe(Drawables.this);
            }
        });

        findViewById(R.id.corners).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Corners.callMe(Drawables.this);
            }
        });

        findViewById(R.id.gradient).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Gradients.callMe(Drawables.this);
            }
        });

        findViewById(R.id.stateLists).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StateLists.callMe(Drawables.this);
            }
        });

        findViewById(R.id.ninePatch).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NinePatch.callMe(Drawables.this);
            }
        });

        findViewById(R.id.relativeLayout).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                RelativeLayoutTest.callMe(Drawables.this);
            }
        });
    }
}
