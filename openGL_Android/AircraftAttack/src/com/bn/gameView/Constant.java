package com.bn.gameView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.bn.archieModel.ArchieForControl;
import com.bn.arsenal.Arsenal_House;
import com.bn.menu.R;
import com.bn.tankemodel.TanKe;

public class Constant 
{	
	public static int isMusicOn=0;//背景音乐   0表示开启,1表示关闭/
	public static int isSoundOn=0;//特效音乐   0表示开启,1表示关闭
	public static int isVibrateOn=0;//特效震动   0表示开启,1表示关闭
	//设置导弹菜单的宽度和高度
	public static float MENU_BUTTON_WIDTH; 
	public static float MENU_BUTTON_HEIGHT;  
	//设置导弹菜单的偏移量
	public static final float MENU_BUTTON_XOffset=0;
	public static final float MENU_BUTTON_YOffset=0;
	//导弹菜单的范围
	public static float[] MENU_BUTTON_AREA;
	//机舱们的宽度和高度
	public static float MENU_DOOR_WIDTH; 
	public static float MENU_DOOR_HEIGHT;  
	//设置页面按钮的宽度和高度
	public static float SETTING_BUTTON_WIDTH; 
	public static float SETTING_BUTTON_HEIGHT;  
	//设置页面按钮的偏移量1
	public static float SETTING_BUTTON_XOffset1=0;
	public static float SETTING_BUTTON_YOffset1=0.5f;
	//设置页面按钮的范围1
	public static float[] SETTING_BUTTON_AREA1;
	//设置页面按钮的偏移量2
	public static float SETTING_BUTTON_XOffset2=0;
	public static float SETTING_BUTTON_YOffset2=0;
	//设置页面按钮的范围2
	public static float[] SETTING_BUTTON_AREA2;
	//设置页面按钮的偏移量3
	public static float SETTING_BUTTON_XOffset3=0;
	public static float SETTING_BUTTON_YOffset3=-0.5f;
	//设置页面按钮的范围3
	public static float[] SETTING_BUTTON_AREA3;
	//退出对话框的宽度和高度 包括标头和按钮
	public static float EXIT_DIALOG_WIDTH; 
	public static float EXIT_DIALOG_HEIGHT;  
	//确定按钮的范围
	public static float DIALOG_BUTTON_WIDTH; 
	public static float  DIALOG_BUTTON_HEIGHT;
	public static float DIALOG_YES_XOffset;
	public static float DIALOG_YES_YOffset;
	public static float[] DIALOG_BUTTON_YES;
	//返回按钮的范围
	public static float DIALOG_NO_XOffset;
	public static float DIALOG_NO_YOffset;
	public static float[] DIALOG_BUTTON_NO;
	//帮助界面的宽度和高度
	public static float HELP_WIDTH; 
	public static float HELP_HEIGHT;  
	//关于界面的宽度和高度
	public static float ABOUT_WIDTH; 
	public static float ABOUT_HEIGHT;  
	
	//-------------二级菜单---------------------------
	//场景中的标头
	public static float PLANE_SELECT_HEAD_WIDTH;
	public static float PLANE_SELECT_HEAD_HEIGHT;
	//选飞机按钮
	public static float PLANE_SELECT_PLANE_WIDTH;
	public static float PLANE_SELECT_PLANE_HEIGHT;
	public static float PLANE_BTN_XOffset;//按钮的偏移量
	public static float PLANE_BTN_YOffset;
	public static float[] PLANE_SELECT_PLANE;//按钮的范围
	
	
	//-----游戏模式按钮
	public static float MENU_TWO_GAME_MODEL_BUTTON_WIDTH;
	public static float MENU_TWO_GAME_MODEL_BUTTON_HEIGHT;
	//------战役模式按钮
	public static float MENU_TWO_WAR_BUTTON_XOffset;//按钮的偏移量
	public static float MENU_TWO_WAR_BUTTON_YOffset;
	public static float[] MENU_TWO_WAR_BUTTON_AREA;//按钮的范围
	//------特别行动按钮
	public static float MENU_TWO_ACTION_BUTTON_XOffset;//按钮的偏移量
	public static float MENU_TWO_ACTION_BUTTON_YOffset;
	public static float[] MENU_TWO_ACTION_BUTTON_AREA;//按钮的范围
	
	
	//------菜单二中按钮的宽度和高度
	public static float MENU_TWO_BUTTON_WIDTH;
	public static float MENU_TWO_BUTTON_HEIGHT;
	//--------确定按钮的范围
	public static float MENU_TWO_BUTTON_OK_XOffset;//按钮的偏移量
	public static float MENU_TWO_BUTTON_OK_YOffset;
	public static float[] MENU_TWO_BUTTON_OK_AREA;//按钮的范围
	//--------左按按钮的范围
	public static float MENU_TWO_BUTTON_LEFT_XOffset;//按钮的偏移量
	public static float MENU_TWO_BUTTON_LEFT_YOffset;
	public static float[] MENU_TWO_BUTTON_LEFT_AREA;//按钮的范围
	//--------右按按钮的范围
	public static float MENU_TWO_BUTTON_RIGHT_XOffset;//按钮的偏移量
	public static float MENU_TWO_BUTTON_RIGHT_YOffset;
	public static float[] MENU_TWO_BUTTON_RIGHT_AREA;//按钮的范围
	//----二级菜单中的飞机图片宽度和高度
	public static float MENU_TWO_PLANE_ICON_WIDTH;
	public static float MENU_TWO_PLANE_ICON_HEIGHT;
	
	public static float MENU_TWO_PLANE_ICON_ONE_XOffset;//按钮的偏移量
	public static float MENU_TWO_PLANE_ICON_ONE_YOffset;
	public static float[] MENU_TWO_PLANE_ICON_ONE_AREA;//按钮的范围
	public static float MENU_TWO_PLANE_ICON_TWO_XOffset;//按钮的偏移量
	public static float MENU_TWO_PLANE_ICON_TWO_YOffset;
	public static float[] MENU_TWO_PLANE_ICON_TWO_AREA;//按钮的范围
	public static float MENU_TWO_PLANE_ICON_THREE_XOffset;//按钮的偏移量
	public static float MENU_TWO_PLANE_ICON_THREE_YOffset;
	public static float[] MENU_TWO_PLANE_ICON_THREE_AREA;//按钮的范围
	
	
	//-------游戏中说明文字的宽度和高度
	public static float NOTICE_WIDTH;
	public static float NOTICE_HEIGHT;
	//----------------------------------------------------------
	
	//-----三级菜单------地图选择界面按钮的宽度和高度
	public static float MAP_BUTTON_WIDTH; 
	public static float MAP_BUTTON_HEIGHT;  
	//第一关的范围
	public static float MAP_ONE_XOffset;
	public static float MAP_ONE_YOffset;
	public static float[] MAP_ONE_AREA;
	//第二关的范围
	public static float MAP_TWO_XOffset;
	public static float MAP_TWO_YOffset;
	public static float[] MAP_TWO_AREA;
	//第三关的范围
	public static float MAP_THREE_XOffset;
	public static float MAP_THREE_YOffset;
	public static float[] MAP_THREE_AREA;
	
	//地图选择界面按钮的宽度和高度
	public static float MAP_WORD_WIDTH; 
	public static float MAP_WORD_HEIGHT; 
	public static float WORD_YOffset;
	
	
	//排行榜界面地图的宽度和高度
	public static float RANK_MAP_WIDTH; 
	public static float RANK_MAP_HEIGHT;  
	
	//排行榜界面数字宽度和高度
	public static float RANK_NUMBER_WIDTH; 
	public static float RANK_NUMBER_HEIGHT;  
	
	public static final int ARCHIBALD_TIME=1;//高射炮被射击最高次数后爆炸
	public static final float ARCHIBALD_BOX_X=8;//高射炮的包围盒大小
	public static final float ARCHIBALD_BOX_Y=12;
	public static final float ARCHIBALD_BOX_Z=8;
	
