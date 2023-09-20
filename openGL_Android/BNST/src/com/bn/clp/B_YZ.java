package com.bn.clp;
 
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

//代表半崖子的类    
public class B_YZ extends BNDrawer
{
	//单位长度 
	float UNIT_SIZE=2.5f;    
	X_BYZ x_byz;     
	public B_YZ(int programId)
	{         
		x_byz=new X_BYZ(programId);
	}
	
	@Override
	public void drawSelf(int[] texId,int dyFlag)
	{
		MatrixState.pushMatrix();
		x_byz.drawSelf(texId[0], texId[1]);
		MatrixState.popMatrix();
		//右侧 
		MatrixState.pushMatrix();
		MatrixState.translate(22*UNIT_SIZE, 0, -4*UNIT_SIZE);
		MatrixState.rotate(180, 0, 1, 0);
		x_byz.drawSelf(texId[0], texId[1]);
		MatrixState.popMatrix(); 
	}
	//半崖子内部类
	private class X_BYZ
	{
		//自定义渲染管线id
		int mProgram;
		//顶点位置属性的引用id
		int maPositionHandle;
		//顶点纹理坐标属性的引用id
		int maTexCoorHandle;
		//总变化矩阵的引用id
		int muMVPMatrixHandle;
		
		//草地的id
		int sTextureGrassHandle;
		//石头的id
		int sTextureRockHandle;
		//起始x值
		int b_YZ_StartXHandle;
		//长度
		int b_YZ_XSpanHandle;
		
		
		//顶点坐标数据缓冲和顶点纹理数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		int vCount=0;
		
