package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
import com.bn.core.MatrixState;
 
//起始结束牌子
public class StartAndEnd
{
	//开始结束条幅的条幅
	TextureRect tr;
	//开始结束条幅的支柱
	Cylinder cylinder;
	
	//切分的角度
	final float ANGLE_SPAN=20;
	final float UNIT_SIZE=1.0f;
	final float R=0.5f;
	final float width=24;
	final float cHeight=12;
	final float height=2;
	public StartAndEnd(int programId)
	{			
		cylinder=new Cylinder(programId,R,ANGLE_SPAN,cHeight);
		tr=new TextureRect(programId,width-R,height);
	}
	//总的绘制方法drawSelf
	public void drawSelf(int zzTexId,int tfTexId,float y,int dyFlag)
	{
		if(dyFlag==0)//绘制实体
		{
			//左侧支柱
			MatrixState.pushMatrix();
			MatrixState.translate(-width, 0, 0);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//右侧支柱
			MatrixState.pushMatrix();
			MatrixState.translate(width, 0, 0);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//条幅
			MatrixState.pushMatrix();
			MatrixState.translate(0, cHeight-height, 0); 
			tr.drawSelf(tfTexId);
			MatrixState.popMatrix();
		}
		else if(dyFlag==1)//绘制倒影
		{
			//实际绘制时Y的零点
			float yTranslate=y;
			//进行镜像绘制时的调整值
			float yjx=(0-yTranslate)*2;
			
			//关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //左侧支柱
			MatrixState.pushMatrix();
			MatrixState.translate(-width, 0, 0);
			MatrixState.translate(0, yjx, 0);
			MatrixState.scale(1, -1, 1);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//右侧支柱
			MatrixState.pushMatrix();
			MatrixState.translate(width, 0, 0);
			MatrixState.translate(0, yjx, 0);
			MatrixState.scale(1, -1, 1);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//条幅
			MatrixState.pushMatrix();
			MatrixState.translate(0, cHeight-height, 0); 
			MatrixState.translate(0, (0-yTranslate-cHeight+height)*2, 0); 
			MatrixState.scale(1, -1, 1); 
			tr.drawSelf(tfTexId);
			MatrixState.popMatrix();
			//打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
		}
	} 
	
	//内部类——圆柱
	private class Cylinder
	{
		//自定义Shader程序的引用
		int mProgram;
		//总变换矩阵的引用id
		int muMVPMatrixHandle;
		//顶点属性的引用id
		int maPositionHandle;
		//顶点纹理坐标的引用id
		int maTexCoorHandle;
		
		//顶点坐标数据缓冲
		FloatBuffer mVertexBuffer;
		//顶点纹理坐标数据缓冲
		FloatBuffer mTexCoorBuffer;
		int vCount=0;//顶点数量
		
		//R为圆柱底部的半径，r为圆柱上部的半径，angle_span表示的是切分的角度
		public Cylinder(int programId,float R,float angle_span,float height)
		{
			initVertexData(R,angle_span,height);
			initShader(programId);
		}
		//初始化坐标数据的方法
		public void initVertexData(float R,float angle_span,float height)
		{
			List<Float> tempList=new ArrayList<Float>();
			for(float vAngle=0;vAngle<360;vAngle=vAngle+angle_span)
			{
				float x0=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float y0=height; 
				float z0=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x1=(float) (R*Math.cos(Math.toRadians(vAngle))); 
				float y1=-height;
				float z1=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x2=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y2=-height;
				float z2=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				float x3=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y3=height;
				float z3=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				tempList.add(x0); tempList.add(y0); tempList.add(z0);
				tempList.add(x1); tempList.add(y1); tempList.add(z1);
				tempList.add(x3); tempList.add(y3); tempList.add(z3);

				tempList.add(x3); tempList.add(y3); tempList.add(z3);
				tempList.add(x1); tempList.add(y1); tempList.add(z1);
				tempList.add(x2); tempList.add(y2); tempList.add(z2);
			}
			vCount=tempList.size()/3;//顶点数量
			float[] vertex=new float[tempList.size()];
			for(int i=0;i<tempList.size();i++)
			{
				vertex[i]=tempList.get(i);
			}
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=generateTexCoor((int)(360/angle_span),1,1,1);
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的initShader方法
		public void initShader(int programId)
		{
			mProgram=programId;
			//获得顶点坐标数据的引用
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//顶点纹理坐标的引用id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//自定义的绘制方法
		public void drawSelf(int texId)
		{
			//使用某套指定的Shader程序
			GLES20.glUseProgram(mProgram);
			//将最终变换矩阵传入到Shader程序中
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//传入顶点坐标数据
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
		//自动切分纹理产生纹理数组的方法
	    public float[] generateTexCoor(int bw,int bh,float width,float height)
	    {
	    	float[] result=new float[bw*bh*6*2]; 
	    	float sizew=width/bw;//列数
	    	float sizeh=height/bh;//行数
	    	int c=0;
	    	for(int i=0;i<bh;i++)
	    	{
	    		for(int j=0;j<bw;j++)
	    		{
	    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
	    			float s=j*sizew;
	    			float t=i*sizeh;
	    			
	    			result[c++]=s;
	    			result[c++]=t;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;    			
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t+sizeh;    			
	    		}
	    	}
	    	return result;
	    }
	}
	//下方的底座
	private class TextureRect
	{
		//自定义渲染管线着色器程序的引用
		int mProgram;
		//总变换矩阵的引用id
		int muMVPMatrixHandle;
		//顶点属性的引用id
		int maPositionHandle;
		//顶点纹理坐标的引用id
		int maTexCoorHandle;
		
		//顶点坐标数据缓冲
		FloatBuffer mVertexBuffer;
		//顶点纹理坐标数据缓冲
		FloatBuffer mTexCoorBuffer;
		int vCount=0;//顶点数量
		
		//R为圆柱底部的半径，r为圆柱上部的半径
		public TextureRect(int programId,float width,float height)
		{
			initVertexData(width,height);
			initShader(programId);
		}
		//初始化坐标数据的方法
		public void initVertexData(float width,float height)
		{
			float[] vertex=new float[]
            {
				-width,height,0,
				-width,-height,0,
				width,-height,0,
				
				-width,height,0,
				width,-height,0,
				width,height,0,
				
				-width,height,0,
				width,-height,0,
				-width,-height,0,
				
				-width,height,0,
				width,height,0,
				width,-height,0,
            };
			vCount=12;
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
            {
 				0,0,  0,1,  1,1,
 				0,0,  1,1,  1,0,
 				
 				0,0,  1,1,  0,1,
 				0,0,  1,0,  1,1
            };
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的方法
		public void initShader(int programId)
		{
			mProgram=programId;
			//获得顶点坐标数据的引用
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//顶点纹理坐标的引用id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//自定义的绘制方法
		public void drawSelf(int texId)
		{
			//使用某套指定的Shader程序
			GLES20.glUseProgram(mProgram);
			//将最终变换矩阵传入到Shader程序中
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//传入坐标数据
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