	public static final int CELL_SIZE=15;//九宫格的一边的个数
	public static final int TANKE_SIZE=15;//绘制坦克等各种物体的九宫格数
	public static final int MapArray[][][]=new int[][][]
    {
		//第一关
		{
			{14,8 ,5 ,7 ,14,	14,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14,},
			{14,6 ,13,10,7 ,	14,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14,},//0
			{8 ,11,13,2 ,1 ,	14,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14,},//1
			{9 ,12,13,4 ,14,	14,14,14,14,14,	8 ,7 ,14,14,14,	14,14,14,14,14,},//2
			{14,9 ,0 ,1 ,14,	14,14,14,14,14,	6 ,10,5 ,7 ,14,	14,14,14,14,14,},//3
			
			{14,14,14,14,14,	14,14,14,14,8 ,	11,13,13,10,7 ,	14,14,14,14,14,},//
			{14,14,14,8 ,5 ,	5 ,5 ,5 ,5 ,11,	13,13,13,13,10,	5 ,5 ,7 ,14,14,},
			{14,14,8 ,11,13,	13,13,13, 3,17, 17,16,13,13,13,	13,13,10,7 ,14,},
			{14,8 ,11,13,13,	13,13,13,13,13,	13,13,13,13,18,	13,13,13,10,7 ,},
			{8 ,11,13,2 ,0 ,	0 ,12,13,13,18,	13,21,13,13,19,	13,13,13,2 ,1 ,},
			
			{6 ,13,2 ,1 ,14,	14,9 ,12,13,20,	13,21,13,13,20,	13,13,2 ,1 ,14,},
			{6 ,2 ,1 ,14,14,	14,14,9 ,12,13,	13,21,13,13,13,	13,2 ,1 ,14,14,},
			{9 ,1 ,14,8 ,5 ,	7 ,14,14,6 ,13,	13,21,13,13,13,	2 ,1 ,14,14,14,},
			{14,14,8 ,11,13,	4 ,14,14,6 ,13,	13,13,13,13,2 ,	1 ,14,14,14,14,},
			{14,8 ,11,13,2 ,	1 ,14,8 ,11,13,	13,13,13,2 ,1 ,	14,14,14,14,14,},
			
			{8 ,11,13,13,4 ,	8 ,5 ,11,13,13,	13,13,13,4 ,14,	14,14,14,14,14,},
			{6 ,13,13,2 ,1 ,	9 ,12,13,13,13,	3 ,17,16,4 ,14,	14,14,14,14,14,},
			{6 ,13,2 ,1 ,14,	14,9 ,12,13,13,	13,13,2 ,1 ,14,	14,14,14,14,14,},
			{9 ,0 ,1 ,14,14,	14,14,9 ,12,13,	13,2 ,1 ,14,14,	14,14,14,14,14,},
			{14,14,14,14,14,	14,14,14,9 ,0 ,	0 ,1 ,14,14,14,	14,14,14,14,14,},
			
		},
		//第二关
		{
			{14,14,14,14,14,	14,14,14, 8, 5,	 5, 5, 7,14,14,	14,14,14,14,14},
			{14,14,14,14,14,	14,14, 8,11,13,	13,13,10, 7,14,	14,14,14,14,14},
			{14,14,14,14,14,	14, 8,11,13,13,	13,13,13,10, 5,	 5, 7,14,14,14},
			{14,14,14,14,14,	 8,11, 3,16, 2,	 0, 0,12,13,13,	13,10, 7,14,14},
			{14,14,14,14, 8,	11,13,13,13, 4,	14,14, 9,12,13,	13,18,10, 5, 7},
			
			{14,14,14, 8,11,	18,13,13,13, 4,	14,14,14, 9,12,	13,20,13, 2, 1},
			{14,14, 8,11,13,	19,13,13, 2, 1,	14,14,14,14, 6,	13,13, 2, 1,14},
			{14,14, 9,12,13,	20, 2, 0, 1,14,	14,14,14,14, 6,	13, 2, 1,14,14},
			{14,14,14, 9,12,	 2, 1,14,14,14,	14,14,14,14, 6,	13, 4,14,14,14},
			{14,14,14,14, 9,	 1,14,14, 8, 5,	 7,14,14,14, 6,	13, 4,14,14,14},
			
			{14,14,14,14,14,	14,14, 8,11, 2,	 1,14,14, 8,11,	13,10, 5, 7,14},
			{14,14,14, 8, 7,	14,14, 6,13, 4,	14,14, 8,11,13,	13,13,13,10, 7},
			{14,14, 8,11, 4,	14,14, 9, 0, 1,	14, 8,11,13,13,	13, 2, 0, 0, 1},
			{14, 8,11,18,10,	 7,14,14,14,14,	 8,11,13,13,13,	 2, 1,14,14,14},
			{ 8,11,13,19, 2,	 1,14,14,14, 8,	11,3,17,16, 2,	 1,14,14,14,14},
			
			{ 6,13,13,19, 4,	14,14,14, 8,11,	13,13,13, 2, 1,	14,14,14,14,14},
			{ 9,12,13,20, 4,	14,14, 8,11,13,	13,13, 2, 1,14,	14,14,14,14,14},
			{14, 9,12, 2, 1,	14,14, 9,12,13,	13, 2, 1,14,14,	14,14,14,14,14},
			{14,14, 9, 1,14,	14,14,14, 9,12,	13, 4,14,14,14,	14,14,14,14,14},
			{14,14,14,14,14,	14,14,14,14, 9,	 0, 1,14,14,14,	14,14,14,14,14},
			
		},
		//第三关
		{
			{14,14,14,14,14,	14,14, 8, 5, 5,	 5, 7,14,14,14,	14,14,14,14,14},
			{14,14,14,14, 8,	 5, 5,11, 3,17,	16,10, 5, 5, 7,	14,14,14,14,14},
			{14,14, 8, 5,11,	13,13,13, 2, 0,	 0, 0,12,13,10,	 5, 5, 7,14,14},
			{14,14, 6,13,13,	13, 2, 0, 1,14,	14,14, 9, 0,12,	13,13, 4,14,14},
			{14,14, 6,18, 2,	 0, 1,14,14,14,	14,14,14,14, 9,	12,13,10, 7,14},
			
			{14, 8,11,20, 4,	14,14,14, 8, 5,	 7,14,14,14,14,	 9, 0, 0, 1,14},
			{14, 6,13, 2, 1,	14,14, 8,11, 2,	 1,14,14, 8, 7,	14,14,14,14,14},
			{ 8,11,13, 4,14,	14, 8,11, 2, 1,	14,14, 8,11, 4,	14,14,14,14,14},
			{ 6, 2, 0, 1,14,	 8,11, 2, 1,14,	14,14, 6,13, 4,	14, 8, 5, 7,14},
			{ 9, 1,14,14,14,	 6, 2, 1,14,14,	14,14, 6,13, 4,	14, 9,12,10, 7},
			
			{14,14,14,14, 8,	11, 4,14,14,14,	 8, 5,11,13, 4,	14,14, 6,13, 4},
			{14,14,14,14, 9,	12, 4,14,14,14,	 6,13,13, 2, 1,	14,14, 6, 2, 1},
			{ 8, 5, 7,14,14,	 6,10, 5, 5, 5,	11, 2, 0, 1,14,	14, 8,11, 4,14},
			{ 9,12,10, 5, 7,	 9, 0,12, 3,16,  2, 1,14,14,14,	 8,11, 2, 1,14},
			{14, 6,13,13, 4,	14,14, 9, 0, 0,	 1,14,14,14,14,	 6,13, 4,14,14},
			
			{14, 6,13,13,10,	 5, 7,14,14,14,	14,14,14, 8, 5,	11, 2, 1,14,14},
			{14, 9,12,13,13,	13,10, 5, 7,14,	14, 8, 5,11,13,	 2, 1,14,14,14},
			{14,14, 9,12, 3,	17,17,16, 4,14,	 8,11, 3,16, 2,	 1,14,14,14,14},
			{14,14,14, 9,12,	 2, 0, 0, 1,14,	 9, 0, 0, 0, 1,	14,14,14,14,14},
			{14,14,14,14, 9,	 1,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14},
		},
		{
			{14,14,14,14,14,	14,14,14, 8, 5,	 5, 5, 7,14,14,	14,14,14,14,14},
			{14,14,14,14,14,	14,14, 8,11,13,	13,13,10, 7,14,	14,14,14,14,14},
			{14,14,14,14,14,	14, 8,11,13,13,	13,13,13,10, 5,	 5, 7,14,14,14},
			{14,14,14,14,14,	 8,11, 3,16, 2,	 0, 0,12,13,13,	13,10, 7,14,14},
			{14,14,14,14, 8,	11,13,13,13, 4,	14,14, 9,12,13,	13,18,10, 5, 7},
			
			{14,14,14, 8,11,	18,13,13,13, 4,	14,14,14, 9,12,	13,20,13, 2, 1},
			{14,14, 8,11,13,	19,13,13, 2, 1,	14,14,14,14, 6,	13,13, 2, 1,14},
			{14,14, 9,12,13,	20, 2, 0, 1,14,	14,14,14,14, 6,	13, 2, 1,14,14},
			{14,14,14, 9,12,	 2, 1,14,14,14,	14,14,14,14, 6,	13, 4,14,14,14},
			{14,14,14,14, 9,	 1,14,14, 8, 5,	 7,14,14,14, 6,	13, 4,14,14,14},
			
			{14,14,14,14,14,	14,14, 8,11, 2,	 1,14,14, 8,11,	13,10, 5, 7,14},
			{14,14,14, 8, 7,	14,14, 6,13, 4,	14,14, 8,11,13,	13,13,13,10, 7},
			{14,14, 8,11, 4,	14,14, 9, 0, 1,	14, 8,11,13,13,	13, 2, 0, 0, 1},
			{14, 8,11,18,10,	 7,14,14,14,14,	 8,11,13,13,13,	 2, 1,14,14,14},
			{ 8,11,13,19, 2,	 1,14,14,14, 8,	11,3,17,16, 2,	 1,14,14,14,14},
			
			{ 6,13,13,19, 4,	14,14,14, 8,11,	13,13,13, 2, 1,	14,14,14,14,14},
			{ 9,12,13,20, 4,	14,14, 8,11,13,	13,13, 2, 1,14,	14,14,14,14,14},
			{14, 9,12, 2, 1,	14,14, 9,12,13,	13, 2, 1,14,14,	14,14,14,14,14},
			{14,14, 9, 1,14,	14,14,14, 9,12,	13, 4,14,14,14,	14,14,14,14,14},
			{14,14,14,14,14,	14,14,14,14, 9,	 0, 1,14,14,14,	14,14,14,14,14},
			
		},
		{
			{14,14,14,14,14,	14,14, 8, 5, 5,	 5, 7,14,14,14,	14,14,14,14,14},
			{14,14,14,14, 8,	 5, 5,11, 3,17,	16,10, 5, 5, 7,	14,14,14,14,14},
			{14,14, 8, 5,11,	13,13,13, 2, 0,	 0, 0,12,13,10,	 5, 5, 7,14,14},
			{14,14, 6,13,13,	13, 2, 0, 1,14,	14,14, 9, 0,12,	13,13, 4,14,14},
			{14,14, 6,18, 2,	 0, 1,14,14,14,	14,14,14,14, 9,	12,13,10, 7,14},
			
			{14, 8,11,20, 4,	14,14,14, 8, 5,	 7,14,14,14,14,	 9, 0, 0, 1,14},
			{14, 6,13, 2, 1,	14,14, 8,11, 2,	 1,14,14, 8, 7,	14,14,14,14,14},
			{ 8,11,13, 4,14,	14, 8,11, 2, 1,	14,14, 8,11, 4,	14,14,14,14,14},
			{ 6, 2, 0, 1,14,	 8,11, 2, 1,14,	14,14, 6,13, 4,	14, 8, 5, 7,14},
			{ 9, 1,14,14,14,	 6, 2, 1,14,14,	14,14, 6,13, 4,	14, 9,12,10, 7},
			
			{14,14,14,14, 8,	11, 4,14,14,14,	 8, 5,11,13, 4,	14,14, 6,13, 4},
			{14,14,14,14, 9,	12, 4,14,14,14,	 6,13,13, 2, 1,	14,14, 6, 2, 1},
			{ 8, 5, 7,14,14,	 6,10, 5, 5, 5,	11, 2, 0, 1,14,	14, 8,11, 4,14},
			{ 9,12,10, 5, 7,	 9, 0,12, 3,16,  2, 1,14,14,14,	 8,11, 2, 1,14},
			{14, 6,13,13, 4,	14,14, 9, 0, 0,	 1,14,14,14,14,	 6,13, 4,14,14},
			
			{14, 6,13,13,10,	 5, 7,14,14,14,	14,14,14, 8, 5,	11, 2, 1,14,14},
			{14, 9,12,13,13,	13,10, 5, 7,14,	14, 8, 5,11,13,	 2, 1,14,14,14},
			{14,14, 9,12, 3,	17,17,16, 4,14,	 8,11, 3,16, 2,	 1,14,14,14,14},
			{14,14,14, 9,12,	 2, 0, 0, 1,14,	 9, 0, 0, 0, 1,	14,14,14,14,14},
			{14,14,14,14, 9,	 1,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14},
		},
		{
			{14,8 ,5 ,7 ,14,	14,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14,},
			{14,6 ,13,10,7 ,	14,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14,},//0
			{8 ,11,13,2 ,1 ,	14,14,14,14,14,	14,14,14,14,14,	14,14,14,14,14,},//1
			{9 ,12,13,4 ,14,	14,14,14,14,14,	8 ,7 ,14,14,14,	14,14,14,14,14,},//2
			{14,9 ,0 ,1 ,14,	14,14,14,14,14,	6 ,10,5 ,7 ,14,	14,14,14,14,14,},//3
			
			{14,14,14,14,14,	14,14,14,14,8 ,	11,13,13,10,7 ,	14,14,14,14,14,},//
			{14,14,14,8 ,5 ,	5 ,5 ,5 ,5 ,11,	13,13,13,13,10,	5 ,5 ,7 ,14,14,},
			{14,14,8 ,11,13,	13,13,13, 3,17, 17,16,13,13,13,	13,13,10,7 ,14,},
			{14,8 ,11,13,13,	13,13,13,13,13,	13,13,13,13,18,	13,13,13,10,7 ,},
			{8 ,11,13,2 ,0 ,	0 ,12,13,13,18,	13,21,13,13,19,	13,13,13,2 ,1 ,},
			
			{6 ,13,2 ,1 ,14,	14,9 ,12,13,20,	13,21,13,13,20,	13,13,2 ,1 ,14,},
			{6 ,2 ,1 ,14,14,	14,14,9 ,12,13,	13,21,13,13,13,	13,2 ,1 ,14,14,},
			{9 ,1 ,14,8 ,5 ,	7 ,14,14,6 ,13,	13,21,13,13,13,	2 ,1 ,14,14,14,},
			{14,14,8 ,11,13,	4 ,14,14,6 ,13,	13,13,13,13,2 ,	1 ,14,14,14,14,},
			{14,8 ,11,13,2 ,	1 ,14,8 ,11,13,	13,13,13,2 ,1 ,	14,14,14,14,14,},
			
			{8 ,11,13,13,4 ,	8 ,5 ,11,13,13,	13,13,13,4 ,14,	14,14,14,14,14,},
			{6 ,13,13,2 ,1 ,	9 ,12,13,13,13,	3 ,17,16,4 ,14,	14,14,14,14,14,},
			{6 ,13,2 ,1 ,14,	14,9 ,12,13,13,	13,13,2 ,1 ,14,	14,14,14,14,14,},
			{9 ,0 ,1 ,14,14,	14,14,9 ,12,13,	13,2 ,1 ,14,14,	14,14,14,14,14,},
			{14,14,14,14,14,	14,14,14,9 ,0 ,	0 ,1 ,14,14,14,	14,14,14,14,14,},
			
		},
	};
	//每所有物体的信息数组
	public static final float ArchieArray[][][]=
	{
		//---------------战役模式--------------------------------------------------
		//第一关
		{
			//高射炮的位置0
			{
				3.5f,2.5f, 
				1.5f,17.5f, 1.5f,16.5f, 2.5f,15.5f,	
				10.5f,17.5f, 10.5f,18, 9.5f,18.5f, 8.5f,17.5f, 8.5f,16.5f, 9.5f,16.5f, 13.5f,6.5f, 14.5f,7.5f, 15,7.5f, 12.5f,7.75f,
				13.5f,8.5f,  3.5f,8.5f, 5.5f,8.5f,
			},   
			//坦克位置数组1
			{//3.5f,14.5f, 3.75f,14.5f, 9.5f,14.5f, 12.5f,14.5f, 14.5f,12.5f, 13.5f,13.5f, 6.5f,8.5f, 4.5f,7.5f,
				},
			//军火库所在位置2
			{ 2.5f,16.5f,// 9.5f,17.5f, 13.5f,7.5f
					},
			//灯塔位置数组3
			{2.5f,1.5f},
			//平民房屋所在位置数组4
			{2.5f,2.5f, 
				12.25f,9.5f, 10.5f,12.5f, 15.15f,9.5f, 11.5f,6.5f, 
			},
			//大坝的起始位置5
			{
				2,11,  2.05f,11.25f,  2.125f,11.55f,  2.20f,11.85f,  2.45f,12.25f,  2.85f,12.725f,  3.25f,12.85f,  3.45f,12.9f, 3.75f,12.95f,
				4,13, 
				
				4.25f,15.75f,  4.625f,16.125f,  4.75f,16.225f, 5,16.25f,  5.25f,16.225f,  5.375f,16.125f,  5.75f,15.75f
				
			},
			//飞机视频播放时,经过的点.6
			{6,4, 13,4,  15,6, 15,12, 13,15, 6,15, 3,13, 3,6,},
			//飞机子弹击中对方目标减少的生命值7
			{5,5,8,2},//军火库,坦克,高射炮,敌机
			//飞机炮弹击中对方目标减少的生命值8
			{50,100,70,100},//军火库,坦克,高射炮,敌机
			//敌方击中飞机减少的生命值9
			{5,15,1,30,5},//坦克,高射炮,敌机,碰到军火库,碰到其他的东西
			//飞机场办公大楼
			{9f,9.5f},//10
			//树所在的数组11
			{2.5f,2.25f, 2.25f,2,  2.25f,3,  1,2.75f, 
				3.5f,16.25f, 4.5f,13.5f, 4.25f,13.5f, 
				13f,9.5f,  11,6.25f, 11.5f,6.25f, 12,6.5f, 12.5f,7, 13,9.5f, 13.5f, 9.75f, 12.25f,11, 12.25f,11.75f,
				10.75f,11.25f, 10.75f,11.75f, 15.25f,9, 15.75f,9, 15.25f,11, 
				10.5f,14.5f, 12.5f,14.5f, 10.5f,15.5f, 10.5f,18.5f, 9.5f,8.5f, 17.5f,8.5f, 1.5f,9.5f, 1.5f,10.5f,
				2.5f,9.5f, 3,9.5f, 4.5f,8.5f, 5,8, 5.5f,7.5f, 
			},
			//击落敌方物品增加的分数12
			{10,15,20,50},//0坦克1高射炮,2敌机,3军火库
			//炮弹和子弹的数目13
			{0,1000},
		},
		//第二关
		{
			//高射炮的位置0
			{
				1.5f,15.25f, // 1.5f,16f,  2.5f,14.5f,  4,12,  8,7,  8,6,  7.5f,5.75f,  6.5f,5.25f,  8,5,  9.5f,2.5f,  
				11.5f,1.5f,  14.5f,3.5f,  15.5f,5.5f,  16,10,  15.5f,12.5f,  10.5f,15.75f,  11.25f,15.75f,  11.5f,16.5f,
				8.5f,11.5f,  5,14
			},   
			//坦克位置数组1
			{
				2.25f,14.25f,  2.75f,14.25f,  2.25f,14.75f,  2.75f,14.75f,  
				2.4f,16.5f,  2.8f,16.75f,  4.25f,7.5f, 
				4.75f,7.75f,  8.5f,5.5f,  8.5f,4.5f,  12.5f,2.5f, 15.25f,3.5f,  15.75f,3.5f, 14.5f,4.25f,  14.5f,4.75f,
				9.5f,17.5f,  10.5f, 17.5f 
			},
			//军火库所在位置2
			{2.5f,15.5f, 15.5f,4.5f, //7.5f,6.5f, 10.5f,16.5f
				},
			//灯塔位置数组3
			{9f,11f},
			//平民房屋所在位置数组4
			{
				6.125f,6.5f,  4.75f,7,  10.5f,1.5f,  18,5,  15.5f,8.5f,  19,12,
				12.5f,15,  14.5f,12.5f, 
			},
			//大坝的起始位置5
			{},
			//飞机视频播放时,经过的点.6
			{6,4, 13,4,  15,6, 15,12, 13,15, 6,15, 3,13, 3,6,},
			//飞机子弹击中对方目标减少的生命值7
			{3,5,5,4},//军火库,坦克,高射炮,敌机
			//飞机炮弹击中对方目标减少的生命值8
			{50,80,70,100},//军火库,坦克,高射炮,敌机
			//敌方击中飞机减少的生命值9
			{8,10,4,10,4},//坦克,高射炮,敌机,碰到军火库,碰到其他的东西
			//飞机场办公大楼
			{9f,9.5f},//10
			//树所在的数组11
			{
				2,16,  2.25f,16,  2.5f,16,  2.75f,16,  3,16,  4.125f,15,    4.125f,15.5f,     4.125f,16.5f,  2,17,
				4,13,  4.25f,13,  5,9,      5.25f,9,   3,7,   3.5f,7,  4.75f,6.5f,  7,7,      7,6,
				8.25f,2.25f,   8.75f,2.75f,  9.5f,1.5f,  10,2,  10.5f,2,  13.5f,3.5f,  15,4,  16,5,
				15.5f,5,  15.5f,6.5f,  16.5f,6.5f,  19,5,  15.5f,7.5f,  16,9,  18,12,  17.5f,11.5f, 
				13.5f,12.5f,  12.5f,15.5f,  10.5f,18.5f,  9,10,  10,10,  9,12
			},
			//击落敌方物品增加的分数12
			{12,17,22,55},//0坦克1高射炮,2敌机,3军火库
			//炮弹和子弹的数目13
			{50,1500},
		},
		//第三关
		{
			//高射炮的位置0
			{
				10,6,8,8,  8,7,  7,9,  /*6.5f,9.5f,*/  7,13,  13.5f,8.5f,  5.5f,2.5f,  6.5f,2.5f, //2.5f,6.5f,  
				3.5f,15.5f,  3.5f,16.5f, /* 5.5f,16.5f,*/  18,10,  18,11,  19,10,  19,11,  17,13,  16,16
			},   
			//坦克位置数组1
			{
				4,16.5f, /* 5,17, */ 6,16,  5,16,  3.5f,14.5f,  3,7,  3,6,/*  6,3,*/  7,3,  12,2,  13.5f,2.5f,  16,3.25f,  /*16.5f, 3.25f, */ 17f,3.25f,
				17,14,  15f,17f, /* 13.25f,9.5f, */ 13.75f,9.5f,  13.5f,10.5f,  12,12,  6,10,  
			},
			//军火库所在位置2
			{16.5f,4.5f,  4.5f,3.5f,  4.5f,16.5f, 18.5f,10.5f
			},
			//灯塔位置数组3
			{6f,11f},
			//平民房屋所在位置数组4
			{
				2.725f,6,  14,2,  7.5f,2.5f,  15,3,  12,18,  14.5f,16.5f,  
			},
			//大坝的起始位置5
			{},
			//飞机视频播放时,经过的点.6
			{6,4, 13,4,  15,6, 15,12, 13,15, 6,15, 3,13, 3,6,},
			//飞机子弹击中对方目标减少的生命值7
			{5,10,10,5},//军火库,坦克,高射炮,敌机
			//飞机炮弹击中对方目标减少的生命值8
			{30,70,70,50},//军火库,坦克,高射炮,敌机
			//敌方击中飞机减少的生命值
			{17,20,2,15,4},//坦克,高射炮,敌机,碰到军火库,碰到其他的东西
			//飞机场办公大楼
			{9f,9.5f},//10
			//树所在的数组11
			{
				5.5f,18.125f,  6.5f,18.125f,  8,18,  3,16,  4,16,  6,17,  2,13,  3,14,  4,14,  3,15,  2,8,  2,7,  4,6,  
				4,4,  5,4,  6,2,  10,2.125f,  13,2.5f,  14,3,  16,4,  17,5, 18,9,  18,12,  18,12.5f, 18,13,
				17.5f,13.5f,   12,17,  14,17,
				14,7,  13,11,  11,13,  5,11,  6,9,  8.5f,7.5f
			},
			//击落敌方物品增加的分数12
			{15,20,25,70},//0坦克1高射炮,2敌机,3军火库
			//炮弹和子弹的数目13
			{80,2000},
		},
		//--------------------------------------特别行动---------------------------------------------------
		//第一关----------消灭飞机即可
		{
			//高射炮的位置0
			{
				1.5f,15.25f,  1.5f,16f,  2.5f,14.5f, // 4,12,  8,7,  8,6,  7.5f,5.75f,  6.5f,5.25f,  8,5,  9.5f,2.5f,  
				11.5f,1.5f,  /*14.5f,3.5f, */ 15.5f,5.5f,  16,10,  15.5f,12.5f,/*  10.5f,15.75f,  11.25f,15.75f, */ 11.5f,16.5f,
				8.5f,11.5f,  5,14
			},   
			//坦克位置数组1
			{
				2.25f,14.25f,  2.75f,14.25f,//  2.25f,14.75f,  2.75f,14.75f,  
				2.4f,16.5f,  2.8f,16.75f,  //4.25f,7.5f, 
				4.75f,7.75f,  8.5f,5.5f,/*  8.5f,4.5f, */ 12.5f,2.5f, 15.25f,3.5f,  15.75f,3.5f,// 14.5f,4.25f,  14.5f,4.75f,
				9.5f,17.5f,  10.5f, 17.5f 
			},
			//军火库所在位置2
			{2.5f,15.5f, 15.5f,4.5f,/* 7.5f,6.5f, 10.5f,16.5f*/},
			//灯塔位置数组3
			{9f,11f},
			//平民房屋所在位置数组4
			{
				6.125f,6.5f,  4.75f,7,  10.5f,1.5f,  18,5,  15.5f,8.5f,  19,12,
				12.5f,15,  14.5f,12.5f, 
			},
			//大坝的起始位置5
			{},
			//飞机视频播放时,经过的点.6
			{6,4, 13,4,  15,6, 15,12, 13,15, 6,15, 3,13, 3,6,},
			//飞机子弹击中对方目标减少的生命值7
			{3,7,7,4},//军火库,坦克,高射炮,敌机
			//飞机炮弹击中对方目标减少的生命值8
			{50,100,70,100},//军火库,坦克,高射炮,敌机
			//敌方击中飞机减少的生命值9
			{8,11,4,6,4},//坦克,高射炮,敌机,碰到军火库,碰到其他的东西
			//飞机场办公大楼
			{9f,9.5f},//10
			//树所在的数组11
			{
				2,16,  2.25f,16,  2.5f,16,  2.75f,16,  3,16,  4.125f,15,    4.125f,15.5f,     4.125f,16.5f,  2,17,
				4,13,  4.25f,13,  5,9,      5.25f,9,   3,7,   3.5f,7,  4.75f,6.5f,  7,7,      7,6,
				8.25f,2.25f,   8.75f,2.75f,  9.5f,1.5f,  10,2,  10.5f,2,  13.5f,3.5f,  15,4,  16,5,
				15.5f,5,  15.5f,6.5f,  16.5f,6.5f,  19,5,  15.5f,7.5f,  16,9,  18,12,  17.5f,11.5f, 
				13.5f,12.5f,  12.5f,15.5f,  10.5f,18.5f,  9,10,  10,10,  9,12
			},
			//击落敌方物品增加的分数12
			{12,17,22,55},//0坦克1高射炮,2敌机,3军火库
			//炮弹和子弹的数目13
			{20,800},
		},
		//第二关----------------消灭高射炮和坦克
		{
			//高射炮的位置0
			{
				10,6,  8,8,  8,7,  7,9,  6.5f,9.5f,  7,13, /* 13.5f,8.5f, */ 5.5f,2.5f,  6.5f,2.5f,// 2.5f,6.5f,  
				3.5f,15.5f,  3.5f,16.5f,  5.5f,16.5f,  18,10, /* 18,11,  19,10,*/  19,11,  17,13,  16,16
			},   
			//坦克位置数组1
			{
				4,16.5f, 5,17,  6,16, /* 5,16,  3.5f,14.5f, */ 3,7,  3,6,  6,3,  7,3,  12,2,/*  13.5f,2.5f,*/  16,3.25f,  16.5f, 3.25f,  17f,3.25f,
				17,14,  /*15f,17f, */ 13.25f,9.5f,  13.75f,9.5f,  13.5f,10.5f,  12,12,//  6,10,  
			},
			//军火库所在位置2
			{16.5f,4.5f, /* 4.5f,3.5f,  4.5f,16.5f, 18.5f,10.5f*/},
			//灯塔位置数组3
			{6f,11f},
			//平民房屋所在位置数组4
			{
				2.725f,6,  14,2,  7.5f,2.5f,  15,3,  12,18,  14.5f,16.5f,  
			},
			//大坝的起始位置5
			{},
			//飞机视频播放时,经过的点.6
			{6,4, 13,4,  15,6, 15,12, 13,15, 6,15, 3,13, 3,6,},
			//飞机子弹击中对方目标减少的生命值7
			{5,10,10,7},//军火库,坦克,高射炮,敌机
			//飞机炮弹击中对方目标减少的生命值8
			{50,100,70,100},//军火库,坦克,高射炮,敌机
			//敌方击中飞机减少的生命值9
			{17,20,13,15,4},//坦克,高射炮,敌机,碰到军火库,碰到其他的东西
			//飞机场办公大楼
			{9f,9.5f},//10
			//树所在的数组11
			{
				5.5f,18.125f,  6.5f,18.125f,  8,18,  3,16,  4,16,  6,17,  2,13,  3,14,  4,14,  3,15,  2,8,  2,7,  4,6,  
				4,4,  5,4,  6,2,  10,2.125f,  13,2.5f,  14,3,  16,4,  17,5, 18,9,  18,12,  18,12.5f, 18,13,
				17.5f,13.5f,   12,17,  14,17,
				14,7,  13,11,  11,13,  5,11,  6,9,  8.5f,7.5f
			},
			//击落敌方物品增加的分数12
			{15,20,25,70},//0坦克1高射炮,2敌机,3军火库
			//炮弹和子弹的数目13
			{50,1500},
		},
		//第三关--------------------消灭军火库
		{
			//高射炮的位置0
			{3.5f,2.5f, 
			1.5f,17.5f, 1.5f,16.5f, 2.5f,15.5f,	
			10.5f,17.5f, 10.5f,18, 9.5f,18.5f, 8.5f,17.5f, 8.5f,16.5f, 9.5f,16.5f, 13.5f,6.5f, 14.5f,7.5f, 15,7.5f, 12.5f,7.75f,
			13.5f,8.5f,  3.5f,8.5f, 5.5f,8.5f,
			},   
			//坦克位置数组1
			{3.5f,14.5f, 3.75f,14.5f, 9.5f,14.5f, 12.5f,14.5f, 14.5f,12.5f, 13.5f,13.5f, 6.5f,8.5f, 4.5f,7.5f,},
			//军火库所在位置2
			{ 2.5f,16.5f, 9.5f,17.5f, 13.5f,7.5f
				},
			//灯塔位置数组3
			{2.5f,1.5f},
			//平民房屋所在位置数组4
			{2.5f,2.5f, 
				12.25f,9.5f, 10.5f,12.5f, 15.15f,9.5f, 11.5f,6.5f, 
			},
			//大坝的起始位置5
			{
				2,11,  2.05f,11.25f,  2.125f,11.55f,  2.20f,11.85f,  2.45f,12.25f,  2.85f,12.725f,  3.25f,12.85f,  3.45f,12.9f, 3.75f,12.95f,
				4,13, 
				
				4.25f,15.75f,  4.625f,16.125f,  4.75f,16.225f, 5,16.25f,  5.25f,16.225f,  5.375f,16.125f,  5.75f,15.75f
				
			},
			//飞机视频播放时,经过的点.6
			{6,4, 13,4,  15,6, 15,12, 13,15, 6,15, 3,13, 3,6,},
			//飞机子弹击中对方目标减少的生命值7
			{1,5,5,2},//军火库,坦克,高射炮,敌机
			//飞机炮弹击中对方目标减少的生命值8
			{50,100,70,100},//军火库,坦克,高射炮,敌机
			//敌方击中飞机减少的生命值9
			{10,20,20,100,10},//坦克,高射炮,敌机,碰到军火库,碰到其他的东西
			//飞机场办公大楼
			{9f,9.5f},//10
			//树所在的数组11
			{2.5f,2.25f, 2.25f,2,  2.25f,3,  1,2.75f, 
				3.5f,16.25f, 4.5f,13.5f, 4.25f,13.5f, 
				13f,9.5f,  11,6.25f, 11.5f,6.25f, 12,6.5f, 12.5f,7, 13,9.5f, 13.5f, 9.75f, 12.25f,11, 12.25f,11.75f,
				10.75f,11.25f, 10.75f,11.75f, 15.25f,9, 15.75f,9, 15.25f,11, 
				10.5f,14.5f, 12.5f,14.5f, 10.5f,15.5f, 10.5f,18.5f, 9.5f,8.5f, 17.5f,8.5f, 1.5f,9.5f, 1.5f,10.5f,
				2.5f,9.5f, 3,9.5f, 4.5f,8.5f, 5,8, 5.5f,7.5f, 
			},
			//击落敌方物品增加的分数12
			{10,15,20,50},//0坦克1高射炮,2敌机,3军火库
			//炮弹和子弹的数目13
			{10,200},
		}
	};
	//------------特别行动的时间域
	public static int [] actionTimeSpan={70,160,60};
	
