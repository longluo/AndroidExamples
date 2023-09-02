package com.android.customviewgroup;

import com.android.customviewgroup.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button btn;
	private TextView txt;
	private MyViewGroup myViewGroup;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// setContentView(new MyViewGroup(MainActivity.this));

		// 获得main.xml的控件引用
		btn = (Button) findViewById(R.id.btn);
		txt = (TextView) findViewById(R.id.txt);
		myViewGroup = (MyViewGroup) findViewById(R.id.custemViewGroup);

		// 点击我查看绘制流程
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// // TODO Auto-generated method stub
				// if(txt.getVisibility() == View.VISIBLE)
				// txt.setVisibility(View.INVISIBLE) ;
				// else
				// txt.setVisibility(View.INVISIBLE) ;

				// myViewGroup.invalidate() ;
				// if(myViewGroup.getVisibility() == View.VISIBLE)
				// myViewGroup.setVisibility(View.GONE) ;
				// else
				// myViewGroup.setVisibility(View.VISIBLE) ;
				//
				myViewGroup.requestFocus();
			}
		});

	}
}