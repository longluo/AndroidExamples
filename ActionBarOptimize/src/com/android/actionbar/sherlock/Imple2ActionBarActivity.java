package com.android.actionbar.sherlock;

import com.android.actionbar.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class Imple2ActionBarActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imple2_actionbar);
		((TextView) findViewById(R.id.text)).setText(R.string.Imple2Title);
	}
}