	//用于记录的相关信息
	public static int[] gradeArray=new int[3];//0关卡,1总的分,2耗时.
	//树的高度和宽度
	public static final float treeWhidth=130;
	public static final float treeHeight=150;
	//平民房的长宽高
	public static final float house_length=200;
	public static final float house_width=90;
	public static final float house_height=100;
	//关于英雄当前状态
	public static boolean isCrash=false;//飞机是否坠毁
	public static boolean isOvercome=false;//是否战胜，即炸掉军火库
	public static boolean isCrashCartoonOver=false;//飞机坠毁动画播放完毕
	public static float Crash_DISTANCE_start=15;//坠毁时碰撞点与摄像机的长度
	public static float Crash_DISTANCE_stop=60;//坠毁时摄像机与目标点的拉伸距离
	public static float BaoZha_scal=0f;//爆炸效果的缩放比
	public static boolean isno_draw_plane=false;//是否绘制飞机
	public static boolean isno_draw_arsenal=true;//是否绘制军火库
	//刚刚进入场景，播放视频等标志
	public static boolean isVideo=true;//是否播放视频中标志
	public static int plane_blood=999;//999;
	public static int arsenal_blood=100;
	//当前所在的关数
	public static int mapId=0;
	
	//是否在游戏界面按下了返回按钮
	public static boolean is_button_return=false;
	
	
	//是否被坦克或者高射炮击中
	public static boolean isno_Hit;//碰到障碍物抖动
	public static boolean isno_Vibrate;//发射炮弹 抖动
	
