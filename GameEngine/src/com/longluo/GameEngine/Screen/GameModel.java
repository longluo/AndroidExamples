package com.longluo.GameEngine.Screen;

/**
 * 游戏模式类：描述当前游戏的模式：运动、对话。 其中运动模式指主角按照玩家的键盘操作而行为。
 * 对话模式是指主角和NPC交互时使用的对话框模式，玩家可通过键盘控制“选项”的选择。
 * 
 */
public class GameModel {
	// 运动模式
	public static final int FREEMOVE_MODEL = 1;
	// 对话模式
	public static final int DIALOG_MODEL = 2;
	// 当前的模式类型
	private int modelType = 0;

	public GameModel() {
		modelType = FREEMOVE_MODEL;
	}

	public GameModel(int gameModel) {
		this.modelType = gameModel;
	}

	public int getModelType() {
		return modelType;
	}

	public void setModelType(int modelType) {
		this.modelType = modelType;
	}

	/**
	 * 判断当前是否是运动模式
	 * 
	 * @return 如果是运动模式，则返回true，否则返回false
	 */
	public boolean isMoveModel() {
		return (modelType == FREEMOVE_MODEL);
	}

	/**
	 * 判断当前是否是对话模式
	 * 
	 * @return 如果是对话模式，则返回true，否则返回false
	 */
	public boolean isDialogModel() {
		return (modelType == DIALOG_MODEL);
	}
}
