package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.bn.core.MatrixState;
import android.opengl.GLES20;
import static com.bn.clp.Constant.*;
//绘制计速器的类
public class Dashboard
{
	TextureRect tr;
	DrawLine dl;
	float angle;
	float startAngle=125;  
	  
	public Dashboard(int programId)
	{
		tr=new TextureRect(programId,Self_Adapter_Data_TRASLATE[screenId][11],0.25f); 
		dl=new DrawLine(programId,0.005f,0.16f);
	}
	//绘制方法
	public void drawSelf(int texId)
	{
		//绘制仪表盘
		MatrixState.pushMatrix();
		tr.drawSelf(texId);
		MatrixState.popMatrix();
		//绘制线
		MatrixState.pushMatrix();
		MatrixState.translate(0.005f, -0.01f, 0.5f);
		MatrixState.rotate(angle, 0, 0, 1);
		dl.drawSelf(texId); 
		MatrixState.popMatrix();
	}
	
	public void changeangle(float v)//指针角度转的方法
	{
		float vSpan=Max_BOAT_V_FINAL/250;//每一仪表盘指针角度所表示的速度
		{
			float v_Angle=v/vSpan;
			angle=startAngle-v_Angle;
		}		
	}
	
	//绘制仪表盘的类
	private class TextureRect
	{
		//自定义渲染管线着色器程序id
		int mProgram;
		//总变化矩阵的引用id
		int muMVPMatrixHandle;
		//顶点属性的引用id
		int maPositionHandle;
		//顶点纹理属性的引用id
		int maTexCoorHandle;
		//顶点坐标数据缓冲、顶点纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		//顶点数量
		int vCount;
		
		public TextureRect(int mProgramId,float width,float height)
		{
			initVertexData(width,height);
			initShader(mProgramId);
		}
		//初始化相应顶点数据的方法
		public void initVertexData(float width,float height)
		{
			float[] vertices=new float[]
	        {
				-width,height,0,
				-width,-height,0,
				width,-height,0,
				
				-width,height,0,
				width,-height,0,
				width,height,0,
	        };
			vCount=6;
			//设置顶点坐标缓冲区
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertices);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
	        {
				0,0.129f,  0,0.98f,  1,0.98f,
				0,0.129f,  1,0.98f,  1,0.129f,
	        };
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的initShader方法
		public void initShader(int programId)
		{
			//创建自定义的Shader程序
			mProgram=programId;
			//获得顶点坐标属性的引用id
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//获得顶点纹理坐标属性的引用id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			//获得总变换矩阵的引用id
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//自定义的绘制方法
		public void drawSelf(int texId)
		{
			//使用某套指定的Shader程序
			GLES20.glUseProgram(mProgram);
			//将总变换矩阵传入Shader程序
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//传入顶点数据
			GLES20.glVertexAttribPointer
			(
				maPositionHandle,
				3, 
				GLES20.GL_FLOAT,
				false, 
				3*4, 
				mVertexBuffer
			);
			//传入顶点纹理坐标数据
			GLES20.glVertexAttribPointer
			(
				maTexCoorHandle, 
				2, 
				GLES20.GL_FLOAT,
				false, 
				2*4, 
				mTexCoorBuffer
			);
			//允许顶点位置数据数组
			GLES20.glEnableVertexAttribArray(maPositionHandle);
			GLES20.glEnableVertexAttribArray(maTexCoorHandle);
			 //绑定纹理
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
			//绘制
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
		}
	}
	//红线类
	private class DrawLine
	{
		//自定义渲染管线着色器程序id
		int mProgram;
		//总变化矩阵的引用id
		int muMVPMatrixHandle;
		//顶点属性的引用id
		int maPositionHandle;
		//顶点纹理属性的引用id
		int maTexCoorHandle;
		//顶点坐标数据缓冲、顶点纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		//顶点数量
		int vCount;
		
		public DrawLine(int mProgramId,float width,float height)
		{
			initVertexData(width,height);
			initShader(mProgramId);
		}
		//初始化相应顶点数据的initVertexData方法
		public void initVertexData(float width,float height)
		{
			float[] vertices=new float[]
	        {
				-width,height,0,
				-width,0,0,
				width,0,0,
				
				-width,height,0,
				width,0,0,
				width,height,0,
	        };
			vCount=6;
			//设置顶点坐标缓冲区
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertices);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
	        {
				0,0,  0,0.125f,  0.45f,0.125f,
				0,0,  0.45f,0.125f,  0.45f,0,
	        };
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的initShader方法
		public void initShader(int programId)
		{
			//创建自定义的Shader程序
			mProgram=programId;
			//获得顶点坐标属性的引用id
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//获得顶点纹理坐标属性的引用id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			//获得总变换矩阵的引用id
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//自定义的绘制方法
		public void drawSelf(int texId)
		{
			//使用某套指定的Shader程序
			GLES20.glUseProgram(mProgram);
			//将总变换矩阵传入Shader程序
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//传入顶点数据
			GLES20.glVertexAttribPointer
			(
				maPositionHandle,
				3, 
				GLES20.GL_FLOAT,
				false, 
				3*4, 
				mVertexBuffer
			);
			//传入顶点纹理坐标数据
			GLES20.glVertexAttribPointer
			(
				maTexCoorHandle, 
				2, 
				GLES20.GL_FLOAT,
				false, 
				2*4, 
				mTexCoorBuffer
			);
			//允许顶点位置数据数组
			GLES20.glEnableVertexAttribArray(maPositionHandle);
			GLES20.glEnableVertexAttribArray(maTexCoorHandle);
			 //绑定纹理
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
			//绘制
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
		}
	}
	
}