	//是否有某一个东西被锁定
	public static final float Lock_Distance=2000;
	public static boolean isno_Lock=false;//标记是否被锁定
	public static float Lock_angle=(float) Math.toRadians(8);//被锁定的角度范围
	public static TanKe Lock_tanke;//被锁定的坦克
	public static Arsenal_House Lock_arsenal;//被锁定的军火库
	public static ArchieForControl Lock_Arch;//被锁定的高射炮
	public static Arsenal_House Lock_Arsenal;//被锁定的军火库
	
	public static float minimumdistance=Lock_Distance;//当前被锁定的最小距离
	//锁定时的方向向量
	public static float nx,ny,nz; 
	public static float directionX,directionY,directionZ;//飞机飞行的当前方向，用来锁定目标

	public static float planezAngle;//飞机螺旋浆转动角度

	{
		 //设定摄像机仰角和方位角
		ELEVATION_CAMERA_ORI=8F;//摄像机初始仰角
	    ELEVATION_CAMERA=30;//ELEVATION_CAMERA_ORI;//摄像机观察点距目标点的仰角
	    DIRECTION_CAMERA=225;//摄像机观察点距目标点的方向角
	    ELEVATION_CAMERA_UP=30;//摄像机向上仰的最大值
	    ELEVATION_CAMERA_DOWN=-5;//摄像机向下俯的最大值
	    ELEVATION_CAMERA_SPAN=0.4F;//摄像机上下俯仰的步值
	    DIRECTION_CAMERA_SPAN=2F;//摄像机左右旋转地步值
	    rotationAngle_Plane_Y=DIRECTION_CAMERA;
	    PLANE_X=1675;//飞机的X位置
		PLANE_Y=500;//飞机的Y位置
		PLANE_Z=2060;//飞机的Z位置
		PLANE_MOVE_SPAN=15;//飞机的速度
		isCrash=false;//飞机是否坠毁
		isOvercome=false;//是否战胜，即炸掉军火库
		isCrashCartoonOver=false;//飞机坠毁动画是否播放完毕
		Crash_DISTANCE_start=15;//坠毁时碰撞点与摄像机的长度
		Crash_DISTANCE_stop=60;//坠毁时摄像机与目标点的拉伸距离
		BaoZha_scal=0f;//爆炸效果的缩放比
		isno_draw_plane=true;//是否绘制飞机
		isno_draw_arsenal=true;//是否绘制军火库
		isVideo=false;//是否播放视频中标志
		bullet_number=(int) ArchieArray[mapId][13][1];
		bomb_number=(int) ArchieArray[mapId][13][0];
		WEAPON_INDEX=0;
		
	}
	public static void initMap_Value()//初始化一些值
	{
		 //设定摄像机仰角和方位角
		ELEVATION_CAMERA_ORI=8F;//摄像机初始仰角
	    ELEVATION_CAMERA=30;//ELEVATION_CAMERA_ORI;//摄像机观察点距目标点的仰角
	    DIRECTION_CAMERA=225;//摄像机观察点距目标点的方向角
	    ELEVATION_CAMERA_UP=30;//摄像机向上仰的最大值
	    ELEVATION_CAMERA_DOWN=-5;//摄像机向下俯的最大值
	    ELEVATION_CAMERA_SPAN=0.4F;//摄像机上下俯仰的步值
	    DIRECTION_CAMERA_SPAN=2F;//摄像机左右旋转地步值
	    rotationAngle_Plane_Y=DIRECTION_CAMERA;
	    PLANE_X=1675;//飞机的X位置
		PLANE_Y=500;//飞机的Y位置
		PLANE_Z=2060;//飞机的Z位置
		PLANE_MOVE_SPAN=15;//飞机的速度
		isCrash=false;//飞机是否坠毁
		isOvercome=false;//是否战胜，即炸掉军火库
		isCrashCartoonOver=false;//飞机坠毁动画是否播放完毕
		Crash_DISTANCE_start=15;//坠毁时碰撞点与摄像机的长度
		Crash_DISTANCE_stop=60;//坠毁时摄像机与目标点的拉伸距离
		BaoZha_scal=0f;//爆炸效果的缩放比
		isno_draw_plane=true;//是否绘制飞机
		isno_draw_arsenal=true;//是否绘制军火库
		isVideo=false;//是否播放视频中标志
		bullet_number=(int) ArchieArray[mapId][13][1];
		bomb_number=(int) ArchieArray[mapId][13][0];
		WEAPON_INDEX=0;
		
	}
	public static final float PLANE_X_R=15f;//飞机包围盒半径
	public static final float PLANE_Y_R=15; 
	public static final float PLANE_Z_R=20;
	public static final float ANGLE_X_Z=35;//飞机包围盒顶点与飞机中心连线与中心线的夹角
	public static final float TRANSFER_Y=0;//Y轴上摄像机和飞机的下调值
	//军火库的包围盒
	public static final float ARSENAL_X=100;
	public static final float ARSENAL_Y=50;
	public static final float ARSENAL_Z=90;
	//坦克包围盒
	public static final float ARCHIBALD_X=40;
	public static final float ARCHIBALD_Y=40;
	public static final float ARCHIBALD_Z=40;
	
