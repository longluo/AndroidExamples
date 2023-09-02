package com.longluo.GameEngine.Screen;

import com.longluo.GameEngine.GameObjectQueue;
import com.longluo.GameEngine.Script.XmlScriptParser;

//游戏装载器：负责将关卡(地图、NPC、地图转换器、道具)、主角、摄像机等装入内存
public class GameLoader {
	// 积极加载模式（加载所有的对象）
	public static final int POSITIVE_LOAD_MODEL = 1;
	// 懒惰加载模式（只加载第一个关卡用到的对象，其它关卡用的对象没有加载时将加载），本版本尚未支持
	public static final int LAZY_LOAD_MODEL = 2;
	// 关卡列表：每个关卡包含多个地图（每个地图包含多个图层、多个NPC、多个地图转换器）
	private GameObjectQueue globalLevelTable = null;
	// 摄像机列表
	private GameObjectQueue globalCameraTable = null;
	// 音乐列表
	private GameObjectQueue globalMusicTable = null;
	// 主角列表
	private GameObjectQueue globalActorTable = null;
	// 用于cache的对象列表-事件列表
	private GameObjectQueue eventTable = null;
	// 用于cache的对象列表-消息列表
	private GameObjectQueue msgTable = null;
	// 用于cache的对象列表-道具列表
	private GameObjectQueue propertyTable = null;
	// 用于cache的对象列表-NPC列表
	private GameObjectQueue npcTable = null;
	// 用于cache的对象列表-图层列表
	private GameObjectQueue layerTable = null;
	// 用于cache的对象列表-地图转换器列表
	private GameObjectQueue transformerTable = null;
	// 用于cache的对象列表-地图列表
	private GameObjectQueue mapTable = null;
	// 用于cache的对象列表-关卡列表
	private GameObjectQueue levelTable = null;
	// 游乐场运行间隔
	private int carnieRunInterval = 10;

	// 加载模式
	private int type = 0;

	// 由于目前仅支持积极加载模式，所以在构造方法中将type赋值为POSITIVE_LOAD_MODEL
	public GameLoader() {
		type = POSITIVE_LOAD_MODEL;
	}

	/**
	 * 从配置文件使用积极加载模式装载所有游戏对象
	 * 
	 * @param gameConfigureResURL
	 *            游戏配置资源路径
	 */
	public void load(String gameConfigureResURL) {
		long startTime = System.currentTimeMillis();

		XmlScriptParser x = new XmlScriptParser();
		// 打开xml配置文件
		x.openConfigure(gameConfigureResURL);

		// 装载消息对象
		GameObjectQueue msgTable = x.readMsgConfigure(true);
		System.out.println("msg数量=" + msgTable.size());

		// 装载消息对列，并与消息对象作关联
		GameObjectQueue mqTable = x.readMsgQueueConfigure(
				new GameObjectQueue[] { msgTable }, true);
		System.out.println("msg Queue数量=" + mqTable.size());

		// 装载事件对象配置
		GameObjectQueue eventTable = x.readEventConfigure(true);
		System.out.println("event数量=" + eventTable.size());

		// 装载事件列表，并与事件对象作关联
		GameObjectQueue eqTable = x.readEventQueueConfigure(
				new GameObjectQueue[] { eventTable }, true);
		System.out.println("event Queue数量=" + eqTable.size());

		// 装载道具对象
		GameObjectQueue propTable = x.readPropertyConfigure(true);
		System.out.println("property数量=" + propTable.size());

		// 装载道具箱，并与道具对象作关联
		GameObjectQueue propBoxTable = x.readPropertyBoxConfigure(
				new GameObjectQueue[] { propTable }, true);
		System.out.println("propertyBox数量=" + propBoxTable.size());

		// 装载NPC对象，关联道具箱对象、事件对象
		GameObjectQueue npcTable = x.readNpcConfigure(new GameObjectQueue[] {
				propTable, eventTable }, true);
		System.out.println("npc数量=" + npcTable.size());

		// 装载主角，关联道具箱对象
		GameObjectQueue actorTable = x.readActorConfigure(
				new GameObjectQueue[] { propTable }, true);
		System.out.println("actor数量=" + actorTable.size());

		// 装载图层对象
		GameObjectQueue layerTable = x.readLayerConfigure(true);
		System.out.println("layer数量=" + layerTable.size());

		// 装载地图转换器对象
		GameObjectQueue transformerTable = x.readTransformerConfigure(true);
		System.out.println("transformer数量=" + transformerTable.size());

		// 装载地图对象，关联NPC、图层、地图转换器
		GameObjectQueue mapTable = x.readMapConfigure(new GameObjectQueue[] {
				layerTable, npcTable, transformerTable }, true);
		System.out.println("map数量=" + mapTable.size());

		// 装载关卡对象，并关联地图对象
		GameObjectQueue levelTable = x.readLevelConfigure(
				new GameObjectQueue[] { mapTable }, true);
		System.out.println("level数量=" + levelTable.size());

		// 装载摄像机对象
		GameObjectQueue cameraTable = x.readCameraConfigure(true);
		System.out.println("camera数量=" + cameraTable.size());

		// 装载音乐对象
		GameObjectQueue musicTable = x.readMusicConfigure(true);
		System.out.println("music数量=" + musicTable.size());

		System.out.println("装载工作花费时间:"
				+ (System.currentTimeMillis() - startTime) + " ms");

		this.globalActorTable = actorTable;
		this.globalCameraTable = cameraTable;
		this.globalMusicTable = musicTable;
		this.globalLevelTable = levelTable;
	}

