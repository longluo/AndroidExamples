package com.android.actionbar.googlenative;

import com.android.actionbar.MainActivity;
import com.android.actionbar.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class NativeActionBarActivity extends Activity {
	private static final String TAG = "NativeActionBarActivity";

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		setContentView(R.layout.native_actionbar);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.action_list,
				android.R.layout.simple_spinner_dropdown_item);
		ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter,
				new mNavigationCallback());
		actionBar.setHomeButtonEnabled(true);
		Log.i(TAG, "height" + actionBar.getHeight());
		System.out.println(actionBar.getHeight());

		Tab tab = actionBar
				.newTab()
				.setText(R.string.tab1)
				.setTabListener(
						new TabListener<tab1Fragment>(this, "tab1",
								tab1Fragment.class));

		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText(R.string.tab2)
				.setTabListener(
						new TabListener<tab2Fragment>(this, "tab2",
								tab2Fragment.class));

		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText(R.string.tab3)
				.setTabListener(
						new TabListener<tab3Fragment>(this, "tab3",
								tab3Fragment.class));

		actionBar.addTab(tab);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menus, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			/*
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			*/
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class mNavigationCallback implements ActionBar.OnNavigationListener {

		String[] listNames = getResources().getStringArray(R.array.action_list);

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			ListFragment listFragment = new ListFragment();
			FragmentManager manager = getFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			ft.replace(R.id.titles, listFragment, listNames[itemPosition]);
			ft.commit();
			return true;
		}

	}

	public class TabListener<T extends Fragment> implements
			ActionBar.TabListener {

		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		public TabListener(Activity activity, String tag, Class<T> cls) {
			mActivity = activity;
			mTag = tag;
			mClass = cls;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabReselected()");

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabSelected()");
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			Log.i(TAG, "onTabUnselected()");
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause()");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart()");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop()");
		super.onStop();
	}

}