	//数字的高度，显示血的百分比
	public static final float NUMBER_WIDTH=20;
	public static final float NUMBER_HEIGHT=20;//数字的宽度和高度，显示血
	public static Object lock=new Object();//创建对象锁
	public static float sXtart=0;//2D界面的起始坐标
	public static float sYtart=0;
	
	//手机屏幕的宽度和高度
	public static  float SCREEN_WIDTH=480;
	public static  float SCREEN_HEIGHT=800;
	//屏幕的缩放比例
	public static float ratio_width;
	public static float ratio_height;
	//键盘状态
	public static int keyState=0;//键盘状态  1-up 2-down 4-left 8-right
    //设定摄像机仰角和方位角
	public static float ELEVATION_CAMERA_ORI=8F;//摄像机初始仰角
    public static float ELEVATION_CAMERA=45;//ELEVATION_CAMERA_ORI;//摄像机观察点距目标点的仰角
    public static float  DIRECTION_CAMERA=180;//摄像机观察点距目标点的方向角
    public static float ELEVATION_CAMERA_UP=30;//摄像机向上仰的最大值
    public static float ELEVATION_CAMERA_DOWN=-5;//摄像机向下俯的最大值
    public static float ELEVATION_CAMERA_SPAN=0.8F;//摄像机上下俯仰的步值
    public static float DIRECTION_CAMERA_SPAN=2F;//摄像机左右旋转地步值
    
