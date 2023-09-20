package com.bn.tl;


import android.content.res.Resources;

public class ShaderManager
{
	final static int shaderCount=5;
	final static String[][] shaderName=
	{
		{"vertex.sh","frag.sh"},
		{"vertex_yingzi.sh","frag_yingzi.sh"},
		{"lightvertex.sh","lightfrag.sh"},
		{"vertex.sh","frag_blackground.sh"},
		{"vertex_net.sh","frag_net.sh"}
	};
	static String[]mVertexShader=new String[shaderCount];
	static String[]mFragmentShader=new String[shaderCount];
	static int[] program=new int[shaderCount];
	
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderCount;i++)
		{
			//加载顶点着色器的脚本内容       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //加载片元着色器的脚本内容 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	//这里主要是编译3D中欢迎界面中的shader
	public static void compileShader()
	{
		for(int i=0;i<1;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//编译3D物体的shader
	public static void compileShaderReal()
	{
		for(int i=1;i<shaderCount;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//这里返回的是普通的shader程序
	public static int getCommTextureShaderProgram()
	{
		return program[0];
	}
	//这里返回的是影子的shader程序
	public static int getShadowshaderProgram()
	{
		return program[1];
	}
	//这里返回的是光照纹理的shader程序
	public static int getLigntAndTexturehaderProgram()
	{
		return program[2];
	}
	//这里返回的是仪表板背景为黑色的然后再着色器中转换成透明的shader程序
	public static int getBlackgroundShaderProgram()
	{
		return program[3];
	}
	public static int getBasketNetShaderProgram()
	{
		return program[4];
	}
}
