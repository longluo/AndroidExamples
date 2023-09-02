package com.longluo.GameEngine.Material;

//边界类
public class Border {
	// 最小X坐标
	private int minX = 0;
	// 最小Y坐标
	private int minY = 0;
	// 最大X坐标
	private int maxX = 0;
	// 最大Y坐标
	private int maxY = 0;

	public Border(int minx, int miny, int maxx, int maxy) {
		this.maxX = maxx;
		this.maxY = maxy;
		this.minX = minx;
		this.minY = miny;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}
}
