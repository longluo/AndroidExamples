package com.bn.planeModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
/*
 * 纹理矩形,用于绘制机翼,只有顶点和纹理
 */
public class Plane_Wing 
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
    
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
    int vCount;
    int texId;
    float mAngleX ;
    
    public Plane_Wing(float width,float height,float length,int mProgram)
    {
    	this.mProgram=mProgram;
    	initVertexData(width,height,length);
    	initShader();
    }
    public void initVertexData(float width,float height,float length)
    {
        vCount=144;
        float vertices[]=new float[]
        {
        		
        		//上表面
        		0,height,length,-width,height,length,-17.0f/15.0f*width,3.0f/5.0f*height,length,//OAB        		
        		
        		0,height,length,-17.0f/15.0f*width,3.0f/5.0f*height,length,-17.0f/15.0f*width,-3.0f/5.0f*height,length,//OBC
        		
        		0,height,length,-17.0f/15.0f*width,-3.0f/5.0f*height,length,-width,-height,length,//OCD
        		
        		0,height,length,-width,-height,length,-1.0f/3.0f*width,-height,length,//ODE

        		0,height,length,-1.0f/3.0f*width,-height,length,-2.0f/15.0f*width,-2.0f/5.0f*height,length,//OEF
        		
        		0,height,length,-2.0f/15.0f*width,-2.0f/5.0f*height,length,2.0f/15.0f*width,-2.0f/5.0f*height,length,//OFG
        		
        		0,height,length,2.0f/15.0f*width,-2.0f/5.0f*height,length,1.0f/3.0f*width,-height,length,//OGH
        		
        		0,height,length,1.0f/3.0f*width,-height,length,width,-height,length,//OHI        		
        		
        		0,height,length,width,-height,length,17.0f/15.0f*width,-3.0f/5.0f*height,length,//OIJ
        		
        		0,height,length,17.0f/15.0f*width,-3.0f/5.0f*height,length,17.0f/15.0f*width,3.0f/5.0f*height,length,//OJK
        		
        		0,height,length,17.0f/15.0f*width,3.0f/5.0f*height,length,width,height,length,//OKL
        		
        		//下表面
        		0,height,-length,-width,height,-length,-17.0f/15.0f*width,3.0f/5.0f*height,-length,//OAB        		
        		
        		0,height,-length,-17.0f/15.0f*width,3.0f/5.0f*height,-length,-17.0f/15.0f*width,-3.0f/5.0f*height,-length,//OBC
        		
        		0,height,-length,-17.0f/15.0f*width,-3.0f/5.0f*height,-length,-width,-height,-length,//OCD
        		
        		0,height,-length,-width,-height,-length,-1.0f/3.0f*width,-height,-length,//ODE

        		0,height,-length,-1.0f/3.0f*width,-height,-length,-2.0f/15.0f*width,-2.0f/5.0f*height,-length,//OEF
        		
        		0,height,-length,-2.0f/15.0f*width,-2.0f/5.0f*height,-length,2.0f/15.0f*width,-2.0f/5.0f*height,-length,//OFG
        		
        		0,height,-length,2.0f/15.0f*width,-2.0f/5.0f*height,-length,1.0f/3.0f*width,-height,-length,//OGH
        		
        		0,height,-length,1.0f/3.0f*width,-height,-length,width,-height,-length,//OHI        		
        		
        		0,height,-length,width,-height,-length,17.0f/15.0f*width,-3.0f/5.0f*height,-length,//OIJ
        		
        		0,height,-length,17.0f/15.0f*width,-3.0f/5.0f*height,-length,17.0f/15.0f*width,3.0f/5.0f*height,-length,//OJK
        		
        		0,height,-length,17.0f/15.0f*width,3.0f/5.0f*height,-length,width,height,-length,//OKL
        		
        		//侧面
        		-width,height,length,-width,height,-length,-17.0f/15.0f*width,3.0f/5.0f*height,-length,//A//A1//B1        		
        		
        		-width,height,length,-17.0f/15.0f*width,3.0f/5.0f*height,-length,-17.0f/15.0f*width,3.0f/5.0f*height,length,//A//B1//B        		
        		
        		-17.0f/15.0f*width,3.0f/5.0f*height,length,-17.0f/15.0f*width,3.0f/5.0f*height,-length,-17.0f/15.0f*width,-3.0f/5.0f*height,-length,//B//B1//C1
        		
        		-17.0f/15.0f*width,3.0f/5.0f*height,length,-17.0f/15.0f*width,-3.0f/5.0f*height,-length,-17.0f/15.0f*width,-3.0f/5.0f*height,length,//B//C1//C
        		
        		-17.0f/15.0f*width,-3.0f/5.0f*height,length,-17.0f/15.0f*width,-3.0f/5.0f*height,-length,-width,-height,-length,//CC1D1
        		
        		-17.0f/15.0f*width,-3.0f/5.0f*height,length,-width,-height,-length,-width,-height,length,//C//D1//D
        		
        		-width,-height,length,-width,-height,-length,-1.0f/3.0f*width,-height,-length,//D//D1//E1        		
        		
        		-width,-height,length,-1.0f/3.0f*width,-height,-length,-1.0f/3.0f*width,-height,length,//D//E1//E\\		
        		
        		-1.0f/3.0f*width,-height,length,-1.0f/3.0f*width,-height,-length,-2.0f/15.0f*width,-2.0f/5.0f*height,-length,//E\\//E1//F1
        		
        		-1.0f/3.0f*width,-height,length,-2.0f/15.0f*width,-2.0f/5.0f*height,-length,-2.0f/15.0f*width,-2.0f/5.0f*height,length,//E\\//F1//F
        		
        		-2.0f/15.0f*width,-2.0f/5.0f*height,length,-2.0f/15.0f*width,-2.0f/5.0f*height,-length,2.0f/15.0f*width,-2.0f/5.0f*height,-length,//F//F1//G1
        		
        		-2.0f/15.0f*width,-2.0f/5.0f*height,length,2.0f/15.0f*width,-2.0f/5.0f*height,-length,2.0f/15.0f*width,-2.0f/5.0f*height,length,//F//G1//G
        		
        		2.0f/15.0f*width,-2.0f/5.0f*height,length,2.0f/15.0f*width,-2.0f/5.0f*height,-length,1.0f/3.0f*width,-height,-length,//G//G1//H1
        		
        		2.0f/15.0f*width,-2.0f/5.0f*height,length,1.0f/3.0f*width,-height,-length,1.0f/3.0f*width,-height,length,//G//H1//H

        		1.0f/3.0f*width,-height,length,1.0f/3.0f*width,-height,-length,width,-height,-length,//H//H1//I1        		
        		
        		1.0f/3.0f*width,-height,length,width,-height,-length,width,-height,length,//H//I1//I
        	
        		width,-height,length,width,-height,-length,17.0f/15.0f*width,-3.0f/5.0f*height,-length,//I//I1//J1	
        		
        		width,-height,length,17.0f/15.0f*width,-3.0f/5.0f*height,-length,17.0f/15.0f*width,-3.0f/5.0f*height,length,//I//J1//J

        		17.0f/15.0f*width,-3.0f/5.0f*height,length,17.0f/15.0f*width,-3.0f/5.0f*height,-length,17.0f/15.0f*width,3.0f/5.0f*height,-length,//J//J1//K1
	
        		17.0f/15.0f*width,-3.0f/5.0f*height,length,17.0f/15.0f*width,3.0f/5.0f*height,-length,17.0f/15.0f*width,3.0f/5.0f*height,length,//J//K1//K

        		17.0f/15.0f*width,3.0f/5.0f*height,length,17.0f/15.0f*width,3.0f/5.0f*height,-length,width,height,-length,//K//K1//L1
  		
        		17.0f/15.0f*width,3.0f/5.0f*height,length,width,height,-length,width,height,length,//K//L1//L
  
        		width,height,length,width,height,-length,0,height,-length,//L//L1//O1
      		
        		width,height,length,0,height,-length,0,height,length,//L//o1//O 
        		
        		0,height,length,0,height,-length,0,height,-length,//oo1a1
        		
        		0,height,length,0,height,-length,0,height,length,//oo1a
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //顶点纹理数据的初始化================begin============================
        float textures[]=new float[]
        {
        	//上表面															
        		0.488f,0.0040f,0.051f,0.0080f,0.0f,0.051f,
        		0.488f,0.0040f,0.0f,0.051f,0.0f,0.203f,
        		0.488f,0.0040f,0.0f,0.203f,0.055f,0.23f,
        		0.488f,0.0040f,0.055f,0.23f,0.344f,0.223f,
        		0.488f,0.0040f,0.344f,0.223f,0.391f,0.168f,
        		0.488f,0.0040f,0.391f,0.168f,0.582f,0.164f,
        		0.488f,0.0040f,0.582f,0.164f,0.664f,0.211f,
        		0.488f,0.0040f,0.664f,0.211f,0.938f,0.203f,
        		0.488f,0.0040f,0.938f,0.203f,0.98f,0.164f,
        		0.488f,0.0040f,0.98f,0.164f,0.984f,0.059f,
        		0.488f,0.0040f,0.984f,0.059f,0.914f,0.0040f,
        	//下表面
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        	//侧面
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,
        		0.0040f,0.277f,0.0040f,0.391f,0.137f,0.352f,  
        };
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
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
    	MatrixState.popMatrix();
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
    }
}
