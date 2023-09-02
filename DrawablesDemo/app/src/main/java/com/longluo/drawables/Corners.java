package com.longluo.drawables;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class Corners extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.corners);
    }

    public static void callMe(Context c) {
        c.startActivity(new Intent(c, Corners.class));
    }
}