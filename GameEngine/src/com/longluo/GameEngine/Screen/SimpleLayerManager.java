package com.longluo.GameEngine.Screen;

import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

import com.longluo.GameEngine.GameActivity;
import com.longluo.GameEngine.Screen.elements.biology.Actor;
import com.longluo.GameEngine.Screen.elements.biology.NPC;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//图层管理器:负责构造地图
public class SimpleLayerManager extends LayerManager {
	public SimpleLayerManager() {
		super();
	}

	/**
	 * 构造地图
	 * 
	 * @param level
	 *            关卡对象
	 * @param actor
	 *            主角对象
	 * @param mapID
	 *            将要构造的地图ID
	 * @return 构造后的地图对象
	 */
	public SimpleMap constructMap(SimpleLevel level, Actor actor, String mapID) {
		SimpleMap map = level.findMap(mapID);
		if (map == null) {
			System.out.println("构造地图失败,原因是没有找到匹配mapID的地图对象");
		}
		// 先装载NPC和Actor，否则它们将被后面的图层盖住
		Object[] npcSet = map.getNpcSet().list();
		for (int i = 0; i < npcSet.length; i++) {
			NPC npc = (NPC) npcSet[i];
			this.append(npc.getAnimator());
		}
		System.out.println("装载NPC完毕！");

		this.append(actor.getAnimator());
		System.out.println("装载Actor完毕！");

		Object[] layerSet = map.getLayerSet().list();
		// 根据SimpleLayer装载tiledLayer
		for (int i = 0; i < layerSet.length; i++) {
			Bitmap img = null;
			SimpleLayer simpleLayer = (SimpleLayer) layerSet[i];
			try {
				img = BitmapFactory.decodeResource(
						GameActivity.mContext.getResources(),
						Integer.parseInt(simpleLayer.getImgURL()));
				// 建立TiledLayer
				TiledLayer layer = new TiledLayer(simpleLayer.getTileCols(),
						simpleLayer.getTileRows(), img,
						simpleLayer.getTileWidth(), simpleLayer.getTileHeight());
				// 获得图层数组
				int[] mapData = simpleLayer.getMapData();
				// 按照数组元素设置此图层的单元格
				for (int j = 0; j < mapData.length; j++) {
					int columnNum = j % simpleLayer.getTileCols();
					int rowNum = (j - columnNum) / simpleLayer.getTileCols();
					layer.setCell(columnNum, rowNum, mapData[j]);
				}

				simpleLayer.setLayer(layer);
				this.append(layer);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("装载图层完毕！");

		Object[] transformer = map.getMapLink().list();
		for (int i = 0; i < transformer.length; i++) {
			MapTransformer trans = (MapTransformer) transformer[i];
			this.append(trans.getSprite());
		}
		System.out.println("装载地图转换器完毕！");
		return map;
	}
}
