package com.longluo.GameEngine.Script;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import com.longluo.GameEngine.GameObject;
import com.longluo.GameEngine.GameObjectQueue;
import com.longluo.GameEngine.Events.Event;
import com.longluo.GameEngine.Events.EventQueue;
import com.longluo.GameEngine.Events.Message;
import com.longluo.GameEngine.Events.MessageQueue;
import com.longluo.GameEngine.Music.Music;
import com.longluo.GameEngine.Music.Musician;
import com.longluo.GameEngine.Screen.Camera;
import com.longluo.GameEngine.Screen.MapTransformer;
import com.longluo.GameEngine.Screen.SimpleLayer;
import com.longluo.GameEngine.Screen.SimpleLevel;
import com.longluo.GameEngine.Screen.SimpleMap;
import com.longluo.GameEngine.Screen.elements.Property.Property;
import com.longluo.GameEngine.Screen.elements.Property.PropertyManager;
import com.longluo.GameEngine.Screen.elements.biology.Actor;
import com.longluo.GameEngine.Screen.elements.biology.NPC;
import com.longluo.GameEngine.Util.StringExtension;


//xml脚本解析器
public class XmlScriptParser {
	private XmlReader xmlReader = null;

	// xml文件中的Tag声明：REF后缀表示对另外一个元素的引用
	// 消息元素名
	private String msgName = "msg";
	// 消息元素属性
	private String[] msgAttributes = { "id", "content" };

	// 消息队列元素名
	private String msgQueueName = "msgQueue";
	// 消息队列元素属性
	private String[] msgQueueAttributes = { "id", "msgsREF" };

	// 事件元素名
	private String eventName = "event";
	// 事件元素属性
	private String[] eventAttributes = { "id", "invoker", "responser", "type",
			"parameter" };
	// 事件队列元素名
	private String eventQueueName = "eventQueue";
	// 事件队列元素属性
	private String[] eventQueueAttributes = { "id", "eventsREF" };

	// 道具元素名
	private String propertyName = "property";
	// 道具元素属性
	private String[] propertyAttributes = { "id", "name", "description",
			"buyPrice", "salePrice", "lifeEffect", "attackEffect",
			"defenceEffect", "useTimes", "type" };

	// 道具箱元素名
	private String propBoxName = "propertyBox";
	// 道具箱元素属性
	private String[] propBoxAttributes = { "id", "propsREF" };

	// NPC元素名
	private String npcName = "npc";
	// NPC元素属性
	private String[] npcAttributes = { "id", "name", "life", "attack",
			"defence", "imgURL", "faceURL", "col", "row", "speed", "direction",
			"animationLoopTime", "animationFrameWidth", "animationFrameHeight",
			"frameSwtichSequence", "eventQueueREF", "propertiesREF", "type" };
	// Actor元素名
	private String actorName = "actor";
	// Actor元素属性
	private String[] actorAttributes = { "id", "name", "life", "attack",
			"defence", "imgURL", "faceURL", "col", "row", "speed", "direction",
			"animationLoopTime", "animationFrameWidth", "animationFrameHeight",
			"frameSwtichSequence", "propertiesREF" };
	// 图层元素名
	private String layerName = "layer";
	// 图层元素属性
	private String[] layerAttributes = { "id", "tileWidth", "tileHeight",
			"tileCols", "tileRows", "type", "imgURL", "mapData" };
	// 地图转换器元素名称
	private String transformerName = "transformer";
	// 地图转换器元素属性
	private String[] transformerAttributes = { "id", "nextLevel", "nextMap",
			"location_col", "location_row", "nextMapEntry_col",
			"nextMapEntry_row", "imgURL", "tileWidth", "tileHeight" };

	// 地图元素名称
	private String mapName = "map";
	// 地图元素属性
	private String[] mapAttributes = { "id", "name", "width", "height",
			"layerREF", "npcREF", "maptransformerREF" };

	// 关卡元素名称
	private String levelName = "level";
	// 关卡元素属性
	private String[] levelAttributes = { "id", "name", "firstMapID", "mapREF" };

	// 摄像机元素名称
	private String cameraName = "camera";
	// 摄像机元素属性
	private String[] cameraAttributes = { "id", "x", "y", "width", "height",
			"moveType", "customSize" };

	// 音乐元素名称
	private String musicName = "music";
	// 音乐元素属性
	private String[] musicAttributes = { "id", "resURL", "musicType",
			"playModel", "loopNumber" };

