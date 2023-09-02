package com.longluo.volume;

import com.longluo.volume.WmtRatingBar.OnRatingBarChanging;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private WmtRatingBar mVoluemRatingBar;
	private TextView mRatingTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mRatingTextView = (TextView) findViewById(R.id.num);
		mVoluemRatingBar = (WmtRatingBar) findViewById(R.id.volume_ratingBar);
		mVoluemRatingBar.setOnRatingBarChange(new OnRatingBarChanging() {

			@Override
			public void onRatingChanging(float f) {
				mRatingTextView.setText("Volume=" + f);

			}
		});
	}
}
