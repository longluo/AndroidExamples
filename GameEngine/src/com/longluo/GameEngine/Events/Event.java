package com.longluo.GameEngine.Events;

import java.util.Vector;

import com.longluo.GameEngine.GameObject;

//事件类:对于对话类事件，一个事件对应着一个消息队列MessageQueue
public class Event extends GameObject {
	// 对话类型事件：当需要主角和NPC对话时触发此事件
	public static final int TALK_EVENT = 1;
	// 战斗类型事件：当需要主角和NPC作战时触发此事件
	public static final int FIGHT_EVENT = 2;

	// 事件发起者
	private String invoker = null;
	// 事件响应者
	private String responser = null;
	// 事件类型
	private int type = 0;
	// 参数
	private String parameter = null;

	public Event() {
		super();
	}

	public void loadProperties(Vector attrValueSet) {
		this.setId((String) attrValueSet.elementAt(0));
		this.invoker = (String) attrValueSet.elementAt(1);
		this.responser = (String) attrValueSet.elementAt(2);
		this.type = Integer.parseInt((String) attrValueSet.elementAt(3));
		this.parameter = (String) attrValueSet.elementAt(4);
	}

	public String getInvoker() {
		return invoker;
	}

	public String getResponser() {
		return responser;
	}

	public int getType() {
		return type;
	}

	public String getParameter() {
		return parameter;
	}

	public String toString() {
		return super.toString() + " invoker=" + invoker + " responser="
				+ responser + " type=" + type + " parameter=" + parameter;
	}
}
/**
 * 事件示例： EVENT01：HERO NPC01 TALK 说明： EVENT01表示eventID，即注册事件ID； HERO表示事件发生的主体；
 * NPC01表示参与事件的被动体； TALK表示对话类型事件
 **/