    //摄像机观察点和目标点的距离
    public static final float DISTANCE=180;
	
	//天空穹的旋转
	public static  float rotationAngle_SkyBall;
	//水面的宽度
	public static float WATER_WIDTH;
	//水面的高度
	public static float WATER_HEIGHT; 
	//------------------------------------山----------------------------------------------
	public static final float LAND_HIGHEST=150f;   
	public static final float LAND_HIGHT=600;//加载山的高度调整
	
	public static final float waterHillHight=20;//水面上的山渐变高度
	public static final float LOTHight=10;//地块渐变高度 
	public static final float HillHight=100;//地面山的渐变高度
	
	public static final float height_span_LOT=130;//拐弯上山渐变高度 
	public static final float height_span_Water=180;//水上山渐变高度
	public static final float height_span_Hill=200;//山上山渐变高度
	
	//该方程是用于产生山地的过度效果
	//相对于灰度图是每一行一行的产生坐标        产生的灰度图为8*8
	static int span=7;// 过度区域所占的行数
	static int rows=7;//总行数                           
	public static float zdYRowFunction(float rowIndex)//参数为行的索引                                           
	{		                                                                                 
		if(rowIndex>=0&&rowIndex<4)
		{
			return LAND_HIGHEST;
		}
		else
		{
			return 0;
		}
	}                                                                                        
	//产生直道Y坐标数组的方法                                              
	public static float[][] generateZDY()                            
	{                                                           
		int colsPlusOne=rows+1;                                 
		int rowsPlusOne=rows+1;		                            
		float[][]temp=new float[rowsPlusOne][colsPlusOne];          
		                                                        
		for(int i=0;i<rowsPlusOne;i++)                          
		{                                                       
			float h=zdYRowFunction(i);                          
			for(int j=0;j<colsPlusOne;j++)                      
			{                                                   
				temp[i][j]=h;                              
			}                                                   
		} 
		return temp;
	}
	//产生上弯道Y坐标数组的方法                                               
	public static float[][] generateUpWDY()                              
	{                                                            
		int colsPlusOne=rows+1;                                  
		int rowsPlusOne=rows+1;		                             
		float [][]temp=new float[rowsPlusOne][colsPlusOne];		     
		for(int i=0;i<rowsPlusOne;i++)                           
		{                                                        
			for(int j=0;j<colsPlusOne;j++)                       
			{                                                    
				float p=(float) Math.sqrt(i*i+j*j);				 
				float h=zdYRowFunction(p);                       
				if(h>LAND_HIGHEST)                           
				{                                                
					h=LAND_HIGHEST;                          
				}  
				if(h<0)
				{
					h=0;
				}
				if(j>7-i)
				{
					h=0;
				}
				temp[i][j]=h;                               
			}                                                    
		} 
		return temp;
	} 
	//产生下弯道的Y数组的方法
	public static float[][] generateDownWDY()                             
	{                                                            
		int colsPlusOne=rows+1;                                  
		int rowsPlusOne=rows+1;		                             
		float [][] temp=new float[rowsPlusOne][colsPlusOne];		     
		for(int i=0;i<rowsPlusOne;i++)                           
		{                                                        
			for(int j=0;j<colsPlusOne;j++)                       
			{                                                         
				float p=(float) Math.sqrt(i*i+j*j);				 
				float h=zdYRowFunction(p);                       
				if(h>LAND_HIGHEST)                           
				{                                                
					h=LAND_HIGHEST;                          
				} 
				if(h<0)
				{
					h=0;
				}
				temp[7-i][7-j]=LAND_HIGHEST-h;                               
			}                                                    
		}
		return temp;
	} 
	//------设定山的调整值
	public static final float LAND_HIGH_ADJUST=0;
	//------陆地的块数
	public static final int LANDS_SIZE=7;
	//-----陆地的高度数组
	public static float[][][] LANDS_HEIGHT_ARRAY=new float[LANDS_SIZE][][];
	//-----陆地的每格单位宽度
	public static final float LAND_UNIT_SIZE=60;
	//陆地的宽度和高度
	public static float WIDTH_LALNDFORM;
	public static float HEIGHT_LANDFORM;
	//设定天空的半径
	public static final float SKY_BALL_RADIUS=40*LAND_UNIT_SIZE*rows;
	public static final float SKY_BALL_SMALL=LAND_UNIT_SIZE*rows*(CELL_SIZE/2-0.3f);
	//------初始化陆地顶点的信息
	public static void initLandsHeightInfo(Resources r)
	{
		LANDS_HEIGHT_ARRAY[0]= generateZDY();
		LANDS_HEIGHT_ARRAY[1]=generateUpWDY();
		LANDS_HEIGHT_ARRAY[2]= generateDownWDY();
		LANDS_HEIGHT_ARRAY[3]=loadLandforms(r,R.drawable.landform,LAND_HIGHEST);//陆地上的山
		LANDS_HEIGHT_ARRAY[4]=loadLandforms(r,R.drawable.landform1,0);//水里面的山
		LANDS_HEIGHT_ARRAY[5]=loadLandforms(r,R.drawable.landform3,LAND_HIGHEST);//陆地上的右边
		LANDS_HEIGHT_ARRAY[6]=loadLandforms(r,R.drawable.landform2,LAND_HIGHEST);//两边接
		//这里初始化陆地的宽度和高度
		WIDTH_LALNDFORM=LAND_UNIT_SIZE*(LANDS_HEIGHT_ARRAY[0].length-1);
		HEIGHT_LANDFORM=LAND_UNIT_SIZE*(LANDS_HEIGHT_ARRAY[0][0].length-1);
		WATER_WIDTH=WIDTH_LALNDFORM;
		WATER_HEIGHT=HEIGHT_LANDFORM;
	}
	//------从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resource,int landformDrawable,float height)
	{
		//加载地形灰度图
		Bitmap bt=BitmapFactory.decodeResource(resource, landformDrawable);
		//获取灰度图的高度与宽度    像素数总比列数和行数大一
		int colsPlusOne=bt.getWidth();
		int rowsPlusOne=bt.getHeight(); 
		//将灰度图中的每个像素的灰度值换算成陆地此点的高度值
		float[][] result=new float[rowsPlusOne][colsPlusOne];
		for(int i=0;i<rowsPlusOne;i++)
		{
			for(int j=0;j<colsPlusOne;j++)
			{
				int color=bt.getPixel(j,i);//获取指定位置颜色值
				int r=Color.red(color);//获取红色分量值
				int g=Color.green(color); //获取绿色分量值
				int b=Color.blue(color);//获取蓝色分量值
				int h=(r+g+b)/3;//颜色均值  为60
				result[i][j]=h*LAND_HIGHT/255.0f+height;  
			}
		}		
		return result;
	}
	//开火按钮的宽度和高度
	public static float BUTTON_FIRE_WIDTH=0.6f;
	public static float BUTTON_FIRE_HEIGHT=0.6f;
	//开火按钮的平移
	public static float BUTTON_FIRE_XOffset=1.4f;
	public static float BUTTON_FIRE_YOffset=-0.7f;//-----------
	//开火按钮的范围
	public static float[]BUTTON_FIRE_AREA;
	//雷达背景的大小
	public static float BUTTON_RADAR_BG_WIDTH=0.5f;
	public static float BUTTON_RADAR_BG_HEIGHT=0.5f;
	public static float scalMark=0.3f;
	
