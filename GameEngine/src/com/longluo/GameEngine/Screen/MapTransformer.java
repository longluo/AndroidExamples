package com.longluo.GameEngine.Screen;

import java.util.Vector;

import javax.microedition.lcdui.game.Sprite;

import com.longluo.GameEngine.GameActivity;
import com.longluo.GameEngine.GameObject;
import com.longluo.GameEngine.Util.Coordinates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//地图转换器：将屏幕的地图从一个切换到另一个，目前仅支持2个地图之间的切换
//地图的切换在Player与地图转换器之间的碰撞发生，地图转换器的图片可以是透明的图片，这样不会影响画面的效果
public class MapTransformer extends GameObject {
	// 下一个的地图ID
	private String nextMapID = null;
	// 下一个关卡ID
	private String nextLevelID = null;
	// 当前地图中的位置
	private Coordinates location = null;
	// 下一个地图的入口位置
	private Coordinates nextMapEntry = null;
	// 躯壳
	private Sprite body = null;

	public MapTransformer() {
		super();
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.nextLevelID = (String) v.elementAt(1);
		this.nextMapID = (String) v.elementAt(2);
		int col = Integer.parseInt((String) v.elementAt(3));
		int row = Integer.parseInt((String) v.elementAt(4));
		this.location = new Coordinates(col, row);
		int nextMapCol = Integer.parseInt((String) v.elementAt(5));
		int nextMapRow = Integer.parseInt((String) v.elementAt(6));
		this.nextMapEntry = new Coordinates(nextMapCol, nextMapRow);
		try {
			String imgUrl = (String) v.elementAt(7);
			this.body = new Sprite(BitmapFactory.decodeResource(
					GameActivity.mContext.getResources(),
					Integer.parseInt(imgUrl)));
			int tileWidth = Integer.parseInt((String) v.elementAt(8));
			int tileHeight = Integer.parseInt((String) v.elementAt(9));
			this.body.setRefPixelPosition(col * tileWidth, row * tileHeight);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Sprite getSprite() {
		return body;
	}

	public void setBody(Sprite sprite) {
		this.body = sprite;
	}

	public Coordinates getLocation() {
		return location;
	}

	public Coordinates getNextMapEntry() {
		return nextMapEntry;
	}

	public String getNextMapID() {
		return nextMapID;
	}

	public String toString() {
		return "id=" + super.toString() + " nextLevel=" + this.nextLevelID
				+ " nextMap=" + this.nextMapID + " location_col="
				+ this.location.getX() + " location_row="
				+ this.location.getY() + " nextMapEntry_col="
				+ this.nextMapEntry.getX() + " nextMapEntry_row="
				+ this.nextMapEntry.getY() + " sprite x=" + this.body.getX()
				+ " y=" + this.body.getY();
	}

	public String getNextLevelID() {
		return nextLevelID;
	}
}
