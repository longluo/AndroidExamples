package com.android.viewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class MyViewGroup extends LinearLayout {
	private static final String TAG = "MyViewGroup";

	public MyViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private void Mylog(String msg) {
		Log.e(TAG, msg);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widhtSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		Mylog("allow wmod " + Integer.toHexString(widthMode) + "wsize "
				+ widhtSize + " hmode " + Integer.toHexString(heightMode)
				+ " hsize " + heightSize);
		setMeasuredDimension(120, 240);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		widhtSize = getMeasuredWidth();
		heightSize = getMeasuredHeight();

		Mylog("after measure wsize " + widhtSize + " wheight " + heightSize);

		// setMeasuredDimension(120, 240);
	}

}
