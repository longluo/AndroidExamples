package com.longluo.GameEngine.Material;

//运动类：记录Player和NPC的运动速度和运动方向
public class Movement {
	// 运动方向：向左
	public static final int LEFT_MOVE = 1;
	// 运动方向：向右
	public static final int RIGHT_MOVE = 2;
	// 运动方向：向上
	public static final int UP_MOVE = 3;
	// 运动方向：向下
	public static final int DOWN_MOVE = 4;
	// 每步速度（大于0），即每步移动的距离，这里假定x轴和y轴的步速相同
	private int stepSpeed = 0;
	// 移动方向
	private int moveDirection = 0;

	public Movement(int stepSpeed, int moveDirection) {
		this.stepSpeed = stepSpeed;
		this.moveDirection = moveDirection;
	}

	public int getStepSpeed() {
		return stepSpeed;
	}

	public void setStepSpeed(int stepSpeed) {
		this.stepSpeed = stepSpeed;
	}

	public int getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(int moveDirection) {
		this.moveDirection = moveDirection;
	}
}
