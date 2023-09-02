package com.longluo.GameEngine;

import java.util.Vector;

/**
 * 游戏对象类
 * 
 * @author long.luo
 * 
 */
public abstract class GameObject {
	// 对象的ID
	private String id = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public abstract void loadProperties(Vector v);

	public String toString() {
		return ("id=" + id);
	}
}
