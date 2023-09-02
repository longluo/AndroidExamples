package com.longluo.GameEngine.Music;


import java.util.Vector;

import com.longluo.GameEngine.GameActivity;
import com.longluo.GameEngine.GameObject;

import android.media.MediaPlayer;

//音乐类：定义音乐资源ID、资源URL、播放方式、播放循环次数
public class Music extends GameObject {
	// 播放方式：无限循环
	public static final int INFINITE_LOOP = 1;
	// 播放方式：有限次数播放
	public static final int FINITE_LOOP = 2;
	// 资源URL
	private String resURL = null;
	// 音乐类型
	private String musicType = null;
	// 播放方式
	private int playModel = 0;
	// 循环次数，在有限次数播放时有效
	private int loopNumber = 0;
	// 音乐播放器
	private MediaPlayer musicPlayer = null;
	// 当前播放次数
	private int currentPlayTimes = 0;

	public Music() {
		super();
		currentPlayTimes = 0;
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.resURL = (String) v.elementAt(1);
		this.musicType = (String) v.elementAt(2);
		this.playModel = Integer.parseInt((String) v.elementAt(3));
		this.loopNumber = Integer.parseInt((String) v.elementAt(4));
		try {
			musicPlayer = MediaPlayer.create(GameActivity.mContext,
					Integer.parseInt(resURL));
			musicPlayer.prepare();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 是否播放完毕，对于有限播放次数的播放方式有效
	 * 
	 * @return 如果达到播放的循环次数，则返回true
	 */
	public boolean isPlayEnd() {
		if (playModel == FINITE_LOOP) {
			return (currentPlayTimes >= loopNumber);
		} else {
			return false;
		}
	}

	/**
	 * 增加播放次数
	 * 
	 */
	public void increasePlayTimes() {
		currentPlayTimes++;
	}

	public int getLoopNumber() {
		return loopNumber;
	}

	public int getPlayModel() {
		return playModel;
	}

	public String getResURL() {
		return resURL;
	}

	public String getMusicType() {
		return musicType;
	}

	public MediaPlayer getMusicPlayer() {
		return musicPlayer;
	}

	public String toString() {
		return super.toString() + " resURL=" + this.resURL + " musicType="
				+ this.musicType + " playModel=" + this.playModel
				+ " loopNumber=" + this.loopNumber;
	}
}
