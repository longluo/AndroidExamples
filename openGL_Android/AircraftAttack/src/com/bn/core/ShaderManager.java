package com.bn.core;

import android.content.res.Resources;
/*
 * 该shader管理器主要是用于加载shader和编译shader
 */
public class ShaderManager
{
	final static String[][] shaderName=
	{
		{"vertex_tex_only.sh","frag_tex_only.sh"},//loading 界面的shader
		{"vertex_tex_water.sh","frag_tex_water.sh"},//水面流动的shader
		{"vertex_landform.sh","frag_tex_landform.sh"},//地形的shader
		{"vertex_button.sh","frag_button.sh"},//按钮的shader
		{"vertex_xk.sh","frag_xk.sh"},//星空着色器
		{"vertex_xue.sh","frag_xue.sh"},//血着色器
		{"vertex_color.sh","frag_color.sh"},//仅有颜色着色器
	};
	static String[]mVertexShader=new String[shaderName.length];//顶点着色器字符串数组
	static String[]mFragmentShader=new String[shaderName.length];//片元着色器字符串数组
	static int[] program=new int[shaderName.length];//程序数组
	//加载loading 界面的shader
	public static void loadFirstViewCodeFromFile(Resources r)
	{
		mVertexShader[0]=ShaderUtil.loadFromAssetsFile(shaderName[0][0],r);
		mFragmentShader[0]=ShaderUtil.loadFromAssetsFile(shaderName[0][1], r);
	}
	//加载shader字符串
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=1;i<shaderName.length;i++)
		{
			//加载顶点着色器的脚本内容       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //加载片元着色器的脚本内容 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	//这里主要是编译loading界面中的shader
	public static void compileFirstViewShader()
	{
			program[0]=ShaderUtil.createProgram(mVertexShader[0], mFragmentShader[0]);
	}
	//编译其他的shader
	public static void compileShader()
	{
		for(int i=1;i<shaderName.length;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//这里返回的是首次加载的shader
	public static int getFirstViewShaderProgram()
	{
		return program[0];
	}
	//返回的是只有纹理的shader程序
	public static int getOnlyTextureShaderProgram()
	{
		return program[0];
	}
	//这里返回的是水面流动的shader程序
	public static int getWaterTextureShaderProgram()
	{
		return program[1];
	}
	//这里返回的是地形的shader
	public static int getLandformTextureShaderProgram()
	{
		return program[2];
	}
	//这里返回的是按钮的shader
	public static int getButtonTextureShaderProgram()
	{
		return program[3];
	}
	//这里返回星空的颜色的shader
	public static int getStarrySkyShaderProgram()
	{
		return program[4];
	}
	//这里返回血颜色的shader
	public static int getStarryXueShaderProgram()
	{
		return program[5];
	}
	//这里返回血颜色的shader
	public static int getOnlyColorShaderProgram()
	{
		return program[6];
	}
}