	//雷达指针的大小
	public static float BUTTON_RADAR_PLANE_WIDTH=0.35F;
	public static float BUTTON_RADAR_PLANE_HEIGHT=0.35F;
	//雷达的位置
	public static float BUTTON_RADAR_XOffset=1.4f;
	public static float BUTTON_RADAR_YOffset=0.7f;
	//雷达指针的旋转角度
	public static  float RADAR_DIRECTION;
	//武器选择按钮的宽度和高度
	public static float BUTTON_WEAPON_WIDTH=0.3f;
	public static float BUTTON_WEAPON_HEIGHT=0.3f;
	//武器选择按钮的平移
	public static float BUTTON_WEAPON_XOffset=-1.45f;
	public static float BUTTON_WEAPON_YOffset=0.8f;//-----------
	//武器的类别的索引
	public static int WEAPON_INDEX=0;//0表示子弹,1表示导弹
	//武器选择按钮的范围
	public static float[]BUTTON_WEAPON_AREA;
	
	//武器剩余量的宽度和高度
	public static float WEAPON_NUMBER_WIDTH=0.05f;
	public static float WEAPON_NUMBER_HEIGHT=0.15f;
	//武器选择按钮的平移
	public static float WEAPON_NUMBER_XOffset;
	public static float WEAPON_NUMBER_YOffset;//-----------
	
