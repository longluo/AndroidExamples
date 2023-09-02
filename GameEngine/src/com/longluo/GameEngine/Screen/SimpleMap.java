package com.longluo.GameEngine.Screen;

import java.util.Vector;

import com.longluo.GameEngine.GameObject;
import com.longluo.GameEngine.GameObjectQueue;

//简单地图：由主角的行走路径和非行走路径组成,还有NPC
public class SimpleMap extends GameObject {
	// 图层集合(图层的顺序按照数组的顺序)
	private GameObjectQueue layerSet = null;
	// 此图层中的NPC
	private GameObjectQueue npcSet = null;
	// 本地图与前后地图的连接器，一个map至少可以有一个
	private GameObjectQueue mapLink = null;
	// 地图的宽度
	private int width = 0;
	// 地图的高度
	private int height = 0;
	// 地图名字
	private String name = null;

	public String getName() {
		return name;
	}

	public SimpleMap() {
		super();
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.name = (String) v.elementAt(1);
		this.width = Integer.parseInt((String) v.elementAt(2));
		this.height = Integer.parseInt((String) v.elementAt(3));
	}

	public GameObjectQueue getLayerSet() {
		return layerSet;
	}

	public GameObjectQueue getMapLink() {
		return mapLink;
	}

	public GameObjectQueue getNpcSet() {
		return npcSet;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setLayerSet(GameObjectQueue layerSet) {
		this.layerSet = layerSet;
	}

	public void setMapLink(GameObjectQueue mapLink) {
		this.mapLink = mapLink;
	}

	public void setNpcSet(GameObjectQueue npcSet) {
		this.npcSet = npcSet;
	}

	public String toString() {
		return "id=" + super.toString() + " name=" + this.name + " width="
				+ this.width + " height=" + this.height + " layerSet size="
				+ this.layerSet.size() + " MapLink size=" + this.mapLink.size()
				+ " NpcSet size=" + this.npcSet.size();
	}
}
