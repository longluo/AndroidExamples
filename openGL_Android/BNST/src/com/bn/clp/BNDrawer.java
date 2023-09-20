package com.bn.clp;

/*
 * 该类是一个抽象类，所有的3D物体的绘制均继承自该类
 */
public abstract class BNDrawer
{
	public abstract void drawSelf(int[] texId,int dyFlag);
}      