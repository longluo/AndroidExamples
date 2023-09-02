package com.longluo.GameEngine;

public class GameDefinition {
	/* 游戏状态 */
	public static final int Game_Logo = 0;
	public static final int Game_MainMenu = 1;
	public static final int Game_Set = 2;
	public static final int Game_Help = 3;
	public static final int Game_About = 4;
	public static final int Game_Over = 5;
	// 显示下一条信息(用于对话模式)
	public static final int PAGEDOWN_KEYPRESS = 11;
	// 确认(用于对话模式)
	public static final int OK_KEYPRESS = 12;
	// 取消(用于对话模式)
	public static final int CANCEL_KEYPRESS = 13;
}
