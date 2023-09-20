package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

public class Bridge extends BNDrawer
{
	Bridge_In bridge_in;
	public Bridge(int programId) 
	{
		bridge_in=new Bridge_In(programId);
	}
	
	@Override
	public void drawSelf(int[] texId,int dyFlag)
	{
		MatrixState.pushMatrix();
		bridge_in.realDrawSelf(texId[0]);			
		MatrixState.popMatrix();		
	}
	
	private class Bridge_In
	{
		//单位长度
		float UNIT_SIZE=2.1f;
		
		//自定义渲染管线的id
		int mProgram;
		//总变化矩阵引用的id
		int muMVPMatrixHandle;
		//顶点位置属性引用id
		int maPositionHandle;
		//顶点纹理坐标属性引用id
		int maTexCoorHandle;
		
		//顶点数据缓冲和纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		//顶点数量
		int vCount=0;
		
		public Bridge_In(int programId) 
		{
			initVertexData();
			initShader(programId);
		}
		//初始化顶点数据的initVertexData方法
		public void initVertexData()
		{
			float[] vertex=new float[]
		    {
				//桥面的前侧
				-6*UNIT_SIZE,4.5f*UNIT_SIZE,0,
				-6*UNIT_SIZE,4*UNIT_SIZE,0,
				23*UNIT_SIZE,4*UNIT_SIZE,0,
				
				-6*UNIT_SIZE,4.5f*UNIT_SIZE,0,
				23*UNIT_SIZE,4*UNIT_SIZE,0,
				23*UNIT_SIZE,4.5f*UNIT_SIZE,0,
				//桥面下侧
				23*UNIT_SIZE,4*UNIT_SIZE,0,
				-6*UNIT_SIZE,4*UNIT_SIZE,0,
				-6*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,

				23*UNIT_SIZE,4*UNIT_SIZE,0,
				-6*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				23*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				//桥面后侧
				-6*UNIT_SIZE,4.5f*UNIT_SIZE,-4*UNIT_SIZE,
				23*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				-6*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				
				-6*UNIT_SIZE,4.5f*UNIT_SIZE,-4*UNIT_SIZE,
				23*UNIT_SIZE,4.5f*UNIT_SIZE,-4*UNIT_SIZE,
				23*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				//桥下侧的支柱——左侧的
				//桥下侧的支柱——梯形——左侧的
				//前侧
				2*UNIT_SIZE,4*UNIT_SIZE,0,
				3*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,

				2*UNIT_SIZE,4*UNIT_SIZE,0,
				4*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,0,
				//右侧
				5*UNIT_SIZE,4*UNIT_SIZE,0,
				4*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,

				5*UNIT_SIZE,4*UNIT_SIZE,0,
				4*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				//后侧
				2*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				3*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,

				2*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				//左侧
				2*UNIT_SIZE,4*UNIT_SIZE,0,
				2*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				3*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,

				2*UNIT_SIZE,4*UNIT_SIZE,0,
				3*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				3*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				//桥下侧的支柱——立方体——左侧的
				//前侧
				3*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				3*UNIT_SIZE,0,-UNIT_SIZE,
				4*UNIT_SIZE,0,-UNIT_SIZE,

				3*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				4*UNIT_SIZE,0,-UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				//右侧
				4*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				4*UNIT_SIZE,0,-UNIT_SIZE,
				4*UNIT_SIZE,0,-3*UNIT_SIZE,

				4*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				4*UNIT_SIZE,0,-3*UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				//后侧
				3*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				4*UNIT_SIZE,0,-3*UNIT_SIZE,
				3*UNIT_SIZE,0,-3*UNIT_SIZE,

				3*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				4*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				4*UNIT_SIZE,0,-3*UNIT_SIZE,
				//左侧
				3*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				3*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				3*UNIT_SIZE,0,-3*UNIT_SIZE,

				3*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				3*UNIT_SIZE,0,-3*UNIT_SIZE,
				3*UNIT_SIZE,0,-UNIT_SIZE,
				//桥下侧的支柱——左侧的

				//桥下侧的支柱——右侧的
				//桥下侧的支柱——梯形——右侧的
				//前侧
				10*UNIT_SIZE,4*UNIT_SIZE,0,
				11*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,

				10*UNIT_SIZE,4*UNIT_SIZE,0,
				12*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				13*UNIT_SIZE,4*UNIT_SIZE,0,
				//右侧
				13*UNIT_SIZE,4*UNIT_SIZE,0,
				12*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,

				13*UNIT_SIZE,4*UNIT_SIZE,0,
				12*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				13*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				//后侧
				10*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				11*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,

				10*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				13*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				//左侧
				10*UNIT_SIZE,4*UNIT_SIZE,0,
				10*UNIT_SIZE,4*UNIT_SIZE,-4*UNIT_SIZE,
				11*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,

				10*UNIT_SIZE,4*UNIT_SIZE,0,
				11*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				11*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				//桥下侧的支柱——立方体——右侧的
				//前侧
				11*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				11*UNIT_SIZE,0,-UNIT_SIZE,
				12*UNIT_SIZE,0,-UNIT_SIZE,

				11*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				12*UNIT_SIZE,0,-UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				//右侧
				12*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				12*UNIT_SIZE,0,-UNIT_SIZE,
				12*UNIT_SIZE,0,-3*UNIT_SIZE,

				12*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				12*UNIT_SIZE,0,-3*UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				//后侧
				11*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				12*UNIT_SIZE,0,-3*UNIT_SIZE,
				11*UNIT_SIZE,0,-3*UNIT_SIZE,

				11*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				12*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				12*UNIT_SIZE,0,-3*UNIT_SIZE,
				//左侧
				11*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				11*UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				11*UNIT_SIZE,0,-3*UNIT_SIZE,

				11*UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				11*UNIT_SIZE,0,-3*UNIT_SIZE,
				11*UNIT_SIZE,0,-UNIT_SIZE
		    };
			vCount=vertex.length/3;
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
	        {
				//桥面的前侧
				0,0,   0,0.1f,   2,0.1f,
				0,0,   2,0.1f,   2,0,
				//桥面下侧
				2,0,    0,0,   0,0.7f,
				2,0,    0,0.7f,   2,0.7f,
				//桥面后侧
				0,0,   2,0.1f,   0,0.1f,
				0,0,   2,0,   2,0.1f,
				//桥下侧的支柱——左侧的
				//桥下侧的支柱——梯形——左侧的
				//前侧
				0.0667f,0.2f,   0.1333f,0.4f,   0.2667f,0.4f,
				0.0667f,0.2f,   0.2667f,0.4f,   0.3333f,0.2f,
				//右侧
				0.0667f,0.2f,   0.1333f,0.4f,   0.2667f,0.4f,
				0.0667f,0.2f,   0.2667f,0.4f,   0.3333f,0.2f,
				//后侧
				0.0667f,0.2f,   0.2667f,0.4f,   0.1333f,0.4f,
				0.0667f,0.2f,   0.3333f,0.2f,   0.2667f,0.4f,
				//左侧
				0.3333f,0.2f,   0.0667f,0.2f,   0.1333f,0.4f,
				0.3333f,0.2f,   0.1333f,0.4f,   0.2667f,0.4f,
				//桥下侧的支柱——立方体——左侧的
				//前侧
				0.1333f,0.4f,   0.1333f,1,   0.2667f,1,
				0.1333f,0.4f,   0.2667f,1,   0.2667f,0.4f,
				//右侧
				0.1333f,0.4f,   0.1333f,1,   0.2667f,1,
				0.1333f,0.4f,   0.2667f,1,   0.2667f,0.4f,
				//后侧
				0.1333f,0.4f,   0.2667f,1,   0.1333f,1,
				0.1333f,0.4f,   0.2667f,0.4f,   0.2667f,1,
				//左侧
				0.2667f,0.4f,   0.1333f,0.4f,   0.1333f,1,
				0.2667f,0.4f,   0.1333f,1,   0.2667f,1,
				//桥下侧的支柱——左侧的

				//桥下侧的支柱——右侧的
				//桥下侧的支柱——梯形——右侧的
				//前侧
				0.6667f,0.2f,   0.7333f,0.4f,   0.8667f,0.4f,
				0.6667f,0.2f,   0.8667f,0.4f,   0.9333f,0.2f,
				//右侧
				0.6667f,0.2f,   0.7333f,0.4f,   0.8667f,0.4f,
				0.6667f,0.2f,   0.8667f,0.4f,   0.9333f,0.2f,
				//后侧
				0.6667f,0.2f,   0.8667f,0.4f,   0.7333f,0.4f,
				0.6667f,0.2f,   0.9333f,0.2f,   0.8667f,0.4f,
				//左侧
				0.9333f,0.2f,   0.6667f,0.2f,   0.7333f,0.4f,
				0.9333f,0.2f,   0.7333f,0.4f,   0.8667f,0.4f,
				//桥下侧的支柱——立方体——右侧的
				//前侧
				0.7333f,0.4f,   0.7333f,1,   0.8667f,1,
				0.7333f,0.4f,   0.8667f,1,   0.8667f,0.4f,
				//右侧
				0.7333f,0.4f,   0.7333f,1,   0.8667f,1,
				0.7333f,0.4f,   0.8667f,1,   0.8667f,0.4f,
				//后侧
				0.7333f,0.4f,   0.8667f,1,   0.7333f,1,
				0.7333f,0.4f,   0.8667f,0.4f,   0.8667f,1,
				//左侧
				0.8667f,0.4f,   0.7333f,0.4f,   0.7333f,1,
				0.8667f,0.4f,   0.7333f,1,   0.8667f,1,
	        };
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		
		//初始化着色器的initShader方法
		public void initShader(int programId) 
		{
			//基于顶点着色器与片元着色器创建程序
	        mProgram = programId;
	        //获取程序中顶点位置属性引用id  
	        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
	        //获取程序中顶点纹理坐标属性引用id  
	        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
	        //获取程序中总变换矩阵引用id
	        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
		}
		
		//自定义的绘制方法drawSelf
		public void realDrawSelf(int texId)
		{
			//制定使用某套shader程序
	   	 	GLES20.glUseProgram(mProgram); 
	        //将最终变换矩阵传入shader程序
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
	        
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
			//传入纹理坐标数据
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
	        
	        //绘制纹理矩形
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
	}
}