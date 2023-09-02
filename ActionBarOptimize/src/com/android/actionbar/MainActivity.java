package com.android.actionbar;

import com.android.actionbar.googlenative.NativeActionBarActivity;
import com.android.actionbar.implement.Imple1ActionBarActivity;
import com.android.actionbar.sherlock.Imple2ActionBarActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "ActionBar";
	private TextView tvActionBarDesp = null;
	private Button btnNativeActionBar = null;
	private Button btnImple1ActionBar = null;
	private Button btnImple2ActionBar = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tvActionBarDesp = (TextView) findViewById(R.id.tvActionBarCompare);
		tvActionBarDesp.setText(R.string.actionbar);

		btnNativeActionBar = (Button) findViewById(R.id.btnNativeActionBar);
		btnNativeActionBar.setText(R.string.NativeActionBar);
		btnNativeActionBar
				.setOnClickListener(new NativeActionBarButtonListener());

		btnImple1ActionBar = (Button) findViewById(R.id.btnImple1ActionBar);
		btnImple1ActionBar.setText(R.string.Imple1Title);
		btnImple1ActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,
						Imple1ActionBarActivity.class);
				MainActivity.this.startActivity(intent);

			}

		});

		btnImple2ActionBar = (Button) findViewById(R.id.btnImple2ActionBar);
		btnImple2ActionBar.setText(R.string.Imple2Title);
		btnImple2ActionBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,
						Imple2ActionBarActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, R.string.exit);
		menu.add(0, 2, 2, R.string.about);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			finish();
		} else if (item.getItemId() == 2) {

		}

		return super.onOptionsItemSelected(item);
	}

	class NativeActionBarButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, NativeActionBarActivity.class);
			MainActivity.this.startActivity(intent);
		}
	}
}
