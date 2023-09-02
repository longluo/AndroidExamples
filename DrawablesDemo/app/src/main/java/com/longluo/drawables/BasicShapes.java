package com.longluo.drawables;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class BasicShapes extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicshapes);
    }

    public static void callMe(Context c) {
        c.startActivity(new Intent(c, BasicShapes.class));
    }
}