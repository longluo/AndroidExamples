package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

//城堡
public class Castle extends BNDrawer
{
	//城堡上方的物体
	Castle_Up castle_Up;
	//下侧的圆柱
	Cylinder cylinder;
	Cylinder cylinder0;
	Cylinder cylinder1;
	Cylinder cylinder2;
	
	//切分的角度
	final float ANGLE_SPAN=30;
	final float UNIT_SIZE=3.75f;
	final float r=UNIT_SIZE*1.75f;
	final float R=UNIT_SIZE*2.2f;
	final float mR=UNIT_SIZE*2f;
	//自下至上各个高度值
	final float HEIGHT0=UNIT_SIZE*1.7f;
	final float HEIGHT1=UNIT_SIZE*0.18f;
	final float HEIGHT2=UNIT_SIZE*0.18f;
	
	public Castle(int programId)
	{
		cylinder=new Cylinder(programId,mR,mR,ANGLE_SPAN,HEIGHT2);
		//圆柱1
		cylinder0=new Cylinder(programId,r,r,ANGLE_SPAN,HEIGHT0);
		//圆柱2
		cylinder1=new Cylinder(programId,r,R,ANGLE_SPAN,HEIGHT1);
		//圆柱3
		cylinder2=new Cylinder(programId,R,R,ANGLE_SPAN,HEIGHT2);
		//城堡上方的物体
		castle_Up=new Castle_Up(programId,R,ANGLE_SPAN,HEIGHT2);
	}
	//总的绘制方法drawSelf
	public void drawSelf(int[] texId, int dyFlag)
	{
		MatrixState.pushMatrix();
		cylinder.drawSelf(texId[1]);
		MatrixState.popMatrix();
		//1
		MatrixState.pushMatrix();
		MatrixState.translate(0, HEIGHT2+HEIGHT0, 0);
		cylinder0.drawSelf(texId[0]);
		MatrixState.popMatrix();
		//2 
		MatrixState.pushMatrix();
		MatrixState.translate(0, HEIGHT2+HEIGHT0*2+HEIGHT1, 0);
		cylinder1.drawSelf(texId[1]);
		MatrixState.popMatrix();
		//3
		MatrixState.pushMatrix();
		MatrixState.translate(0, HEIGHT2+HEIGHT0*2+HEIGHT1*2+HEIGHT2, 0);
		cylinder2.drawSelf(texId[1]);
		MatrixState.popMatrix();
		//4
		MatrixState.pushMatrix();
		MatrixState.translate(0, HEIGHT2+HEIGHT0*2+HEIGHT1*2+HEIGHT2*2, 0);
		castle_Up.drawSelf(texId[1]);
		MatrixState.popMatrix();
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
		public Cylinder(int programId,float R,float r,float angle_span,float height)
		{
			initVertexData(R,r,angle_span,height);
			initShader(programId);
		}
		//初始化坐标数据的initVertexData方法
		public void initVertexData(float R,float r,float angle_span,float height)
		{
			List<Float> tempList=new ArrayList<Float>();
			for(float vAngle=0;vAngle<360;vAngle=vAngle+angle_span)
			{
				float x0=(float) (r*Math.cos(Math.toRadians(vAngle)));
				float y0=height; 
				float z0=(float) (-r*Math.sin(Math.toRadians(vAngle)));
				
				float x1=(float) (R*Math.cos(Math.toRadians(vAngle))); 
				float y1=-height;
				float z1=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x2=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y2=-height;
				float z2=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				float x3=(float) (r*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y3=height;
				float z3=(float) (-r*Math.sin(Math.toRadians(vAngle+angle_span)));
				
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
	//该类表示城堡上侧的部分
	private class Castle_Up
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
		public Castle_Up(int programId,float R,float angle_span,float height)
		{
			initVertexData(R,angle_span,height);
			initShader(programId);
		}
		//初始化坐标数据的initVertexData方法
		public void initVertexData(float R,float angle_span,float height)
		{
			List<Float> vertexList=new ArrayList<Float>();
			for(float vAngle=0;vAngle<360;vAngle=vAngle+angle_span)
			{
				
				float tempX0=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float tempZ0=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float tempX1=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float tempZ1=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				//两点之间的间距平分3份
				float tempX=(tempX1-tempX0)/3;
				float tempZ=(tempZ1-tempZ0)/3;
				
				float x0=tempX0;
				float y0=height;
				float z0=tempZ0;
				
				float x1=tempX0; 
				float y1=-height;
				float z1=tempZ0;
				
				float x2=tempX0+tempX;
				float y2=height;
				float z2=tempZ0+tempZ;
				
				float x3=tempX0+tempX;
				float y3=-height;
				float z3=tempZ0+tempZ;
				
				float x4=tempX0+tempX*2;
				float y4=height;
				float z4=tempZ0+tempZ*2;
				
				float x5=tempX0+tempX*2;
				float y5=-height;
				float z5=tempZ0+tempZ*2;
				
				float x6=tempX1;
				float y6=height;
				float z6=tempZ1;
				
				float x7=tempX1;
				float y7=-height;
				float z7=tempZ1;
				//第一个三角形
				vertexList.add(x0); vertexList.add(y0); vertexList.add(z0);
				vertexList.add(x1); vertexList.add(y1); vertexList.add(z1);
				vertexList.add(x3); vertexList.add(y3); vertexList.add(z3);
				//第二个三角形
				vertexList.add(x0); vertexList.add(y0); vertexList.add(z0);
				vertexList.add(x3); vertexList.add(y3); vertexList.add(z3);
				vertexList.add(x2); vertexList.add(y2); vertexList.add(z2);
				//第三个三角形
				vertexList.add(x4); vertexList.add(y4); vertexList.add(z4);
				vertexList.add(x5); vertexList.add(y5); vertexList.add(z5);
				vertexList.add(x7); vertexList.add(y7); vertexList.add(z7);
				//第四个三角形
				vertexList.add(x4); vertexList.add(y4); vertexList.add(z4);
				vertexList.add(x7); vertexList.add(y7); vertexList.add(z7);
				vertexList.add(x6); vertexList.add(y6); vertexList.add(z6);
			}
			vCount=vertexList.size()/3;
			System.out.println("vCount="+vCount);
			float[] vertex=new float[vertexList.size()];
			for(int i=0;i<vertexList.size();i++)
			{
				vertex[i]=vertexList.get(i);
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
			//为传入坐标数据
			GLES20.glVertexAttribPointer
			(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT, 
				false, 
				3*4, 
				mVertexBuffer
			);
			//为传入纹理坐标数据
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
	    	float[] result=new float[bw*bh*12*2]; 
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
	    			//第一个三角形
	    			result[c++]=s;
	    			result[c++]=t;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew/3;
	    			result[c++]=t+sizeh;
	    			//第二个三角形
	    			result[c++]=s;
	    			result[c++]=t;
	    			
	    			result[c++]=s+sizew/3;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew/3;
	    			result[c++]=t;
	    			//第三个三角形
	    			result[c++]=s+sizew*2/3;
	    			result[c++]=t;
	    			
	    			result[c++]=s+sizew*2/3;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t+sizeh;
	    			//第四个三角形
	    			result[c++]=s+sizew*2/3;
	    			result[c++]=t;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;
	    		}
	    	}
	    	return result;
	    }
	}
}