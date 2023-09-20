package com.bn.clp;

import com.bn.R;

public class BoatInfo 
{
	public static int cuttBoatIndex=2;
	//船部件的名字
	public static String[][] boatPartNames=
	{
		{
	 		"one_chuanshen.obj",
	 		"one_dizuo.obj",
	 		"one_houmian.obj",   
	 		"one_qiandizuo.obj",
	 		"one_qianmian.obj" 
	 	}, 
	 	{ 
	 		"three_chuanshen.obj",
	 		"three_liangce.obj",
	 		"three_pentong.obj",
	 		"three_shangmian.obj",
	 		"three_shangmianqian.obj",
	 		"three_weiyi.obj"
		},
		{//彭彭
			"two_ceyi.obj",
			"two_chuanshen.obj",   
			"two_pentong.obj", 
			"two_shangmian.obj",
			"two_weijia.obj",   
			"two_weiyi.obj"     
		 } 
	};
	 
	public final static int[][] boatTexIdName=
	{
		{
			R.raw.one_chuanshen,
			R.raw.one_dizuo,
			R.raw.one_houmian,
			R.raw.one_qiandizuo,
			R.raw.one_qianmian
		},
		{
			R.raw.three_chuanshen,
			R.raw.three_liangce,
			R.raw.three_pentong,
			R.raw.three_shangmian, 
			R.raw.three_shangmianqian,
			R.raw.three_weiyi
		},
		{
			R.drawable.two_ceyi,
			R.drawable.two_chuanshen,
			R.drawable.two_pentong,
			R.drawable.two_shangmian,
			R.drawable.two_weijia,
			R.drawable.two_weiyi
		}
	};
}