package com.longluo.GameEngine.Screen.elements.Property;

import java.util.Enumeration;

import com.longluo.GameEngine.GameObjectQueue;

//道具管理：装载一定数量的道具
public class PropertyManager extends GameObjectQueue {
	public PropertyManager() {
		super();
	}

	/**
	 * 尝试将道具放入道具箱
	 * 
	 * @param prop
	 *            尝试被放入的道具
	 */
	public void putIntoBox(Property prop) {
		this.put(prop.getId(), prop);
	}

	/**
	 * 取出道具
	 * 
	 * @param propID
	 *            被取出的道具ID
	 * @return 如果道具箱中存在此道具，则返回该道具，否则返回null
	 */
	public Property takeFromBox(String propID) {
		try {
			return (Property) this.get(propID);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 注销道具(当使用完道具或者丢弃道具时使用)
	 * 
	 * @param propID
	 *            将要注销的道具ID
	 * @return 注销成功返回true，否则返回false
	 */
	public boolean unRegisterProperty(String propID) {
		try {
			this.remove(propID);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 返回道具数组列表
	 * 
	 * @return 道具数组列表
	 */
	public Property[] getPropertyList() {
		Property[] prop = new Property[this.size()];
		Enumeration enu = this.elements();
		int i = 0;
		while (enu.hasMoreElements()) {
			prop[i++] = (Property) enu.nextElement();
		}
		return prop;
	}
}
