package com.longluo.GameEngine.Events;

import java.util.Vector;

import com.longluo.GameEngine.GameObject;

//œ˚œ¢¿‡
public class Message extends GameObject {
	private String msgContent = null;

	public Message() {
		super();
	}

	public Message(String msgID, String msgContent) {
		super();
		this.msgContent = msgContent;
		this.setId(msgID);
	}

	public void loadProperties(Vector valueSet) {
		this.msgContent = (String) valueSet.elementAt(0);
		this.setId((String) valueSet.elementAt(1));
	}

	public String getMsgContent() {
		return msgContent;
	}

	public String toString() {
		return super.toString() + " msgContent=" + msgContent;
	}
}
