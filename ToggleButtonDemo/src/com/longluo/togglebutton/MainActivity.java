package com.longluo.togglebutton;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnCheckedChangeListener {
	private ToggleButton mToggleButton;
	private TextView tvSound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		initView();//
	}

	private void initView() {
		mToggleButton = (ToggleButton) findViewById(R.id.tglSound);
		mToggleButton.setOnCheckedChangeListener(this);
		tvSound = (TextView) findViewById(R.id.tvSound);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			tvSound.setText("Open");
		} else {
			tvSound.setText("Closed");
		}
	}
}
