package com.android.layouts;

import com.android.layouts.tablayout.TabLayoutActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Layouts extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 找到按钮Button0
		Button btn = (Button) findViewById(R.id.Button01);
		// 添加单击事件
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Layouts.this,
						Layout_Framelayout.class);
				setTitle("FrameLayout演示");
				startActivity(intent);
			}
		});

		/*
		 * LinearLayout demo
		 */
		// 找到按钮
		btn = (Button) findViewById(R.id.Button02);
		// 添加单击事件
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Layouts.this,
						Layout_Linearlayout.class);
				setTitle("LinearLayout演示");
				startActivity(intent);
			}
		});

		/*
		 * RelativeLayout demo
		 */
		// 找到按钮
		btn = (Button) findViewById(R.id.Button03);
		// 添加单击事件
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Layouts.this,
						Layout_Relativelayout.class);
				setTitle("RelativeLayout演示");
				startActivity(intent);
			}
		});

		/*
		 * Layout_RelativeLinearlayout demo
		 */
		// 找到按钮
		btn = (Button) findViewById(R.id.Button04);
		// 添加单击事件
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Layouts.this,
						Layout_RelativeLinearlayout.class);
				setTitle("RelativeLayout 和LinearLayout的演示");
				startActivity(intent);
			}
		});

		/*
		 * TableLayout demo
		 */
		// 找到按钮
		btn = (Button) findViewById(R.id.Button05);
		// 添加单击事件
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Layouts.this,
						Layout_Tablelayout.class);
				setTitle("TableLayout的演示");
				startActivity(intent);
			}
		});
		
		
		// Tab Layout
		btn = (Button) findViewById(R.id.ButtonTab);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Layouts.this,
						TabLayoutActivity.class);
				setTitle("Tab Layout Show");
				startActivity(intent);
			}
		});
	}
	
}