	//向上按钮的宽度高度
	public static float BUTTON_UP_WIDTH=0.5f;
	public static float BUTTON_UP_HEIGHT=0.5f;
	//向上按钮的平移
	public static float BUTTON_UP_XOffset=-1.45f;
	public static float BUTTON_UP_YOffset=-0.4f;//-----------
	//向上按钮的范围
	public static float[]BUTTON_UP_AREA;
	
	//向下按钮的宽度高度
	public static float BUTTON_DOWN_WIDTH=0.5f;
	public static float BUTTON_DOWN_HEIGHT=0.5f;
	//向下按钮的平移
	public static float BUTTON_DOWN_XOffset=-1.45f;
	public static float BUTTON_DOWN_YOffset=-0.8f;//-----------
	//向下按钮的范围
	public static float[]BUTTON_DOWN_AREA;

	//加载Max物体时的缩放比例
	public static final float ratio_3dmax=0.7f;
	
	//------飞机模型的相关参数-------------------
	public final static float BODYBACK_B=0.08f;				//机身椭球b轴长度
	public final static float BODYBACK_C=0.08f;				//机身椭球c轴长度
	public final static float BODYBACK_A=0.6f;				//机身椭球a轴长度
	public final static float BODYHEAD_A=0.2f;				//机头椭球a轴长度
	
	public static final float PLANE_SIZE=1.5f*2;				//hero机的尺寸
	public final static float BODYHEAD_B=0.08f;				//机头椭球b轴长度
	public final static float BODYHEAD_C=0.08f;				//机头椭球c轴长度
	public final static float CABIN_A=0.08f;				//机舱椭球a轴长度
	public final static float CABIN_B=0.032f;				//机舱椭球b轴长度
	public final static float CABIN_C=0.032f;				//机舱椭球c轴长度
	public static final float ENEMYPLANE_SIZE=3;			//敌机的尺寸
	//--------飞机控制相关参数------------------------------------
	public final static float PLANE_RATIO=20F;//飞机的缩放比例
	public static float PLANE_X=1675;//飞机的X位置
	public static float PLANE_Y=330;//飞机的Y位置
	public static float PLANE_Z=2060;//飞机的Z位置
	//设定飞机每次向前移动的距离
    public static float PLANE_MOVE_SPAN=12;
    //设定敌机飞行速度
    public static float ENEMYPLANE_SPAN=10;
    //飞机的飞行的海拔最大值
    public static float PLANE_HEIGHT_MAX=LAND_HIGHT*1.2f;
    //飞机旋转地角度    分别为绕 X轴,Y轴,和Z轴
    public static float rotationAngle_Plane_X;
    public static float rotationAngle_Plane_Y=DIRECTION_CAMERA;
    public static float rotationAngle_Plane_Z; 
	
	public final static float PLANE_UP_ROTATION_DOMAIN_X=45;//飞机向上仰的最大值
	public final static float PLANE_ROTATION_SPEED_SPAN_X=5f;//飞机上下俯仰角速度
	public final static float PLANE_DOWN_ROTATION_DOMAIN_X=-20;//飞机向下俯的最大值
	
	public final static float PLANE_ROTATION_SPEED_SPAN_Z=2f;//飞机左右倾斜角速度 
	public final static float PLANE_LEFT_ROTATION_DOMAIN_Z=16;//飞机向左倾斜的最大值
	public final static float PLANE_RIGHT_ROTATION_DOMAIN_Z=-16;//飞机向右倾斜的最大值
	
	//-----------------------子弹的相关参数
	public final static float BULLET_WIDTH=7F;
	public final static float BULLET_HEIGHT=4F;
	public final static float BULLET_SCALE=4f;//0.6F;//子弹的大小
	public final static float BULLET_MAX_DISTANCE=2000;//子弹的最大射程
	public static ArrayList<BulletForControl> bullet_List=new ArrayList<BulletForControl>();//发射出去的子弹列表
	public final static float BULLET_VELOCITY=40;//子弹的速度
	public static boolean isFireOn;//是否发射子弹
	public static int bullet_number;//子弹数量
	//-----------------------炮弹的相关参数
	public static ArrayList<BombForControl> bomb_List=new ArrayList<BombForControl>();//发射出去的子弹列表
	public static int fire_index=0;//0表示左机翼发射,1表示右机翼发射
	public final static float BOMB_MAX_DISTANCE=1500;//子弹的最大射程
	public final static float BOMB_VELOCITY=40;//炮弹的速度
	public static int bomb_number;//子弹数量
	//------------------------高射炮的相关参数
	public static float barrel_length=60;//炮管的长度
	public static float barrel_radius=6f;//炮管的半径
	public static float barbette_length=6;//炮台的长度
	public static float barbette_radius=35;//炮台的半径
	public static float cube_length=10;//挡板的长度
	public static float cube_width=15;//挡板的宽度
	public static float cube_height=25;//挡板的高度
	public static final float ARCHIE_MAX_DISTANCE=1500;//高射炮能够扫描到的最大范围
	public static ArrayList<ArchieForControl> archie_List=new ArrayList<ArchieForControl>();//高射炮的列表
	public static Map<Integer,ArrayList<ArchieForControl>> archie_Map=new HashMap<Integer,ArrayList<ArchieForControl>>();
	public static ArrayList<BombForControl> archie_bomb_List=new ArrayList<BombForControl>();//高射炮炮弹的列表
	public final static float ARCHIE_BOMB_VELOCITY=30;//炮弹的速度
	//------------------------坦克相关参数--------
	public static final float TANK_MAX_DISTANCE=600;//坦克能够扫描到的最大范围
	public static ArrayList<BombForControl> tank_bomb_List=new ArrayList<BombForControl>();//坦克炮弹的列表
	public final static float TANK_BOMB_VELOCITY=30f;//炮弹的速度
	public final static float tank_ratio=3f;//坦克的缩放比例
	//------通过输入流加载纹理的方法
	public static int initTexture(Resources r,int drawableId,boolean isMipmap)
	{
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组 
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		if(isMipmap)
		{//Mipmap纹理采样过滤参数	
			GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);   
			GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
		}
		else
		{//非Mipmap纹理采样过滤参数	
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		}
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        InputStream is = r.openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        GLUtils.texImage2D
        (
         GLES20.GL_TEXTURE_2D, //纹理类型
          0,   
          GLUtils.getInternalFormat(bitmapTmp), 
          bitmapTmp, //纹理图像
          GLUtils.getType(bitmapTmp), 
          0 //纹理边框尺寸
         );   
        //自动生成Mipmap纹理
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
	//敌机位置数组
	public static final float[][][] enemy_plane_place=
	{
		{
//			{-500,LAND_HIGHT+100,-500, 0,225,0,
//				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length-500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{500,LAND_HIGHT+100,-500, 0,225,0,
//				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
		},
		{
//			{-500,LAND_HIGHT+100,-500, 0,225,0,
//				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length-500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{500,LAND_HIGHT+100,-500, 0,225,0,
//				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
//			
//			{500,LAND_HIGHT+100,500, 0,225,0,
//				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
		},
		
		{
			{-500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length-500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
			
			{500,LAND_HIGHT+100,500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+500, 0,135,0,	
			-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			
			{-800,LAND_HIGHT+100,-800, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
//			{LAND_UNIT_SIZE*7*MapArray[mapId].length-700,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-700, 0,135,0,	
//				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
		},
		//--------------------特别行动------------------------------
		{
			{-500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length-500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
			
			{500,LAND_HIGHT+100,500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
		},
		{
			{-500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length-500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
			
			{500,LAND_HIGHT+100,500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+500, 0,135,0,	
			-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			
			{-800,LAND_HIGHT+100,-800, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length-700,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-700, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
		},
		{
			{-500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length-500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{500,LAND_HIGHT+100,-500, 0,225,0,
				LAND_UNIT_SIZE*7*MapArray[mapId].length+1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},
			{LAND_UNIT_SIZE*7*MapArray[mapId].length+500,200,LAND_UNIT_SIZE*7*MapArray[mapId].length-500, 0,135,0,	
				-1000,200,LAND_UNIT_SIZE*7*MapArray[mapId].length+1000},	
		},
	};
}
