package com.android.viewdemo;

import com.android.viewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ViewDemoActivity extends Activity {
	private static final String TAG = "ViwDemo";
	private Button vTest;
	private Button outTest;
	LinearLayout myLinearLayout;
	MyViewGroup mViewGroup;

	private void Mylog(String msg) {
		Log.e(TAG, msg);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.viewgroup);
		setContentView(R.layout.main);

		vTest = (Button) findViewById(R.id.test);
		outTest = (Button) findViewById(R.id.outtext);
		myLinearLayout = (LinearLayout) findViewById(R.id.testViewGroup);
		mViewGroup = (MyViewGroup) findViewById(R.id.myViewGroup);
		vTest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Mylog("vTest width " + vTest.getMeasuredWidth() + "height "
						+ vTest.getMeasuredHeight());
				Mylog("outTest width " + outTest.getMeasuredWidth() + "height "
						+ outTest.getMeasuredHeight());
				Mylog("myLinearLayout width "
						+ myLinearLayout.getMeasuredWidth() + "height "
						+ myLinearLayout.getMeasuredHeight());
				Mylog("mViewGroup width " + mViewGroup.getMeasuredWidth()
						+ "height " + mViewGroup.getMeasuredHeight());
				Mylog("Visible vTest " + vTest.getVisibility() + " outTest "
						+ outTest.getVisibility() + " myLinearLayout "
						+ myLinearLayout.getVisibility() + " mViewGroup "
						+ mViewGroup.getVisibility());

			}
		});

		outTest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Mylog("vTest width " + vTest.getMeasuredWidth() + "height "
						+ vTest.getMeasuredHeight());
				Mylog("outTest width " + outTest.getMeasuredWidth() + "height "
						+ outTest.getMeasuredHeight());
				Mylog("myLinearLayout width "
						+ myLinearLayout.getMeasuredWidth() + "height "
						+ myLinearLayout.getMeasuredHeight());
				Mylog("mViewGroup width " + mViewGroup.getMeasuredWidth()
						+ "height " + mViewGroup.getMeasuredHeight());
				Mylog("Visible vTest " + vTest.getVisibility() + " outTest "
						+ outTest.getVisibility() + " myLinearLayout "
						+ myLinearLayout.getVisibility() + " mViewGroup "
						+ mViewGroup.getVisibility());
			}
		});

	}
}