	public GameObjectQueue getGlobalActorTable() {
		return globalActorTable;
	}

	public GameObjectQueue getGlobalCameraTable() {
		return globalCameraTable;
	}

	public GameObjectQueue getGlobalLevelTable() {
		return globalLevelTable;
	}

	public GameObjectQueue getGlobalMusicTable() {
		return globalMusicTable;
	}

	public int getType() {
		return type;
	}

	public void setGlobalActorTable(GameObjectQueue globalActorTable) {
		this.globalActorTable = globalActorTable;
	}

	public void setGlobalCameraTable(GameObjectQueue globalCameraTable) {
		this.globalCameraTable = globalCameraTable;
	}

	public void setGlobalLevelTable(GameObjectQueue globalLevelTable) {
		this.globalLevelTable = globalLevelTable;
	}

	public void setGlobalMusicTable(GameObjectQueue globalMusicTable) {
		this.globalMusicTable = globalMusicTable;
	}

	public GameObjectQueue getEventTable() {
		return eventTable;
	}

	public void setEventTable(GameObjectQueue eventTable) {
		this.eventTable = eventTable;
	}

	public GameObjectQueue getLayerTable() {
		return layerTable;
	}

	public void setLayerTable(GameObjectQueue layerTable) {
		this.layerTable = layerTable;
	}

	public GameObjectQueue getLevelTable() {
		return levelTable;
	}

	public void setLevelTable(GameObjectQueue levelTable) {
		this.levelTable = levelTable;
	}

	public GameObjectQueue getMapTable() {
		return mapTable;
	}

	public void setMapTable(GameObjectQueue mapTable) {
		this.mapTable = mapTable;
	}

	public GameObjectQueue getMsgTable() {
		return msgTable;
	}

	public void setMsgTable(GameObjectQueue msgTable) {
		this.msgTable = msgTable;
	}

	public GameObjectQueue getNpcTable() {
		return npcTable;
	}

	public void setNpcTable(GameObjectQueue npcTable) {
		this.npcTable = npcTable;
	}

	public GameObjectQueue getPropertyTable() {
		return propertyTable;
	}

	public void setPropertyTable(GameObjectQueue propertyTable) {
		this.propertyTable = propertyTable;
	}

	public GameObjectQueue getTransformerTable() {
		return transformerTable;
	}

	public void setTransformerTable(GameObjectQueue transformerTable) {
		this.transformerTable = transformerTable;
	}

	public int getCarnieRunInterval() {
		return carnieRunInterval;
	}

	public void setCarnieRunInterval(int carnieRunInterval) {
		this.carnieRunInterval = carnieRunInterval;
	}
}
