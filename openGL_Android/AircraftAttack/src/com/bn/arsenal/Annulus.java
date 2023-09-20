package com.bn.arsenal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.bn.core.MatrixState;

public class Annulus {//颜色圆环
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    
    private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;//顶点数量
	public Annulus(int  mProgram,float R,float r,int angle){
			this.mProgram=mProgram;
			//初始化着色器的initShader方法        
			initShader();
	    	vCount=360/angle*6;
	    	
	        float vertices[]=new float[vCount*3];
	        int j=0;
	        for(int i=0;i<360/angle;i++){
	        	vertices[j++]=(float) (R*Math.cos(Math.toRadians(i*angle)));
	        	vertices[j++]=0;
	        	vertices[j++]=(float) (R*Math.sin(Math.toRadians(i*angle)));
	        	
	        	vertices[j++]=(float) (r*Math.cos(Math.toRadians(i*angle)));
	        	vertices[j++]=0;
	        	vertices[j++]=(float) (r*Math.sin(Math.toRadians(i*angle)));
	        	
	        	vertices[j++]=(float) (r*Math.cos(Math.toRadians((1+i)*angle)));
	        	vertices[j++]=0;
	        	vertices[j++]=(float) (r*Math.sin(Math.toRadians((1+i)*angle)));
	        	
	        	vertices[j++]=(float) (R*Math.cos(Math.toRadians(i*angle)));
	        	vertices[j++]=0;
	        	vertices[j++]=(float) (R*Math.sin(Math.toRadians(i*angle)));
	        	
	        	
	        	vertices[j++]=(float) (r*Math.cos(Math.toRadians((1+i)*angle)));
	        	vertices[j++]=0;
	        	vertices[j++]=(float) (r*Math.sin(Math.toRadians((1+i)*angle)));
	        	
	        	vertices[j++]=(float) (R*Math.cos(Math.toRadians((1+i)*angle)));
	        	vertices[j++]=0;
	        	vertices[j++]=(float) (R*Math.sin(Math.toRadians((1+i)*angle)));
	        	
	        	
	        }
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	     
	        float texCoor[]=new float[vCount*2];//顶点颜色值数组，每个顶点4个色彩值RGBA
	        j=0;
	        float texAngle=1.0f/(360.0f/angle);
	        for(int i=0;i<360/angle;i++){
	        	texCoor[j++]=i*texAngle;
	        	texCoor[j++]=0;
	        	
	        	texCoor[j++]=i*texAngle;
	        	texCoor[j++]=1;
	        	
	        	texCoor[j++]=(i+1)*texAngle;
	        	texCoor[j++]=1;
	        	
	        	texCoor[j++]=i*texAngle;
	        	texCoor[j++]=0;
	        	
	        	
	        	texCoor[j++]=(i+1)*texAngle;
	        	texCoor[j++]=1;
	        	
	        	texCoor[j++]=(i+1)*texAngle;
	        	texCoor[j++]=0;
	        }
	        
	       //创建顶点纹理坐标数据缓冲
	       ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
	       cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
	       mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
	       mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
	       mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
	 public void initShader()
	    {
	        //获取程序中顶点位置属性引用id  
	        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
	        //获取程序中总变换矩阵引用id
	        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
	        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
	    }
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
	         //传入顶点位置数据数组
	         GLES20.glEnableVertexAttribArray(maPositionHandle);  
	         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
	         //绑定纹理
	         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
	         //绘制纹理矩形
	         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	    }
}
