package com.longluo.GameEngine.Events;

import java.util.Vector;

import com.longluo.GameEngine.GameObjectQueue;

//事件队列类：用于按照FIFO的顺序保存游戏中的事件
public class EventQueue extends GameObjectQueue {
	public EventQueue() {
		super();
	}

	/**
	 * 查询指定事件ID对应的事件对象
	 * 
	 * @param eventID
	 *            查询使用到的指定事件ID
	 * @return 指定事件ID对应的事件对象
	 */
	public Event PollEvent(String eventID) {
		if (this.containsKey(eventID)) {
			return (Event) this.get(eventID);
		} else {
			return null;
		}
	}

	/**
	 * 查询指定事件ID对应的事件对象，并作为事件数组返回
	 * 
	 * @param eventID
	 *            查询使用到的指定事件ID数组
	 * @return 指定事件ID对应的事件对象数组，没有发现对应的事件则返回null
	 */
	public Event[] PollEvent(String[] eventIDArray) {
		Vector polledList = new Vector();
		for (int i = 0; i < eventIDArray.length; i++)
			if (this.containsKey(eventIDArray[i])) {
				polledList.addElement(this.get(eventIDArray[i]));
			}

		if (polledList.size() > 0) {
			Event[] result = new Event[polledList.size()];
			polledList.copyInto(result);
			return result;
		} else {
			return null;
		}
	}
}
