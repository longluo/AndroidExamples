package com.bn.clp;
import com.bn.core.ShaderUtil;

import android.content.res.Resources;

public class ShaderManager
{
	final static int shaderCount=7;
	final static String[][] shaderName=
	{
		{"vertex_color_light.sh","frag_color_light.sh"},
		{"vertex_b_yz.sh","frag_b_yz.sh"},
		{"vertex_tex_g.sh","frag_tex_g.sh"},
		{"vertex_tex_xz.sh","frag_tex_xz.sh"},
		{"vertex_mountion.sh","frag_mountion.sh"},
		{"vertex_prograss.sh","frag_prograss.sh"},
		{"vertex_tex_xz.sh","frag_weilang.sh"}
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
	//这里主要是编译shader
	public static void compileShader()
	{		
		program[0]=ShaderUtil.createProgram(mVertexShader[0], mFragmentShader[0]);
		program[1]=ShaderUtil.createProgram(mVertexShader[1], mFragmentShader[1]);
		program[3]=ShaderUtil.createProgram(mVertexShader[3], mFragmentShader[3]);
		program[4]=ShaderUtil.createProgram(mVertexShader[4], mFragmentShader[4]);
		program[6]=ShaderUtil.createProgram(mVertexShader[6], mFragmentShader[6]);
	}
	
	public static void compileShaderHY()
	{
		program[2]=ShaderUtil.createProgram(mVertexShader[2], mFragmentShader[2]);
		program[5]=ShaderUtil.createProgram(mVertexShader[5], mFragmentShader[5]);
	}
	
	
	//这里返回的是设置光照的Shader程序
	public static int getLightShaderProgram()
	{
		return program[0];
	}
	//这里返回的是半崖子的Shader程序
	public static int getBYZTextureShaderProgram()
	{
		return program[1];
	}
	//这里返回的是只有纹理的Shader程序
	public static int getTextureShaderProgram()
	{
		return program[2];
	}
	//这里返回的是绘制水面时用到的Shader程序
	public static int getWaterShaderProgram()
	{
		return program[3];
	}
	//这里返回绘制山时用到的Shader程序
	public static int getMountionShaderProgram()
	{
		return program[4];
	}
	//这里返回的是进度条的Shader程序
	public static int getPrograssShaderProgram()
	{
		return program[5];
	}  
	//返回尾浪的shader程序
	public static int getWeiLangShaderProgram()
	{
		return program[6];
	}
}