	// 全局配置名称
	private String globalName = "global";
	// 全局配置属性
	private String[] globalAttributes = { "carnieRunInterval" };

	// 元素对应的游戏对象的类型
	public static final int EVENT_OBJECT = 1;
	public static final int EVENTQUEUE_OBJECT = 2;
	public static final int MESSAGE_OBJECT = 3;
	public static final int MESSAGEQUEUE_OBJECT = 4;
	public static final int PROPERTY_OBJECT = 5;
	public static final int PROPERTYBOX_OBJECT = 6;
	public static final int NPC_OBJECT = 7;
	public static final int ACTOR_OBJECT = 8;
	public static final int LAYER_OBJECT = 9;
	public static final int TRANSFORMER_OBJECT = 10;
	public static final int MAP_OBJECT = 11;
	public static final int LEVEL_OBJECT = 12;
	public static final int CAMERA_OBJECT = 13;
	public static final int MUSIC_OBJECT = 14;
	public static final int GLOBAL = 15;
	private final int NOTFOUND = 1;
	private final int FOUND = 2;
	private final int ELEMENTEND = 3;

	/**
	 * 打开配置
	 */
	public void openConfigure(String resURL) {
		InputStream in = this.getClass().getResourceAsStream(resURL);
		InputStreamReader reader = new InputStreamReader(in);
		try {
			xmlReader = new XmlReader(reader);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 找到任意元素的下一个StartTag位置，无论是哪个元素的START_TAG位置
	 */
	private void findNextStartTag() throws Exception {
		// 遇到StartTag或者xml文档结尾时结束
		while ((xmlReader.getType() != XmlReader.END_DOCUMENT)
				&& (xmlReader.next() != XmlReader.START_TAG)) {
			// System.out.println(xmlReader.getPositionDescription());
			;
		}
	}

	/**
	 * 找到指定元素的下一个StartTag位置，返回查找结果常量
	 * 
	 * @param name
	 *            被查找的元素名
	 * @return 如果在xml文件的下一个位置发现被查找元素的START_TAG，则返回FOUND;
	 *         如果发现的元素名不是被查找的元素，则返回NOTFOUND;
	 *         如果在xml文件的下一个位置发现被查找元素的TEXT，则返回ELEMENTEND;
	 */
	private int findNextStartTag(String name) throws Exception {
		int result = NOTFOUND;

		// 遇到StartTag或者xml文档结尾时结束
		while (xmlReader.getType() != XmlReader.END_DOCUMENT) {

			// 如果已经读到START_TAG位置
			if (xmlReader.next() == XmlReader.START_TAG) {
				// 如果为当前参数指定的name元素，则返回FOUND
				if (xmlReader.getName().equals(name)) {
					System.out.println("已经找到" + name + "元素的START_TAG位置:"
							+ xmlReader.getPositionDescription());
					// xmlReader.require(XmlReader.START_TAG,name);
					result = FOUND;
					break;
				}
				// 否则返回NOTFOUND
				else {
					result = NOTFOUND;
					break;
				}
			}
			// 如果没有读到START_TAG位置
			else {
				// 如果读到的是TEXT位置，说明读到了不同名的元素之间的空行，则返回ELEMENTEND
				if (xmlReader.getType() == XmlReader.TEXT) {
					System.out.println("查找到元素结尾");
					result = this.ELEMENTEND;
					break;
				}
				// 否则返回NOTFOUND
				else {
					System.out.println("没有找到" + name + "元素的START_TAG位置:"
							+ xmlReader.getPositionDescription());
					result = NOTFOUND;
				}

			}
		}
		return result;
	}

	/**
	 * 读取配置头部
	 */
	public void readConfigureHeader() {
		try {
			// 读取gmatrix的属性
			xmlReader.next();
			xmlReader.require(XmlReader.START_TAG, "gmatrix");
			System.out.println("gmatrix属性--verion="
					+ xmlReader.getAttributeValue("version"));

			// 读取configure的属性
			xmlReader.next();
			xmlReader.require(XmlReader.START_TAG, "configure");
			System.out.println("读完配置头");

			// 找到下一个StartTag
			findNextStartTag();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * 读取元素名为name的所有元素
	 * 
	 * @param name
	 *            元素名称
	 * @param attr
	 *            元素属性名数组
	 * @param isOrdinal
	 *            是否按照顺序读取
	 * @return 装载元素属性值的Vector
	 */
	public Vector readElement(String name, String[] attr, boolean isOrdinal) {
		try {
			System.out.println("开始读取" + name + "元素...");
			Vector AttributesValueSet = new Vector();
			// 第一次找到被查找的元素START_TAG位置时被置为true
			boolean firstMatch = false;

			while (true) {
				// System.out.println("type="+xmlReader.getType()+" name="+xmlReader.getName());

				// 如果是xml文件开头，则首先读取文件头
				if (xmlReader.getType() == XmlReader.START_DOCUMENT) {
					readConfigureHeader();
				}

				// 不按照顺序处理
				if (isOrdinal == false) {
					// 找到name元素的START_TAG位置
					int findResult = findNextStartTag(name);
					System.out.println("findResult=" + findResult);

					// 根据查找的返回值判断是否继续查询
					switch (findResult) {
					// 如果返回NOTFOUND，且尚未发现过元素的START_TAG位置，则继续查询
					// 如果已经发现过元素的START_TAG位置，则返回查找的结果
					case NOTFOUND:
						if (firstMatch == false) {
							continue;
						} else {
							return AttributesValueSet;
						}
						// 如果返回ELEMENTEND，且尚未发现过元素的START_TAG位置，则继续查询
						// 如果已经发现过元素的START_TAG位置，则返回查找的结果
					case ELEMENTEND:
						if (firstMatch == false) {
							continue;
						} else {
							return AttributesValueSet;
						}
						// 如果返回FOUND，则将firstMatch标志置为true，继续处理
					case FOUND:
						firstMatch = true;
						break;
					}
				}

				// 如果xml文件结束，则退出
				if (xmlReader.getType() == XmlReader.END_DOCUMENT) {
					System.out.println("读取" + name + "时文档结束...");
					break;
				}

				// 判断是否还是在当前的元素中
				if ((xmlReader.getName() != null)
						&& (xmlReader.getName().equals(name) == false)) {
					System.out.println("读取" + name + "元素结束...");
					break;
				}

				// 声明装载元素属性值的Vector
				Vector AttributesValue = new Vector();

				// 请求读取元素的START_TAG位置的数据
				xmlReader.require(XmlReader.START_TAG, name);

				// 取出元素的属性
				for (int i = 0; i < attr.length; i++) {
					System.out.println(name + "属性--" + attr[i] + "="
							+ xmlReader.getAttributeValue(attr[i]));
					AttributesValue.addElement(xmlReader
							.getAttributeValue(attr[i]));
				}

				// 添加装载元素属性值的Vector
				AttributesValueSet.addElement(AttributesValue);

				// 如果按照顺序的话，需要找到元素的START_Tag开始位置
				if (isOrdinal) {
					findNextStartTag();
				}

			}
			return AttributesValueSet;
		} catch (Exception ex) {
			System.out.println("读取" + name + "元素时出现错误：" + ex.getMessage());
			return null;
		}
	}

	/**
	 * 读取元素名为name的所有元素
	 * 
	 * @param name
	 *            元素名称
	 * @param attr
	 *            元素属性名数组
	 */
	public Vector readElement(String name, String[] attr) {
		try {
			System.out.println("开始读取" + name + "元素...");
			Vector AttributesValueSet = new Vector();

			while (true) {
				// System.out.println("type="+xmlReader.getType()+" name="+xmlReader.getName());
				// 如果是xml文件开头，则首先读取文件头
				if (xmlReader.getType() == XmlReader.START_DOCUMENT) {
					readConfigureHeader();
				}
				// 如果xml文件结束，则退出
				if (xmlReader.getType() == XmlReader.END_DOCUMENT) {
					System.out.println("读取" + name + "时文档结束...");
					break;
				}
				// 判断是否还是在当前的元素中
				if ((xmlReader.getName() != null)
						&& (xmlReader.getName().equals(name) == false)) {
					System.out.println("读取" + name + "元素结束...");
					break;
				}

				// 声明装载元素属性值的Vector
				Vector AttributesValue = new Vector();

				// 找到元素的Tag开始位置
				xmlReader.require(XmlReader.START_TAG, name);
				// 取出name的属性
				for (int i = 0; i < attr.length; i++) {
					System.out.println(name + "属性--" + attr[i] + "="
							+ xmlReader.getAttributeValue(attr[i]));
					AttributesValue.addElement(xmlReader
							.getAttributeValue(attr[i]));
				}

				// 添加装载元素属性值的Vector
				AttributesValueSet.addElement(AttributesValue);

				// 移动到下一个位置
				xmlReader.next();
				// 读取元素的Tag结束位置
				xmlReader.require(XmlReader.END_TAG, name);
				// System.out.println(xmlReader.getPositionDescription());
				// 找到下一个startTag
				findNextStartTag();
			}
			return AttributesValueSet;
		} catch (Exception ex) {
			System.out.println("读取" + name + "元素时出现错误：" + ex.getMessage());
			return null;
		}
	}

	/**
	 * 使用逗号分割字符串为数组
	 * 
	 * @param s
	 *            被分割的字符串
	 * @return 分割后的数组
	 */
	private String[] splitByComma(String s) {
		String[] result = StringExtension
				.objectArrayBatchToStringArray(StringExtension.split(
						new StringBuffer(s), ",", StringExtension.STRING_ARRAY,
						false));
		return result;
	}

	/**
	 * 取出指定元素的属性，构造相应的对象，以列表的方式返回
	 * 
	 * @param elementName
	 *            元素名称
	 * @param elementAttributes
	 *            元素属性名称数组
	 * @param elementType
	 *            元素所说明的游戏对象的类型
	 * @param associatedTableArray
	 *            与本游戏对象关联的游戏对象列表，供本游戏对象查找并作关联
	 * @param isOrdinal
	 *            是否按顺序读取xml
	 * @return 装载游戏对象列表（包括游戏对象或者作为游戏对象列表-如消息队列）
	 */
	public GameObjectQueue readGameObjectConfigure(String elementName,
			String[] elementAttributes, int elementType,
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {

		GameObjectQueue resultTable = null;
		GameObject go = null;
		GameObject findGo = null;
		GameObjectQueue findGq = null;
		String id = null;
		String objectIDList = null;

		// 读取元素属性值,返回包含多个属性的值集合的Vector
		Vector AttributesValueSet = readElement(elementName, elementAttributes,
				isOrdinal);

		if ((AttributesValueSet != null) && (AttributesValueSet.size() > 0)) {
			// 处理每个属性值集合
			for (int i = 0; i < AttributesValueSet.size(); i++) {
				Vector attrValue = (Vector) AttributesValueSet.elementAt(i);
				switch (elementType) {
				// 对于没有关联子列表的属性值集合，直接构造对象
				case EVENT_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new Event();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				case MESSAGE_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new Message();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				case PROPERTY_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new Property();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
					/*
				case LAYER_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new SimpleLayer();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
					*/
				case TRANSFORMER_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new MapTransformer();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				case CAMERA_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new Camera();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				case MUSIC_OBJECT:
					if (resultTable == null) {
						resultTable = new Musician();
					}
					go = new Music();
					go.loadProperties(attrValue);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				// 对于带有关联子列表的属性值集合，需要进行关联
				case EVENTQUEUE_OBJECT:
					if (resultTable == null) {
						resultTable = new EventQueue();
					}
				case MESSAGEQUEUE_OBJECT:
					if (resultTable == null) {
						resultTable = new MessageQueue();
					}
				case PROPERTYBOX_OBJECT:
					if (resultTable == null) {
						resultTable = new PropertyManager();
					}
					// 获得ID
					id = (String) attrValue.elementAt(0);
					// 获得其关联的对象的ID列表
					objectIDList = (String) attrValue.elementAt(1);
					// 对其关联的对象进行关联
					if (objectIDList.length() > 0) {
						GameObjectQueue gq = new GameObjectQueue();
						// 将关联的对象的ID列表分离为数组
						String[] objectIDSet = splitByComma(objectIDList);
						// 在关联对象的列表中查找与关联的对象的ID对应的对象，并作关联
						for (int j = 0; j < objectIDSet.length; j++) {
							findGo = (GameObject) associatedTableArray[0]
									.find(objectIDSet[j]);
							if (findGo != null) {
								gq.put(objectIDSet[j], findGo);
								gq.setId(id);
								resultTable.put(id, gq);
							}
						}
					}
					break;
				case NPC_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new NPC();
					go.loadProperties(attrValue);
					NPC npc = (NPC) go;
					// 查找关联对象列表中与关联对象ID相对应的对象，即与本NPC对应的事件列表
					findGq = (GameObjectQueue) associatedTableArray[0]
							.find((String) attrValue.elementAt(15));
					if (findGo != null) {
						npc.setEventQueue((EventQueue) findGq);
					} else {
						npc.setEventQueue(new EventQueue());
					}
					// 查找关联对象列表中与关联对象ID相对应的对象，即与本NPC对应的道具列表
					findGq = (GameObjectQueue) associatedTableArray[1]
							.find((String) attrValue.elementAt(16));
					if (findGq != null) {
						npc.setPropertyBox((PropertyManager) findGq);
					} else {
						npc.setPropertyBox(new PropertyManager());
					}
					npc.setType(Integer.parseInt((String) attrValue
							.elementAt(17)));
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(npc.getId(), npc);
					break;
				case ACTOR_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new Actor();
					go.loadProperties(attrValue);
					Actor actor = (Actor) go;
					// 查找关联对象列表中与关联对象ID相对应的对象，即与本Actor对应的道具列表
					findGq = (GameObjectQueue) associatedTableArray[0]
							.find((String) attrValue.elementAt(14));
					if (findGq != null) {
						actor.setPropertyBox((PropertyManager) findGq);
					} else {
						actor.setPropertyBox(new PropertyManager());
					}
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(actor.getId(), actor);
					break;
				case MAP_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new SimpleMap();
					go.loadProperties(attrValue);
					SimpleMap simpleMap = (SimpleMap) go;

					int attrID = 4;
					int associatedTableID = 0;

					for (int m = attrID; m < attrValue.size(); m++) {
						// 获得Layer和npc、maptransformer
						// 获得其关联的对象的ID列表
						objectIDList = (String) attrValue.elementAt(attrID);
						// 对其关联的对象进行关联
						if (objectIDList.length() > 0) {
							GameObjectQueue gq = new GameObjectQueue();
							// 将关联的对象的ID列表分离为数组
							String[] objectIDSet = splitByComma(objectIDList);
							// 在关联对象的列表中查找与关联的对象的ID对应的对象，并作关联
							for (int j = 0; j < objectIDSet.length; j++) {
								findGo = (GameObject) associatedTableArray[associatedTableID]
										.find(objectIDSet[j]);
								if (findGo != null) {
									gq.put(objectIDSet[j], findGo);
									gq.setId(id);
								}
							}
							switch (attrID) {
							case 4:
								simpleMap.setLayerSet(gq);
								break;
							case 5:
								simpleMap.setNpcSet(gq);
								break;
							case 6:
								simpleMap.setMapLink(gq);
								break;
							}
						}
						attrID++;
						associatedTableID++;
					}
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				case LEVEL_OBJECT:
					if (resultTable == null) {
						resultTable = new GameObjectQueue();
					}
					go = new SimpleLevel();
					go.loadProperties(attrValue);
					SimpleLevel level = (SimpleLevel) go;
					// 获得map队列
					GameObjectQueue gq = new GameObjectQueue();
					// 将关联的对象的ID列表分离为数组
					String[] objectIDSet = splitByComma((String) attrValue
							.elementAt(3));
					// 在关联对象的列表中查找与关联的对象的ID对应的对象，并作关联
					for (int j = 0; j < objectIDSet.length; j++) {
						findGo = (GameObject) associatedTableArray[0]
								.find(objectIDSet[j]);
						if (findGo != null) {
							gq.put(objectIDSet[j], findGo);
						}
					}
					level.setMapSet(gq);
					System.out.println("被装载的游戏对象:" + go);
					resultTable.put(go.getId(), go);
					break;
				}

			}
			return resultTable;
		} else {
			return new GameObjectQueue();
		}
	}

	/**
	 * 取出全局配置元素的属性，返回游乐场要使用的运行时间间隔
	 * 
	 * @param elementName
	 *            元素名称
	 * @param elementAttributes
	 *            元素属性名称数组
	 * @param isOrdinal
	 *            是否按顺序读取xml
	 * @return 游乐场要使用的运行时间间隔
	 */
	public int readGlobalConfigureForCarnieRunInterval(String elementName,
			String[] elementAttributes, boolean isOrdinal) {

		// 读取元素属性值,返回包含多个属性的值集合的Vector
		Vector AttributesValueSet = readElement(elementName, elementAttributes,
				isOrdinal);
		int result = 10;
		if ((AttributesValueSet != null) && (AttributesValueSet.size() > 0)) {
			// 处理每个属性值集合
			Vector attrValue = (Vector) AttributesValueSet.elementAt(0);
			result = Integer.valueOf((String) attrValue.elementAt(0))
					.intValue();
		}
		return result;
	}

	public GameObjectQueue readMsgConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(msgName, msgAttributes, MESSAGE_OBJECT,
				null, isOrdinal);
	}

	public GameObjectQueue readMsgQueueConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(msgQueueName, msgQueueAttributes,
				MESSAGEQUEUE_OBJECT, associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readEventConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(eventName, eventAttributes,
				EVENT_OBJECT, null, isOrdinal);
	}

	public GameObjectQueue readEventQueueConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(eventQueueName, eventQueueAttributes,
				EVENTQUEUE_OBJECT, associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readPropertyConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(propertyName, propertyAttributes,
				PROPERTY_OBJECT, null, isOrdinal);
	}

	public GameObjectQueue readPropertyBoxConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(propBoxName, propBoxAttributes,
				PROPERTYBOX_OBJECT, associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readNpcConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(npcName, npcAttributes, NPC_OBJECT,
				associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readActorConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(actorName, actorAttributes,
				ACTOR_OBJECT, associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readLayerConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(layerName, layerAttributes,
				LAYER_OBJECT, null, isOrdinal);
	}

	public GameObjectQueue readTransformerConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(transformerName, transformerAttributes,
				TRANSFORMER_OBJECT, null, isOrdinal);
	}

	public GameObjectQueue readMapConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(mapName, mapAttributes, MAP_OBJECT,
				associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readLevelConfigure(
			GameObjectQueue[] associatedTableArray, boolean isOrdinal) {
		return readGameObjectConfigure(levelName, levelAttributes,
				LEVEL_OBJECT, associatedTableArray, isOrdinal);
	}

	public GameObjectQueue readCameraConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(cameraName, cameraAttributes,
				CAMERA_OBJECT, null, isOrdinal);
	}

	public GameObjectQueue readMusicConfigure(boolean isOrdinal) {
		return readGameObjectConfigure(musicName, musicAttributes,
				MUSIC_OBJECT, null, isOrdinal);
	}

	public int readCarnieRunInterval(boolean isOrdinal) {
		return readGlobalConfigureForCarnieRunInterval(globalName,
				globalAttributes, isOrdinal);
	}

	public static void main(String[] argv) {
		XmlScriptParser x = new XmlScriptParser();
		x.openConfigure("/configure/config.xml");
		GameObjectQueue msgTable = x.readMsgConfigure(true);
		System.out.println("msg数量=" + msgTable.size());
		GameObjectQueue mqTable = x.readMsgQueueConfigure(
				new GameObjectQueue[] { msgTable }, true);
		System.out.println("msg Queue数量=" + mqTable.size());

		GameObjectQueue eventTable = x.readEventConfigure(true);
		System.out.println("event数量=" + eventTable.size());
		GameObjectQueue eqTable = x.readEventQueueConfigure(
				new GameObjectQueue[] { eventTable }, true);
		System.out.println("event Queue数量=" + eqTable.size());

		GameObjectQueue propTable = x.readPropertyConfigure(true);
		System.out.println("property数量=" + propTable.size());
		GameObjectQueue propBoxTable = x.readPropertyBoxConfigure(
				new GameObjectQueue[] { propTable }, true);
		System.out.println("propertyBox数量=" + propBoxTable.size());

		GameObjectQueue npcTable = x.readNpcConfigure(new GameObjectQueue[] {
				propTable, eventTable }, true);
		System.out.println("npc数量=" + npcTable.size() + " npc[npc01].name="
				+ (NPC) npcTable.get("npc01"));

		GameObjectQueue actorTable = x.readActorConfigure(
				new GameObjectQueue[] { propTable }, true);
		System.out.println("actor数量=" + actorTable.size());

		GameObjectQueue layerTable = x.readLayerConfigure(true);
		System.out.println("layer数量=" + layerTable.size());

		GameObjectQueue transformerTable = x.readTransformerConfigure(true);
		System.out.println("transformer数量=" + transformerTable.size());

		GameObjectQueue mapTable = x.readMapConfigure(new GameObjectQueue[] {
				layerTable, npcTable, transformerTable }, true);
		System.out.println("map数量=" + mapTable.size());

		GameObjectQueue levelTable = x.readLevelConfigure(
				new GameObjectQueue[] { mapTable }, true);
		System.out.println("level数量=" + levelTable.size());

		GameObjectQueue cameraTable = x.readCameraConfigure(true);
		System.out.println("camera数量=" + cameraTable.size());

		GameObjectQueue musicTable = x.readMusicConfigure(true);
		System.out.println("music数量=" + musicTable.size());
		System.out.println("Carnie运行间隔=" + x.readCarnieRunInterval(true));
	}
}
