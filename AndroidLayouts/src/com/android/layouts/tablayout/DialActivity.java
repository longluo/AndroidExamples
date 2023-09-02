package com.android.layouts.tablayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DialActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textview = new TextView(this);
		textview.setText("This is the Dial tab");
		setContentView(textview);
	}
}

