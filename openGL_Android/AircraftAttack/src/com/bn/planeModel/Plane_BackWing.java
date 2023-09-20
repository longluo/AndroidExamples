package com.bn.planeModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
//纹理矩形,用于绘制后机翼,只有顶点和纹理
public class Plane_BackWing 
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
    
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
    int vCount ;
    float mAngleX ;
    
    public Plane_BackWing(float width,float height,float length,int mProgram)
    {
    	this.mProgram=mProgram;
    	initVertexData(width,height,length);
    	initShader();
    }
    public void initVertexData(float width,float height,float length)
    {
        float vertices[]=new float[]
        {
    		//上表面
    		0,height,length,-width,height,length,-13.0f/10.0f*width,2.0f/5.0f*height,length,//OAB        		
    		
    		0,height,length,-13.0f/10.0f*width,2.0f/5.0f*height,length,-width,-height,length,//OBC
    		
    		0,height,length,-width,-height,length,width,-height,length,//OCD
    		
    		0,height,length,width,-height,length,13.0f/10.0f*width,2.0f/5.0f*height,length,//ODE

    		0,height,length,13.0f/10.0f*width,2.0f/5.0f*height,length,width,height,length,//OEF
    		
    		//下表面
    		0,height,-length,-width,height,-length,-13.0f/10.0f*width,2.0f/5.0f*height,-length,//OAB        		
    		
    		0,height,-length,-13.0f/10.0f*width,2.0f/5.0f*height,-length,-width,-height,-length,//OBC
    		
    		0,height,-length,-width,-height,-length,width,-height,-length,//OCD
    		
    		0,height,-length,width,-height,-length,13.0f/10.0f*width,2.0f/5.0f*height,-length,//ODE

    		0,height,-length,13.0f/10.0f*width,2.0f/5.0f*height,-length,width,height,-length,//OEF
 
    		-width,height,length,-width,height,-length,-13.0f/10.0f*width,2.0f/5.0f*height,-length,
    		0-width,height,length,-13.0f/10.0f*width,2.0f/5.0f*height,-length,-13.0f/10.0f*width,2.0f/5.0f*height,length,
    		-13.0f/10.0f*width,2.0f/5.0f*height,length,-13.0f/10.0f*width,2.0f/5.0f*height,-length,-width,-height,-length,
    		-13.0f/10.0f*width,2.0f/5.0f*height,length,-width,-height,-length,-width,-height,length,
    		-width,-height,length,-width,-height,-length,width,-height,-length,
    		-width,-height,length,width,-height,-length,width,-height,length,
    		width,-height,length,width,-height,-length,13.0f/10.0f*width,2.0f/5.0f*height,-length,
    		width,-height,length,13.0f/10.0f*width,2.0f/5.0f*height,-length,13.0f/10.0f*width,2.0f/5.0f*height,length,
    		13.0f/10.0f*width,2.0f/5.0f*height,length,13.0f/10.0f*width,2.0f/5.0f*height,-length,width,height,-length,
    		13.0f/10.0f*width,2.0f/5.0f*height,length,width,height,-length,width,height,length,
    		width,height,length,width,height,-length,-width,height,-length,
    		width,height,length,width,height,-length,0,height,-length,
    		width,height,length,0,height,-length,0,height,length,
    		0,height,length,0,height,-length,-width,height,-length,
    		0,height,length, -width,height,-length,	-width,height,length,	
        };
        
        vCount=vertices.length/3;
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点纹理数据的初始化================begin============================
        float textures[]=new float[]
        {
        	
        	//上表面
        		0.18f,0.0f,0.027f,0.0080f,0.0f,0.027f,
        		0.18f,0.0f,0.0f,0.027f,0.0f,0.09f,
        		0.18f,0.0f,0.0f,0.09f,0.035f,0.109f,
        		0.18f,0.0f,0.035f,0.109f,0.145f,0.109f,
        		0.18f,0.0f,0.145f,0.109f,0.168f,0.074f,
        		0.18f,0.0f,0.168f,0.074f,0.211f,0.07f,
        		
        	//下表面
        		0.18f,0.0f,0.027f,0.0080f,0.0f,0.027f,
        		0.18f,0.0f,0.0f,0.027f,0.0f,0.09f,
        		0.18f,0.0f,0.0f,0.09f,0.035f,0.109f,
        		0.18f,0.0f,0.035f,0.109f,0.145f,0.109f,
        		0.18f,0.0f,0.145f,0.109f,0.168f,0.074f,
        		0.18f,0.0f,0.168f,0.074f,0.211f,0.07f,
        	//侧面
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,   		
        		0.168f,0.043f,0.152f,0.09f,0.223f,0.074f,0.168f,0.043f,0.152f,0.09f,0.223f,0.074f, 
        };

        
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================
    }
    //初始化着色器的initShader方法
    public void initShader()
    {
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
    
    public void drawSelf(int texId)
    {     
    	MatrixState.pushMatrix();
    	MatrixState.rotate(mAngleX, 1, 0, 0);
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
               mTextureBuffer
        );   
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
        MatrixState.popMatrix();
    }
}
