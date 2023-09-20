package com.bn.commonObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.bn.core.MatrixState;

import android.opengl.GLES20;

//表示星空天球的类
public class SkyNight 
{
	float skyR=30.0f;//天球半径
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    int vCount=0;//星星数量
    float scale;//星星尺寸
    
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
    
    int mProgram;//自定义渲染管线着色器程序id 
    int muMVPMatrixHandle;//总变换矩阵引用id   
    int maPositionHandle; //顶点位置属性引用id  
    int uPointSizeHandle;//顶点尺寸参数引用
    
    public SkyNight(float scale,int vCount,float skyR)
    {
    	this.skyR=skyR;
    	this.scale=scale;
    	this.vCount=vCount;  
    	initVertexData();
    }
    
    //初始化顶点坐标的方法
    public void initVertexData()
    {    	  	
    	//顶点坐标数据的初始化================begin=======================================       
        float vertices[]=new float[vCount*6];
        for(int i=0;i<vCount;i++)
        {
        	//随机产生每个星星的xyz坐标
        	double angleTempJD=Math.PI*2*Math.random();
        	double angleTempWD=Math.PI/3*(Math.random());
        	vertices[i*6]=(float)(skyR*Math.cos(angleTempWD)*Math.sin(angleTempJD));
        	vertices[i*6+1]=(float)(skyR*Math.sin(angleTempWD));
        	vertices[i*6+2]=(float)(skyR*Math.cos(angleTempWD)*Math.cos(angleTempJD));
        	
        	vertices[i*6+3]=(float)(skyR*Math.cos(angleTempWD)*Math.sin(angleTempJD));
        	vertices[i*6+4]=-(float)(skyR*Math.sin(angleTempWD));
        	vertices[i*6+5]=(float)(skyR*Math.cos(angleTempWD)*Math.cos(angleTempJD));
        	
        	System.out.println();
        }
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个Float四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

    }

    //初始化着色器的initShader方法
    public void initShader(int mProgram)
    {
        this.mProgram = mProgram;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");        
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //获取顶点尺寸参数引用
        uPointSizeHandle = GLES20.glGetUniformLocation(mProgram, "uPointSize"); 
    }
    
    public void drawSelf()
    {  
    	//制定使用某套shader程序
   	    GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);  
        //将顶点尺寸传入Shader程序
        GLES20.glUniform1f(uPointSizeHandle, scale);  
        //传入顶点位置数据    
        GLES20.glVertexAttribPointer        
        (
        		maPositionHandle,   
        		3, 
        		GLES20.GL_FLOAT, 
        		false,
                3*4, 
                mVertexBuffer   
        );   
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);         
        //绘制星星点    
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount*2); 
    }
}
