package com.android.layouts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.*;
import android.widget.Gallery.LayoutParams;

public class Layout_RelativeLinearlayout extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//创建一个水平的LinearLayout
		LinearLayout layoutMain = new LinearLayout(this);
		layoutMain.setOrientation(LinearLayout.HORIZONTAL);
		setContentView(layoutMain);
		
		//将两个xml布局文件加载RelativeLayout布局
		LayoutInflater inflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layoutLeft = (RelativeLayout)inflate.inflate(R.layout.relativelinearlayout,null);
		RelativeLayout layoutRight = (RelativeLayout)inflate.inflate(R.layout.relativelinearlayout2, null);
		
		RelativeLayout.LayoutParams relParam = new 
			RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT
			);
		
		layoutMain.addView(layoutLeft,100,100);
		layoutMain.addView(layoutRight,relParam);
		
	}

}
