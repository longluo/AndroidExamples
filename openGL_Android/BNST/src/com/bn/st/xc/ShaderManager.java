package com.bn.st.xc;


import com.bn.core.ShaderUtil;

import android.content.res.Resources;

public class ShaderManager
{
	final static int shaderCount=2;
	final static String[][] shaderName=
	{
		{"vertex_tex.sh","frag_tex.sh"},
		{"vertex_color.sh","frag_color.sh"},
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
	//编译shader
	public static void compileShaderReal()
	{
		for(int i=0;i<shaderCount;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//这里返回的是纹理的shader程序
	public static int getCommTextureShaderProgram()
	{
		return program[0];
	}
	//这里返回的是颜色的shader程序
	public static int getColorshaderProgram()
	{
		return program[1];
	}
}
