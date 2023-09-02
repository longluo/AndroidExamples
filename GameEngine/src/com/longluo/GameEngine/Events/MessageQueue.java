package com.longluo.GameEngine.Events;

import com.longluo.GameEngine.GameObjectQueue;

//消息队列类：用于显示角色语言和NPC语言、系统的信息
public class MessageQueue extends GameObjectQueue {
	public MessageQueue() {
		super();
	}

	/**
	 * 查询消息
	 * 
	 * @param msgID
	 *            消息ID
	 * @return 返回的显示信息，如果没找到则返回null
	 */
	public String findContent(String msgID) {
		if (this.containsKey(msgID)) {
			if (this.get(msgID) == null) {
				return null;
			} else {
				return this.get(msgID).toString();
			}
		} else {
			return null;
		}
	}
}
