package com.bn.tl;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.content.res.Resources;
import android.opengl.GLES20;
//篮板,立方体
public class LanBan  
{
	int mProgram;//自定义渲染管线着色器程序id 
    int muMVPMatrixHandle;//总变换矩阵引用   
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用  
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    int maSunLightLocationHandle;//光源位置属性引用  
    
    public FloatBuffer mVertexBuffer;
	public FloatBuffer mTexCoorBuffer;
    public FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
	int vCount; 
	public LanBan(float length,float width,float height,Resources r){
		float l=length/2;
		float w=width/2;
		float h=height/2;
		
		
		vCount=36;
		float[] vertexs=new float[]
		{	
			0-w,0-h,length-l,	
			0-w,0-h,0-l,
			0-w,height-h,0-l,
			
			0-w,0-h,length-l,
			0-w,height-h,0-l,
			0-w,height-h,length-l,
			
			width-w,0-h,0-l,
			0-w,0-h,0-l,
			0-w,height-h,0-l,
			
			width-w,0-h,0-l,
			0-w,height-h,0-l,
			width-w,height-h,0-l,
			
			width-w,height-h,length-l,
			width-w,height-h,0-l,
			width-w,0-h,0-l,
			
			width-w,height-h,length-l,
			width-w,0-h,0-l,
			width-w,0-h,length-l,
			
			width-w,height-h,0-l,
			0-w,height-h,0-l,
			0-w,height-h,length-l,
			
			width-w,height-h,0-l,
			0-w,height-h,length-l,
			width-w,height-h,length-l,
			
			width-w,height-h,length-l,
			0-w,height-h,length-l,
			0-w,0-h,length-l,
			
			width-w,height-h,length-l,
			0-w,0-h,length-l,
			width-w,0-h,length-l,
			
			width-w,0-h,length-l,
			0-w,0-h,length-l,
			0-w,0-h,0-l,
			
			width-w,0-h,length-l,
			0-w,0-h,0-l,
			width-w,0-h,0-l,
		};
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexs.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertexs);
		mVertexBuffer.position(0);
		float[] textures=new float[] 
		{						
	            1,1,1,0,0,0,
	            1,1,0,0,0,1,
	            0,1,1,1,1,0,
	            0,1,1,0,0,0,
	            1,1,1,0,0,0,
	            1,1,0,0,0,1,
	            1,0,0,0,0,1,
	            1,0,0,1,1,1,
	            
	            1,0,0,0,0,1,
	            1,0,0,1,1,1,
	            
	            0,1,1,1,1,0,
	            0,1,1,0,0,0
	           
		};  
		ByteBuffer tbb=ByteBuffer.allocateDirect(textures.length*4);
		tbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=tbb.asFloatBuffer();
		mTexCoorBuffer.put(textures);
		mTexCoorBuffer.position(0);
		
		 float norma[]={//顶点法向量数组
	        		0,1,0, 0,1,0, 0,1,0,
	        		0,1,0, 0,1,0, 0,1,0,
	        };
	        ByteBuffer tnom=ByteBuffer.allocateDirect(norma.length*4);
	        tnom.order(ByteOrder.nativeOrder());//设置字节顺序
	        mNormalBuffer=tnom.asFloatBuffer();//转换为Float型缓冲
	        mNormalBuffer.put(norma);//向缓冲区添加顶点法向量数据
	        mNormalBuffer.position(0);//设置缓冲区的起始位置
	}
	 //初始化着色器数据的initShader方法
	 public void initShader(int mProgram)
	 {
	    this.mProgram=mProgram; 
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
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
