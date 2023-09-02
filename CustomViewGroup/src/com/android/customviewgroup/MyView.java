package com.android.customviewgroup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

//自定义View对象
public class MyView extends View {

	private Paint paint = new Paint();

	public MyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 设置该View大小为 80 80
		setMeasuredDimension(50, 50);
	}

	// 存在canvas对象，即存在默认的显示区域
	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Log.i("MyViewGroup", "MyView is onDraw ");
		// 加粗
		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		paint.setColor(Color.RED);
		canvas.drawColor(Color.BLUE);
		canvas.drawRect(0, 0, 30, 30, paint);
		canvas.drawText("MyView", 10, 40, paint);
	}
}
