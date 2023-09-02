package com.longluo.GameEngine.Screen.elements.biology;

import java.util.Vector;

import com.longluo.GameEngine.GameActivity;
import com.longluo.GameEngine.GameObject;
import com.longluo.GameEngine.Events.EventQueue;
import com.longluo.GameEngine.Material.Movement;
import com.longluo.GameEngine.Screen.animation.Animator;
import com.longluo.GameEngine.Util.Coordinates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



//实现Biology接口的动物类
public class Animal extends GameObject implements Biology {
	// 名称
	private String name = null;
	// 生命值
	private int life = 0;
	// 攻击值
	private int attack = 0;
	// 防御值
	private int defence = 0;
	// 主角是否生存
	private boolean alive = true;
	// 图片/动画URL
	private String imgURL = null;
	// 脸部图片URL
	private String faceURL = null;
	// 事件对列：当主角与NPC发生碰撞时，使用事件ID调用事件对列中的某个事件。
	private EventQueue eventQueue = null;
	// 坐标
	private Coordinates co = null;
	// 运动方向和速度
	private Movement movement = null;
	// 动画对象
	private Animator ani = null;

	public Animal() {
		super();
	}

	/**
	 * 只使用前14个属性
	 */
	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.name = (String) v.elementAt(1);
		this.life = Integer.parseInt((String) v.elementAt(2));
		this.attack = Integer.parseInt((String) v.elementAt(3));
		this.defence = Integer.parseInt((String) v.elementAt(4));
		this.imgURL = (String) v.elementAt(5);
		this.faceURL = (String) v.elementAt(6);
		int col = Integer.parseInt((String) v.elementAt(7));
		int row = Integer.parseInt((String) v.elementAt(8));
		this.co = new Coordinates(col, row);
		int stepSpeed = Integer.parseInt((String) v.elementAt(9));
		int moveDirection = Integer.parseInt((String) v.elementAt(10));
		this.movement = new Movement(stepSpeed, moveDirection);
		this.alive = true;
		int animationLoopTime = Integer.parseInt((String) v.elementAt(11));
		int frameWidth = Integer.parseInt((String) v.elementAt(12));
		int frameHeight = Integer.parseInt((String) v.elementAt(13));

		try {
			Bitmap img = BitmapFactory.decodeResource(
					GameActivity.mContext.getResources(),
					Integer.parseInt(this.imgURL));
			this.ani = new Animator(img, frameWidth, frameHeight,
					animationLoopTime);
			this.ani.setRefPixelPosition(col * frameWidth, row * frameHeight);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 修改名字
	 * 
	 * @param name
	 */
	public void changeName(String name) {
		this.name = name;
	}

	/**
	 * 返回动物的名字
	 * 
	 * @return 动物的名字
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 修改动物的图片/动画URL
	 * 
	 * @param imgURL
	 */
	public void changeImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	/**
	 * 返回图片URL
	 * 
	 * @return 图片URL
	 */
	public String getImgURL() {
		return this.imgURL;
	}

	/**
	 * 由于受到伤害或者其因素减少动物的生命力，如果生命值小于MINLIFE，则表明动物已死亡。
	 * 
	 * @param detaLife
	 *            生命力减少量
	 * @return 返回动物的生命力
	 */
	public int decreaseLife(int detaLife) {
		if (alive) {
			if (life > MINLIFE) {
				life -= detaLife;
				if (life < MINLIFE) {
					alive = false;
				}
			}
		}
		return life;
	}

	/**
	 * 由于使用物品或其它原因，动物的生命力增加
	 * 
	 * @param detaLife
	 *            生命力增加量
	 * @return 动物的生命力
	 */
	public int increaseLife(int detaLife) {
		if (alive) {
			if ((life + detaLife) <= MAXLIFE) {
				life += detaLife;
			} else {
				life = MAXLIFE;
			}
		}
		return life;
	}

	/**
	 * 判断动物是否生存
	 * 
	 * @return 如果动物活着，返回true，否则返回false
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * 获得运动速度和方向
	 * 
	 * @return Movement对象
	 */
	public Movement getMovement() {
		return movement;
	}

	/**
	 * 设置运动速度和方向
	 * 
	 * @param movement
	 *            Movement对象
	 */
	public void setMovement(Movement movement) {
		this.movement = movement;
	}

	/**
	 * 获得动物当前的坐标
	 * 
	 * @return 动物当前的坐标
	 */
	public Coordinates getCoordinates() {
		return co;
	}

	public int getAttack() {
		return attack;
	}

	public int increaseAttack(int detaAttack) {
		this.attack += detaAttack;
		return this.attack;
	}

	public int getDefence() {
		return defence;
	}

	public int increaseDefence(int detaDefence) {
		this.defence += detaDefence;
		return this.defence;
	}

	public EventQueue getEventQueue() {
		return eventQueue;
	}

	public String getFaceURL() {
		return faceURL;
	}

	public void setCoordinate(Coordinates co) {
		this.co = co;
	}

	public Animator getAnimator() {
		return ani;
	}

	public void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	public int getLife() {
		return this.life;
	}

	public String toString() {
		return super.toString() + " name=" + name + " life=" + life
				+ " attack=" + attack + " defence=" + defence + " imgURL="
				+ imgURL + " faceURL=" + faceURL + " col=" + co.getX()
				+ " row=" + co.getY() + " speed=" + movement.getStepSpeed()
				+ " direction=" + movement.getMoveDirection() + " eventQueue="
				+ eventQueue + " animator=" + ani;

	}
}
