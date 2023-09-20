package com.bn.core;
//计算内存消耗情况,这里只考虑的是对象所消耗堆内存空间
public class Memory 
{
	public static long used()
	{
		long total=Runtime.getRuntime().totalMemory();
		long free=Runtime.getRuntime().freeMemory();
		return total-free;
	}
}
