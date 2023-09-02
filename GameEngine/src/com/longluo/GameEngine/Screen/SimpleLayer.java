package com.longluo.GameEngine.Screen;

import java.util.Vector;

import javax.microedition.lcdui.game.TiledLayer;

import com.longluo.GameEngine.GameObjectQueue;
import com.longluo.GameEngine.Util.StringExtension;


//简单地图层：只显示一屏大小
public class SimpleLayer extends GameObjectQueue {
	// 行走路径类型图层
	public static int WALKARENA = 1;
	// 非行走路径类型图层
	public static int NO_WALKARENA = 2;
	// 地图数据
	private int[] mapData = null;
	// 瓷砖的宽度
	private int tileWidth = 0;
	// 瓷砖的高度
	private int tileHeight = 0;
	// 瓷砖的列数
	private int tileCols = 0;
	// 瓷砖的行数
	private int tileRows = 0;
	// 图层
	private TiledLayer layer = null;
	// 图层种类：主角的行走路径、非行走路径
	private int type = 0;
	// 图片URL
	private String imgURL = null;

	public SimpleLayer() {
		super();
	}

	/**
	 * 将字符串转换为数组
	 * 
	 * @param s
	 *            被转换的字符串
	 * @return 转换后的整型数组
	 */
	private int[] StringToIntArray(String s) {
		// 首先去掉字符串中的格式化字符
		s = StringExtension.removeToken(s,
				new String[] { "\t", " ", "\r", "\n" });
		Object[] objArr = StringExtension.split(new StringBuffer(s), ",",
				StringExtension.INTEGER_ARRAY, false);
		return StringExtension.objectArrayBatchToIntArray(objArr);
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.tileWidth = Integer.parseInt((String) v.elementAt(1));
		this.tileHeight = Integer.parseInt((String) v.elementAt(2));
		this.tileCols = Integer.parseInt((String) v.elementAt(3));
		this.tileRows = Integer.parseInt((String) v.elementAt(4));
		this.type = Integer.parseInt((String) v.elementAt(5));
		this.imgURL = (String) v.elementAt(6);
		this.mapData = StringToIntArray((String) v.elementAt(7));
	}

	public TiledLayer getLayer() {
		return layer;
	}

	public int[] getMapData() {
		return mapData;
	}

	public int getTileCols() {
		return tileCols;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public int getTileRows() {
		return tileRows;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getType() {
		return type;
	}

	public void setLayer(TiledLayer layer) {
		this.layer = layer;
	}

	public String getImgURL() {
		return imgURL;
	}

	public String toString() {
		return "id=" + super.getId() + " tileWidth=" + this.tileWidth
				+ " tileHeight=" + this.tileHeight + " tileCols="
				+ this.tileCols + " tileRows=" + this.tileRows + " type="
				+ this.type + " imgURL=" + this.imgURL + " mapData Size="
				+ this.mapData.length;
	}
}
