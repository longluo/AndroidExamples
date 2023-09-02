package com.longluo.GameEngine;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class GameControl extends View implements Runnable {
	// 游戏状态
	public int mIGameStatus = -1;
	// 是否开启线程
	public boolean mBLoop = false;

	public GameControl(Context context) {
		super(context);
	}

	// 初始化游戏
	public void initGame() {
		mBLoop = true;
		mIGameStatus = GameDefinition.Game_Logo;

		Thread t = new Thread(this);
		t.start();
	}

	// 绘制游戏界面
	protected void onDraw(Canvas canvas) {
		switch (mIGameStatus) {
		case GameDefinition.Game_Logo:
			// 显示logo
			break;
		case GameDefinition.Game_MainMenu:
			// 显示主菜单
			break;
		case GameDefinition.Game_Help:
			// 显示帮助
			break;
		default:
			break;
		}
	}

	// 线程控制
	public void run() {
		while (mBLoop) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
			postInvalidate(); // 刷新屏幕
		}
	}

	/* 按键按下 */
	boolean onKeyDown(int keyCode) {
		switch (mIGameStatus) {
		case GameDefinition.Game_Logo:
			// 处理logo状态的按键事件
			break;
		case GameDefinition.Game_MainMenu:
			// 处理主菜单状态的按键事件
			break;
		case GameDefinition.Game_Help:
			// /处理帮助状态的按键事件
			break;
		default:
			break;
		}
		return true;
	}

	/* 按键弹起 */
	boolean onKeyUp(int keyCode) {
		return true;
	}

	/* 触笔事件 */
	public boolean onTouchEvent(MotionEvent event) {
		int iAction = event.getAction();
		// 根据获得的不同的事件进行处理
		if (iAction == MotionEvent.ACTION_CANCEL) {
		} else if (iAction == MotionEvent.ACTION_DOWN) {
		} else if (iAction == MotionEvent.ACTION_MOVE) {
		}
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (mIGameStatus) {
		case GameDefinition.Game_Logo:
			// 处理logo状态的触笔事件
			break;
		case GameDefinition.Game_MainMenu:
			// 处理主菜单状态的触笔事件
			break;
		case GameDefinition.Game_Help:
			// /处理帮助状态的触笔事件
			break;
		default:
			break;
		}
		return true;
	}
}
