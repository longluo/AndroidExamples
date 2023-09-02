package com.longluo.GameEngine.Screen.elements.biology;

import java.util.Vector;

import com.longluo.GameEngine.Material.Movement;
import com.longluo.GameEngine.Screen.elements.Property.PropertyManager;
import com.longluo.GameEngine.Util.StringExtension;

public class NPC extends Animal {
	// 功能类NPC：在游戏中担任功能性工作-如卖道具
	public static final int FUNCTIONAL_NPC = 1;
	// 情节类NPC：对游戏的故事情节发生作用-如城门的士兵
	public static final int SCENARIO_NPC = 2;
	// 非交互类NPC：在游戏中只与主角和其它NPC进行碰撞检测
	public static final int NOINTERACTION_NPC = 3;
	// 帧切换数组，对应着主角的上、下、左、右四个方向的帧图片
	private int[] frameSwtichSequence = null;

	// NPC的种类
	private int type = 0;
	// 道具箱
	private PropertyManager propBox = null;

	public NPC() {
		super();
	}

	/**
	 * 返回NPC的道具箱
	 * 
	 * @return 道具管理对象
	 */
	public PropertyManager getPropertyBox() {
		return propBox;
	}

	/**
	 * 返回NPC的类型
	 * 
	 * @return NPC的类型
	 */
	public int getType() {
		return type;
	}

	public void setPropertyBox(PropertyManager propBox) {
		this.propBox = propBox;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 重载父类的loadProperties，读取帧切换序列
	 */
	public void loadProperties(Vector v) {
		super.loadProperties(v);
		// System.out.println("FrameSequenceLength="+this.getAnimator().getFrameSequenceLength());
		Object[] objArray = StringExtension.split(
				new StringBuffer((String) v.elementAt(14)), ",",
				StringExtension.INTEGER_ARRAY, false);
		if (objArray != null) {
			frameSwtichSequence = StringExtension
					.objectArrayBatchToIntArray(objArray);
		} else {
			frameSwtichSequence = null;
		}
	}

	/**
	 * 根据运动的方向和每步速度移动,每次移动一个主角的动画桢的位置
	 */
	public void move() {
		int x = this.getAnimator().getX();
		int y = this.getAnimator().getY();
		// 移动动画位置，每次移动一个主角的动画桢的位置
		switch (this.getMovement().getMoveDirection()) {
		case Movement.LEFT_MOVE:
			x -= this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[2]);
			getAnimator().flushPosition(x, y);
			break;
		case Movement.RIGHT_MOVE:
			x += this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[3]);
			getAnimator().flushPosition(x, y);
			break;
		case Movement.UP_MOVE:
			y -= this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[0]);
			getAnimator().flushPosition(x, y);
			break;
		case Movement.DOWN_MOVE:
			y += this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[1]);
			getAnimator().flushPosition(x, y);
			break;
		}

	}

	/**
	 * 根据运动的方向和每步速度反向移动，用于做前面取消移动效果的目的
	 */
	public void undoMove() {
		int x = this.getAnimator().getX();
		int y = this.getAnimator().getY();
		// 移动动画位置
		switch (this.getMovement().getMoveDirection()) {
		case Movement.LEFT_MOVE:
			x += this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[2]);
			getAnimator().flushPosition(x, y);
			break;
		case Movement.RIGHT_MOVE:
			x -= this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[3]);
			getAnimator().flushPosition(x, y);
			break;
		case Movement.UP_MOVE:
			y += this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[0]);
			getAnimator().flushPosition(x, y);
			break;
		case Movement.DOWN_MOVE:
			y -= this.getMovement().getStepSpeed();
			getAnimator().setFrame(frameSwtichSequence[1]);
			getAnimator().flushPosition(x, y);
			break;
		}
	}

	public String toString() {
		return super.toString() + " propBox=" + propBox;
	}
}
