package com.longluo.GameEngine.Screen.animation;

import javax.microedition.lcdui.game.Sprite;

import com.longluo.GameEngine.Util.Calculagraph;

import android.graphics.Bitmap;

//动画角色类
public class Animator extends Sprite {
	private Calculagraph cal = null;

	public Animator(Bitmap img, int frameWidth, int frameHeight, int loopTime) {
		super(img, frameWidth, frameHeight);
		cal = new Calculagraph(loopTime);
	}

	public Animator(Sprite s, int loopTime) {
		super(s);
		cal = new Calculagraph(loopTime);
	}

	/**
	 * 播放动画
	 * 
	 */
	public void PlayAnimation() {
		if (cal.getLoopTime() > 0) {
			// 如果超时，则重新计时并播放下一Frame
			if (cal.isTimeout()) {
				cal.reset();
				this.nextFrame();
			}
			// 否则继续计时
			else {
				cal.calculate();
			}
		}
	}

	/**
	 * 停止播放动画
	 * 
	 */
	public void StopAnimation() {
		cal.reset();
	}

	/**
	 * 刷新动画的位置
	 * 
	 * @param x
	 *            阿尔法坐标系的x轴位置
	 * @param y
	 *            阿尔法坐标系的y轴位置
	 */
	public void flushPosition(int x, int y) {
		setRefPixelPosition(x, y);
	}
}
