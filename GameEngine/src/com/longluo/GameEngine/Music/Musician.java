package com.longluo.GameEngine.Music;

import java.util.Enumeration;

import com.longluo.GameEngine.GameObjectQueue;

import android.media.MediaPlayer;

//音乐家类：负责管理各种Music对象
public class Musician extends GameObjectQueue {
	public Musician() {
		super();
	}

	/**
	 * 从音乐家的手中取出某个音乐，被取出的音乐将作为当前音乐
	 * 
	 * @param musicID
	 *            音乐资源ID
	 * @return 音乐对象
	 */
	public Music takeMusicFromMusicBox(String musicID) {
		if (this.containsKey(musicID)) {
			return (Music) this.get(musicID);
		} else {
			return null;
		}
	}

	/**
	 * 从音乐家的手中取出某个音乐，被取出的音乐将作为当前音乐
	 * 
	 * @param player
	 *            Player对象
	 * @return 音乐对象
	 */
	public Music takeMusicFromMusicBox(MediaPlayer player) {
		Enumeration emu = this.elements();
		while (emu.hasMoreElements()) {
			Music music = (Music) emu.nextElement();
			if (music.getMusicPlayer().equals(player)) {
				return music;
			}
		}
		return null;
	}

	/**
	 * 将音乐对象交给音乐家
	 * 
	 * @param music
	 *            音乐对象
	 */
	public void putMusicIntoMusicBox(Music music) {
		this.put(music.getId(), music);
	}
}
