package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20; 
import com.bn.core.MatrixState;

//该类是船埠头
public class Dock extends BNDrawer
{
	DockIn dockIn;
	public Dock(int programId)
	{
		dockIn=new DockIn(programId);
	}
	@Override
	public void drawSelf(int[] texId, int dyFlag)
	{
		MatrixState.pushMatrix();
		dockIn.drawSelf(texId[0]);
		MatrixState.popMatrix();
	}
	
	private class DockIn
	{
		//单位长度
		float UNIT_SIZE=0.3f;
		
		//自定义渲染管线着色器的id
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
		
		public DockIn(int programId)
		{
			initVertexData();
			initShader(programId);
		}
		//初始化顶点数据的initVertexData方法
		public void initVertexData()
		{
			float[] vertex=new float[]
		    {
					//板上面
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					
					15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					//板下面
					15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					//板前面
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					
					15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					//板后面
					15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					//板左面
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					
					-15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,-5*UNIT_SIZE,
					-15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					//板右面
					15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,8.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					
					15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,    
					15*UNIT_SIZE,9.5f*UNIT_SIZE,5*UNIT_SIZE,
					15*UNIT_SIZE,9.5f*UNIT_SIZE,-5*UNIT_SIZE,
					
					//---------------------左上角支柱---------------------------
					//上面
					-13*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					
					-12*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					//下面
					-12*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					
					-13*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					//前面
					-13*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					
					-12*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					//后面
					-12*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					
					-13*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					//左面
					-13*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					
					-13*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					//右面
					-12*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					
					-12*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					//----------------------左下角支柱------------------
					//上面
					-13*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					
					-12*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					//下面
					-12*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					
					-13*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					
					//前面
					-13*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					
					-12*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					//后面
					-12*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					
					-13*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					//左面
					-13*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					
					-13*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-13*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					//右面
					-12*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					
					-12*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-12*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					//----------右上角支柱------------
					//上面
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					
					//下面
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					//前面
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					//后面
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					//左面
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					//右面
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					
					0.5f*UNIT_SIZE,12*UNIT_SIZE,-5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,-5*UNIT_SIZE,
					//----------右下角支柱------------
					//上面
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					
					0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					//下面
					0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					    
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					//前面
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					
					0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					//后面
					0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					//左面
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					
					-0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE,
					-0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					//右面
					0.5f*UNIT_SIZE,12*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					
					0.5f*UNIT_SIZE,12*UNIT_SIZE,4*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,5*UNIT_SIZE,
					0.5f*UNIT_SIZE,0*UNIT_SIZE,4*UNIT_SIZE, 
		    };
			vCount=vertex.length/3;
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
	        {
				//--------一个长方体的纹理坐标--------------
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				//--------一个长方体的纹理坐标--------------
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				//--------一个长方体的纹理坐标--------------
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				//--------一个长方体的纹理坐标--------------
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				//--------一个长方体的纹理坐标--------------
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
				
				0,0, 0,1, 1,0,
				1,0, 0,1, 1,1,
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
	        mProgram =programId;
	        //获取程序中顶点位置属性引用id  
	        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
	        //获取程序中顶点纹理坐标属性引用id  
	        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
	        //获取程序中总变换矩阵引用id
	        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
		}
		
		//自定义的绘制方法drawSelf
		public void drawSelf(int texId)
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
