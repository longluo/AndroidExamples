package com.longluo.GameEngine.Screen.elements.Property;

import java.util.Vector;

import com.longluo.GameEngine.GameObject;

//道具类
public class Property extends GameObject {
	// 治疗类道具:用于恢复主角生命力
	public static final int MEDICINE_PROP = 1;
	// 攻击武器类道具:增加主角的攻击力
	public static final int ATTACK_WEAPON_PROP = 2;
	// 防御武器类道具:增加主角的防御力
	public static final int DEFENCE_WEAPON_PROP = 3;
	// 情节类道具：作为用于过关的条件，如钥匙等
	public static final int SCENARIO_PROP = 4;

	// 道具名称
	private String name = null;
	// 道具的描述
	private String description = null;
	// 道具买进价格（玩家买进）
	private int buyPrice = 0;
	// 道具卖出价格（玩家卖出）
	private int salePrice = 0;
	// 治疗效果（对生命值的影响）
	private int lifeEffect = 0;
	// 攻击力增加效果（对攻击值的影响）
	private int attackEffect = 0;
	// 防御力增加效果（对防御值的影响）
	private int defenceEffect = 0;
	// 耐用次数，如药品只能用一次，而小刀能许多次
	private int useTimes = 0;
	// 道具类型
	private int type = 0;

	public Property() {
		super();
	}

	public void loadProperties(Vector v) {
		this.setId((String) v.elementAt(0));
		this.name = ((String) v.elementAt(1));
		this.description = ((String) v.elementAt(2));
		this.buyPrice = (Integer.parseInt((String) v.elementAt(3)));
		this.salePrice = (Integer.parseInt((String) v.elementAt(4)));
		this.lifeEffect = (Integer.parseInt((String) v.elementAt(5)));
		this.attackEffect = (Integer.parseInt((String) v.elementAt(6)));
		this.defenceEffect = (Integer.parseInt((String) v.elementAt(7)));
		this.useTimes = (Integer.parseInt((String) v.elementAt(8)));
		this.type = (Integer.parseInt((String) v.elementAt(9)));
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	public String getDescription() {
		return description;
	}

	public int getLifeEffect() {
		return lifeEffect;
	}

	public String getName() {
		return name;
	}

	public int getSalePrice() {
		return salePrice;
	}

	public int getUseTimes() {
		return useTimes;
	}

	public int getAttackEffect() {
		return attackEffect;
	}

	public int getDefenceEffect() {
		return defenceEffect;
	}

	public int getType() {
		return type;
	}

	public String toString() {
		return super.toString() + " name=" + name + " description="
				+ description + " buyPrice=" + buyPrice + " salePrice="
				+ salePrice + " lifeEffect=" + lifeEffect + " attackEffect="
				+ attackEffect + " defenceEffect=" + defenceEffect
				+ " useTimes=" + useTimes + " type=" + type;
	}
}
