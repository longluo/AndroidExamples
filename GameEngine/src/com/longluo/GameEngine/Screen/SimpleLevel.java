package com.longluo.GameEngine.Screen;

import java.util.Vector;

import com.longluo.GameEngine.GameObject;
import com.longluo.GameEngine.GameObjectQueue;



//游戏世界：有多个map组成的大型关卡
public class SimpleLevel extends GameObject {
	// 关卡名称
	private String levelName = null;
	// 关卡中用到的地图,使用Map的id作为主键，以便MapTransformer查找
	private GameObjectQueue mapSet = null;
	// 关卡中第一个地图的ID
	private String firstMapID = null;

	public SimpleLevel() {
		super();
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.levelName = (String) v.elementAt(1);
		this.firstMapID = (String) v.elementAt(2);
	}

	public void setMapSet(GameObjectQueue mapSet) {
		this.mapSet = mapSet;
	}

	public String getLevelName() {
		return levelName;
	}

	public GameObjectQueue getMapSet() {
		return mapSet;
	}

	/**
	 * 查找ID对应的地图（SimpleMap对象）
	 * 
	 * @param mapID
	 *            地图ID
	 * @return 如果查找到，则返回查找到的ID对应的地图，否则返回null
	 */
	public SimpleMap findMap(String mapID) {
		if (mapSet.containsKey(mapID)) {
			return (SimpleMap) mapSet.get(mapID);
		} else {
			return null;
		}
	}

	public String getFirstMapID() {
		return firstMapID;
	}

	public String toString() {
		return "id=" + super.toString() + " levelName=" + levelName
				+ " firstMapID=" + firstMapID + " mapSet size=" + mapSet.size();
	}
}
