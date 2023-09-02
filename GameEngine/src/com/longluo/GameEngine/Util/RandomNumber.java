package com.longluo.GameEngine.Util;

import java.util.Random;

//随机数类
public class RandomNumber {
	/**
	 * 判断n是否在except数组中
	 * 
	 * @param n
	 * @param except
	 * @return
	 */
	private static boolean isExcept(int n, int[] except) {
		for (int i = 0; i < except.length; i++) {
			if (n == except[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 生成随机数(0<=范围<bound)
	 * 
	 * @param bound
	 *            随机数的范围
	 * @param except
	 *            随机数取值的排除范围
	 * @return 随机数
	 */
	public static int genRandomlyNumber(int bound, int[] except) {
		Random ran = new Random();
		int result = ran.nextInt(bound);
		if (except != null) {
			while (isExcept(result, except)) {
				result = ran.nextInt(bound);
			}
		}
		return result;
	}
}
