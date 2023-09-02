package com.longluo.GameEngine.Screen;

import java.util.Vector;

import com.longluo.GameEngine.GameObject;
import com.longluo.GameEngine.Screen.elements.biology.Actor;
import com.longluo.GameEngine.Util.Coordinates;

public class Camera extends GameObject {
	// 移动模式-跟随主角模式
	public static final int TRACK_PLAYER_MODEL = 1;
	// 使用自定义大小的Camera
	public static final int CUSTOM_SIZE = 1;
	// 使用与当前屏幕大小相同的Camera
	public static final int SCREEN_SIZE = 2;

	// 移动模式类型
	private int type = 0;
	// 摄像机位置，指笛卡儿坐标系的左上角坐标位置
	private Coordinates col = null;
	// 摄像机镜头宽度
	private int width = 0;
	// 摄像机镜头长度
	private int height = 0;
	// 使用自定义大小的标志
	private int CustomSizeFlag = 0;

	public Camera() {
		super();
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		int col = Integer.parseInt((String) v.elementAt(1));
		int row = Integer.parseInt((String) v.elementAt(2));
		this.col = new Coordinates(col, row);
		this.width = Integer.parseInt((String) v.elementAt(3));
		this.height = Integer.parseInt((String) v.elementAt(4));
		this.type = Integer.parseInt((String) v.elementAt(5));
		this.CustomSizeFlag = Integer.parseInt((String) v.elementAt(6));
	}

	public Coordinates getCoordinates() {
		return col;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setCoorindates(Coordinates col) {
		this.col = col;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void move(Actor actor, int mapWidth, int mapHeight) {
		int x = 0;
		int y = 0;
		switch (type) {
		case TRACK_PLAYER_MODEL:
			x = actor.getAnimator().getX() + actor.getAnimator().getWidth() / 2
					- width / 2;
			y = actor.getAnimator().getY() + actor.getAnimator().getHeight()
					/ 2 - height / 2;
			if (x < 0) {
				x = 0;
			} else if ((x + width) > mapWidth) {
				x = mapWidth - width;
			}
			if (y < 0) {
				y = 0;
			} else if ((y + height) > mapHeight) {
				y = mapHeight - height;
			}
			this.getCoordinates().setX(x);
			this.getCoordinates().setY(y);
			break;
		}
		System.out.println("Actor x=" + actor.getAnimator().getX() + " y="
				+ actor.getAnimator().getY());
		System.out.println("Camera x=" + this.getCoordinates().getX() + " y="
				+ this.getCoordinates().getY());
	}

	public int getCustomSizeFlag() {
		return CustomSizeFlag;
	}

	public void setCustomSizeFlag(int customSizeFlag) {
		CustomSizeFlag = customSizeFlag;
	}

	public String toString() {
		return "id=" + super.toString() + " x=" + this.col.getX() + " y="
				+ this.col.getY() + " width=" + this.width + " height="
				+ this.height + " moveType=" + this.type + " customSize="
				+ this.CustomSizeFlag;
	}
}
