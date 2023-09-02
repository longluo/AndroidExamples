package com.longluo.GameEngine.Screen.elements.biology;

//生物接口：定义了具有生命力的动/植物行为
public interface Biology {
	// 最大生命力
	public static final int MAXLIFE = 100;
	// 最小生命力
	public static final int MINLIFE = 1;

	/**
	 * 由于受到伤害或者其因素减少主角的生命力，如果生命值小于MINLIFE，则表明主角已死亡。
	 * 
	 * @param detaLife
	 *            生命力减少量
	 * @return 返回主角的生命力
	 */
	public int decreaseLife(int detaLife);

	/**
	 * 由于使用物品或其它原因，主角的生命力增加
	 * 
	 * @param detaLife
	 *            生命力增加量
	 * @return 主角的生命力
	 */
	public int increaseLife(int detaLife);
}
