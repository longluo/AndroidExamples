package com.longluo.GameEngine.Util;

//计时器类
public class Calculagraph {
	// 循环间隔（以ms为单位）
	private int loopTime = 0;
	// 是否是第一次计时
	private boolean isFirstTime = true;
	// 开始时间
	private long startTime = 0;
	// 运行时间
	private long runTime = 0;

	public Calculagraph(int loopTime) {
		this.loopTime = loopTime;
		isFirstTime = true;
		runTime = 0;
		startTime = 0;
	}

	/**
	 * 计时
	 * 
	 */
	public void calculate() {
		if (isFirstTime) {
			startTime = System.currentTimeMillis();
			isFirstTime = false;
		} else {
			runTime = System.currentTimeMillis();
		}
	}

	/**
	 * 是否超时
	 * 
	 * @return 如果超时返回true，否则返回false
	 */
	public boolean isTimeout() {
		return ((runTime - startTime) > loopTime);
	}

	/**
	 * 重置计时器
	 * 
	 */
	public void reset() {
		startTime = 0;
		runTime = 0;
		isFirstTime = true;
	}

	public int getLoopTime() {
		return loopTime;
	}
}
