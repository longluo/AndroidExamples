package com.android.actionbardemo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class ActionBarDemoActivity extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "ActionBarDemoActivity";

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
		setContentView(R.layout.contents);
		// 资源布局的获取必须在调用之前actionBar.setListNavigationCallbacks()定义，否则会报空指针异常
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.action_list,
				android.R.layout.simple_spinner_dropdown_item);
		// 获取ActionBar代码需写在setContentView方法后
		ActionBar actionBar = getActionBar();
		// 设置操作栏导航下拉列表模式
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// 设置导航列表回调响应
		actionBar.setListNavigationCallbacks(mSpinnerAdapter,
				new mNavigationCallback());
		// 设置Tab标签导航模式
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// 隐藏应用标题
		// actionBar.setDisplayShowTitleEnabled(false);
		// 实现用户点击ActionBar图标后返回前一个activity
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// 实现返回主页需添加
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

		/*
		 * tab1Fragment tab1Fragment = new tab1Fragment(); tab2Fragment
		 * tab2Fragment = new tab2Fragment(); tab3Fragment tab3Fragment = new
		 * tab3Fragment();
		 */
	}

	// 为操作栏添加菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * MenuItem add = menu.add(0, 1, 0, "Save"); MenuItem open = menu.add(0,
		 * 2, 1, "Open"); MenuItem close = menu.add(0, 3, 2, "Close"); MenuItem
		 * share = menu.add(0, 4, 3, "Share"); MenuItem search = menu.add(0, 5,
		 * 4, "search");
		 * add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem
		 * .SHOW_AS_ACTION_WITH_TEXT);
		 * open.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 * close.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 * share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 * search.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 */
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menus, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
			// return true;
		}
	}

	// 下拉列表导航的监听类
	public class mNavigationCallback implements ActionBar.OnNavigationListener {

		String[] listNames = getResources().getStringArray(R.array.action_list);

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			// TODO Auto-generated method stub
			ListFragment listFragment = new ListFragment();
			FragmentManager manager = getFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			ft.replace(R.id.titles, listFragment, listNames[itemPosition]);
			ft.commit();
			return true;
		}

	}

	// tab标签的监听类
	/*
	 * protected class TabListener implements ActionBar.TabListener { private
	 * Fragment fragment; public TabListener(Fragment fragment) { this.fragment
	 * = fragment; }
	 * 
	 * @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onTabSelected(Tab tab, FragmentTransaction ft) { //
	 * TODO Auto-generated method stub ft.add(android.R.id.content, fragment,
	 * null); }
	 * 
	 * @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	 * // TODO Auto-generated method stub ft.remove(fragment); }
	 * 
	 * 
	 * }
	 */

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