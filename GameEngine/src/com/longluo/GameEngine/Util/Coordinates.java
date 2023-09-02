package com.longluo.GameEngine.Util;

//坐标类：表示对象的位置（可以是在x,y坐标系坐标，也可以是在图层中的行列数）
public class Coordinates {
	// x轴坐标或者列数
	private int x = 0;
	// y轴坐标或者行数
	private int y = 0;

	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
