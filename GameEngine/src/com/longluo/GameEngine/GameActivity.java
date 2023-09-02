package com.longluo.GameEngine;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class GameActivity extends Activity {
	public static Context mContext = null;

	/* 创建 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.main);
	}

	/* 创建菜单 */
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	/* 销毁 */
	protected void onDestroy() {
		super.onDestroy();
	}

	/* 按键按下 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	/* 按键重复按下 */
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	/* 按键释放 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	/* 菜单事件 */
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	/* 暂停 */
	protected void onPause() {
		super.onPause();
	}

	/* 重新开始 */
	protected void onRestart() {
		super.onRestart();
	}

	/* 重新激活 */
	protected void onResume() {
		super.onResume();
	}

	/* 开始 */
	protected void onStart() {
		super.onStart();
	}

	/* 停止 */
	protected void onStop() {
		super.onStop();
	}

	/* 触笔事件 */
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/* 设置标题 */
	// 使用MVC框架让资源文件通过R.java文件读取
	public void setTitle(int titleId) {
		super.setTitle(titleId);
	}
}