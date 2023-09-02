package com.longluo.GameEngine.Screen.elements.biology;

import java.util.Vector;

import com.longluo.GameEngine.Material.Movement;
import com.longluo.GameEngine.Screen.GameModel;
import com.longluo.GameEngine.Screen.elements.Property.Property;
import com.longluo.GameEngine.Screen.elements.Property.PropertyManager;
import com.longluo.GameEngine.Util.StringExtension;

public class Actor extends Animal {
	// 道具箱
	private PropertyManager propBox = null;
	// 游戏模式
	private GameModel gameModel = null;
	// 帧切换数组，对应着主角的上、下、左、右四个方向的帧图片
	private int[] frameSwtichSequence = null;

	public Actor() {
		super();
		gameModel = new GameModel();
		gameModel.setModelType(GameModel.FREEMOVE_MODEL);
	}

	/**
	 * 重载父类的loadProperties，读取帧切换序列
	 */
	public void loadProperties(Vector v) {
		super.loadProperties(v);
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
	 * 获得主角的道具箱
	 * 
	 * @return 道具管理对象
	 */
	public PropertyManager getPropertyBox() {
		return propBox;
	}

	/**
	 * 设置主角的道具箱
	 * 
	 * @param 道具管理对象
	 */
	public void setPropertyBox(PropertyManager propBox) {
		this.propBox = propBox;
	}

	/**
	 * 装备道具，从道具箱中注销
	 * 
	 * @param prop
	 *            要装备的道具
	 */
	public void equipProperty(Property prop) {
		switch (prop.getType()) {
		case Property.MEDICINE_PROP:
			increaseLife(prop.getLifeEffect());
			break;
		case Property.ATTACK_WEAPON_PROP:
			increaseAttack(prop.getAttackEffect());
			break;
		case Property.DEFENCE_WEAPON_PROP:
			increaseDefence(prop.getDefenceEffect());
		}
		propBox.unRegisterProperty(prop.getId());
	}

	/**
	 * 返回当前的游戏模式
	 * 
	 * @return 游戏模式
	 */
	public GameModel getGameModel() {
		return gameModel;
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
		if (frameSwtichSequence != null) {
			StringBuffer tmp = new StringBuffer();
			for (int i = 0; i < frameSwtichSequence.length; i++) {
				if (i < frameSwtichSequence.length - 1) {
					tmp.append(frameSwtichSequence[i] + ",");
				} else {
					tmp.append(frameSwtichSequence[i]);
				}
			}
			return super.toString() + " frameSwtichSequence=" + tmp.toString();
		} else {
			return super.toString();
		}
	}
}