		public X_BYZ(int programId)
		{
			initVertexData();
			initShader(programId);
		}
		//初始化顶点数据的initVertexData方法
		public void initVertexData()
		{
			float[] vertex=new float[]
	        {
				//底部				
				//中间部分
				//前侧
				UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				UNIT_SIZE,2*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-UNIT_SIZE,

				UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,5*UNIT_SIZE,-UNIT_SIZE,
				//右侧
				5*UNIT_SIZE,4*UNIT_SIZE,-UNIT_SIZE,
				UNIT_SIZE,2*UNIT_SIZE,-UNIT_SIZE,
				UNIT_SIZE,2*UNIT_SIZE,-3*UNIT_SIZE,

				5*UNIT_SIZE,4*UNIT_SIZE,-UNIT_SIZE,
				UNIT_SIZE,2*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-3*UNIT_SIZE,
				//后侧
				UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-3*UNIT_SIZE,
				UNIT_SIZE,2*UNIT_SIZE,-3*UNIT_SIZE,

				UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,5*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-3*UNIT_SIZE,
				//左侧
				5*UNIT_SIZE,5*UNIT_SIZE,-3*UNIT_SIZE,
				UNIT_SIZE,3*UNIT_SIZE,-3*UNIT_SIZE,
				UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,

				5*UNIT_SIZE,5*UNIT_SIZE,-3*UNIT_SIZE,
				UNIT_SIZE,3*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,5*UNIT_SIZE,-UNIT_SIZE,
				//最上侧部分
				//前侧
				5*UNIT_SIZE,5*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-UNIT_SIZE,

				5*UNIT_SIZE,5*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.7f*UNIT_SIZE,-UNIT_SIZE,
				//右侧（下）
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-3*UNIT_SIZE,

				7*UNIT_SIZE,4.3f*UNIT_SIZE,-UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-3*UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-3*UNIT_SIZE,
				//后侧
				5*UNIT_SIZE,5*UNIT_SIZE,-3*UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,4*UNIT_SIZE,-3*UNIT_SIZE,

				5*UNIT_SIZE,5*UNIT_SIZE,-3*UNIT_SIZE,
				7*UNIT_SIZE,4.7f*UNIT_SIZE,-3*UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-3*UNIT_SIZE,
				//左侧（上）
				7*UNIT_SIZE,4.7f*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,5*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,5*UNIT_SIZE,-UNIT_SIZE,

				7*UNIT_SIZE,4.7f*UNIT_SIZE,-3*UNIT_SIZE,
				5*UNIT_SIZE,5*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.7f*UNIT_SIZE,-UNIT_SIZE,
				//最右侧的封闭
				7*UNIT_SIZE,4.7f*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-3*UNIT_SIZE,

				7*UNIT_SIZE,4.7f*UNIT_SIZE,-UNIT_SIZE,
				7*UNIT_SIZE,4.3f*UNIT_SIZE,-3*UNIT_SIZE,
				7*UNIT_SIZE,4.7f*UNIT_SIZE,-3*UNIT_SIZE
	        };
			vCount=vertex.length/3;
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
  		    {
  				//中间部分
  				//前侧
  				0.1f,0.4f,   0.1f,0.6f,   0.5f,0.2f,
  				0.1f,0.4f,   0.5f,0.2f,   0.5f,0,
  				//右侧
  				0.5f,0.2f,   0.1f,0.6f,   0.2f,1.0f,
  				0.5f,0.2f,   0.2f,1.0f,   0.6f,0.6f,
  				//后侧
  				0.1f,0.4f,   0.5f,0.2f,   0.1f,0.6f,
  				0.1f,0.4f,   0.5f,0,   0.5f,0.2f,
  				//左侧
  				0.6f,0.6f,   0.2f,1.0f,   0.1f,0.6f,
  				0.6f,0.6f,   0.1f,0.6f,   0.5f,0.2f,
  				//最上侧部分
  				//前侧
  				0.5f,0,   0.5f,0.2f,   0.7f,0.14f,
  				0.5f,0,   0.7f,0.14f,   0.7f,0.06f,
  				//右侧（下）
  				0.7f,0.14f,   0.5f,0.2f,   0.6f,0.6f,
  				0.7f,0.14f,   0.6f,0.6f,   0.8f,0.54f,
  				//后侧
  				0.5f,0,   0.7f,0.14f,   0.5f,0.2f,
  				0.5f,0,   0.7f,0.06f,   0.7f,0.14f,
  				//左侧（上）
  				0.8f,0.54f,   0.6f,0.6f,   0.5f,0.2f,
  				0.8f,0.54f,   0.5f,0.2f,   0.7f,0.14f,
  				//最右侧的封闭
  				0.7f,0.06f,   0.7f,0.14f,   0.9f,0.14f,
  				0.7f,0.06f,   0.9f,0.14f,   0.9f,0.06f
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
			mProgram=programId;
			//获得顶点坐标属性的引用id
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//获得顶点纹理坐标属性的引用id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			//获得总变化矩阵引用的id
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");			
			
			//纹理
			//草地
			sTextureGrassHandle=GLES20.glGetUniformLocation(mProgram, "sTextureGrass");
			//石头
			sTextureRockHandle=GLES20.glGetUniformLocation(mProgram, "sTextureRock");
			//x位置
			b_YZ_StartXHandle=GLES20.glGetUniformLocation(mProgram, "b_YZ_StartX");
			//x最大
			b_YZ_XSpanHandle=GLES20.glGetUniformLocation(mProgram, "b_YZ_XSpan");
		}
		//自定义的绘制方法
		public void drawSelf(int texIdGrass,int texIdRock)
		{
			//指定某套Shader程序
			GLES20.glUseProgram(mProgram);
			//将最终变化矩阵传入到Shader程序
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//为画笔指定顶点坐标——>此处将坐标传入Shader??????
			GLES20.glVertexAttribPointer
			(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT, 
				false, 
				3*4, 
				mVertexBuffer
			);
			//传入顶点纹理坐标
			GLES20.glVertexAttribPointer
			(
				maTexCoorHandle, 
				2, 
				GLES20.GL_FLOAT, 
				false, 
				2*4, 
				mTexCoorBuffer
			);
			//允许使用顶点坐标以及顶点纹理坐标
			GLES20.glEnableVertexAttribArray(maPositionHandle);
			GLES20.glEnableVertexAttribArray(maTexCoorHandle);
			
			//启用纹理
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIdGrass);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIdRock);
			GLES20.glUniform1i(sTextureGrassHandle, 0);//使用0号纹理
	        GLES20.glUniform1i(sTextureRockHandle, 1); //使用1号纹理
			
	        //传送相应的x参数
	        GLES20.glUniform1f(b_YZ_StartXHandle, 0);
	        GLES20.glUniform1f(b_YZ_XSpanHandle, 7*UNIT_SIZE);
	        
			//绘制物体
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);		
		}
	}
}