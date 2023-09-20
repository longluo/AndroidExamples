package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static com.bn.clp.Constant.*;
import android.opengl.GLES20;
import com.bn.core.MatrixState;
import com.bn.st.d2.MyActivity;

import static com.bn.clp.MyGLSurfaceView.*;

public class DaoJiShiForDraw 
{
	//数字矩形块的宽度和高度
	float SHUZI_KUANDU=0.5f;
	 
	int DaoJiShiFlag=3;
	 
	float z_Order_Offset=-10;
	
	WenLiJuXing wljx;
	
	public static boolean DAOJISHI_FLAG=true;
	
	public DJSThread djst;
	
	MyActivity ma;
	
	MyGLSurfaceView mgsv;
	
	public DaoJiShiForDraw(int mProgram,MyActivity ma,MyGLSurfaceView mgsv)
	{
		wljx=new WenLiJuXing
		(
			SHUZI_KUANDU*5,
			SHUZI_KUANDU*5
		);
		
		this.ma=ma;
		
		this.mgsv=mgsv;
		
		djst=new DJSThread();
		
		wljx.initShader(mProgram);
	}
	
	public void drawSelf(int texId)
	{
		if(DaoJiShiFlag==3)
		{			
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, z_Order_Offset);
			MatrixState.rotate(90, 1, 0, 0);
			wljx.drawSelf(texId,0);
			MatrixState.popMatrix();
		}
		else if(DaoJiShiFlag==2)
		{
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, z_Order_Offset);
			MatrixState.rotate(90, 1, 0, 0);
			wljx.drawSelf(texId,1);
			MatrixState.popMatrix();
		}
		else if(DaoJiShiFlag==1)
		{
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, z_Order_Offset);
			MatrixState.rotate(90, 1, 0, 0);
			wljx.drawSelf(texId,2);
			MatrixState.popMatrix();
		}
		else if(DaoJiShiFlag==0)
		{
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, z_Order_Offset);
			MatrixState.rotate(90, 1, 0, 0);
			wljx.drawSelf(texId,3);
			MatrixState.popMatrix();
		}		 
	}	
	
	//纹理矩形内部类
	class WenLiJuXing
	{
		int mProgram;//自定义渲染管线着色器程序id 
	    int muMVPMatrixHandle;//总变换矩阵引用id   
	    int muMMatrixHandle;//位置、旋转变换矩阵
	    int maCameraHandle; //摄像机位置属性引用id  
	    int maPositionHandle; //顶点位置属性引用id  
	    int maNormalHandle; //顶点法向量属性引用id  
	    int maTexCoorHandle; //顶点纹理坐标属性引用id  
	    int maSunLightLocationHandle;//光源位置属性引用id  
		
	    private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	    private FloatBuffer   mTextureBuffer[];//顶点着色数据缓冲
	    int vCount;//顶点数量
	    int texId;//纹理Id
			
	    public WenLiJuXing(float width,float height){//传入宽高和纹理坐标数组
	    	//顶点坐标数据的初始化================begin============================
	        vCount=6;//每个格子两个三角形，每个三角形3个顶点        
	        float vertices[]=
	        {
        		-width/2,0,-height/2,
        		-width/2,0,height/2,
        		width/2,0,height/2,
        		
        		-width/2,0,-height/2,
        		width/2,0,height/2,
        		width/2,0,-height/2
	        };
	        //创建顶点坐标数据缓冲
	        //vertices.length*4是因为一个整数四个字节
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	        
	        float[][] texTures=new float[][]
           	{
       			{
       				0.41f,0, 0.41f,1, 0.615f,1,
       				0.41f,0, 0.615f,1, 0.615f,0 
       			},
       			{
       				0.2f,0, 0.2f,1, 0.415f,1,
       				0.2f,0, 0.415f,1, 0.415f,0
       			},
       			{
       				0,0, 0,1, 0.2f,1,
       				0,0, 0.2f,1, 0.2f,0
       			},
       			{
       				0.62f,0, 0.62f,1, 1,1,
       				0.62f,0, 1,1, 1,0
       			}
           	};
	        
	        mTextureBuffer=new FloatBuffer[4];
	        for(int i=0;i<texTures.length;i++)
	        {
	        	//创建顶点纹理数据缓冲
		        ByteBuffer tbb = ByteBuffer.allocateDirect(texTures[i].length*4);
		        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
		        mTextureBuffer[i]= tbb.asFloatBuffer();//转换为Float型缓冲
		        mTextureBuffer[i].put(texTures[i]);//向缓冲区中放入顶点着色数据
		        mTextureBuffer[i].position(0);//设置缓冲区起始位置
		        //加载顶点着色器的脚本内容
	        }
	    }
		//初始化着色器的initShader方法
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
		public void drawSelf(int texId,int number)
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
	               mTextureBuffer[number]
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
	
	class DJSThread extends Thread
	{
		
		public DJSThread()
		{
			this.setName("DJSThread");
		}
		
		public void run()
		{
			while(DAOJISHI_FLAG)
			{
				//当游戏开始时，倒计时3时播放一声
				if(DaoJiShiFlag==3&&z_Order_Offset==-10&&SoundEffectFlag)
				{
					ma.shengyinBoFang(5, 0);
				}
				z_Order_Offset=z_Order_Offset+0.3f;
				if(z_Order_Offset>-5)
				{
					z_Order_Offset=-10;
					DaoJiShiFlag=DaoJiShiFlag-1; 
					
					//当换图时，倒计时2、1各播放一声
					if(DaoJiShiFlag>0&&SoundEffectFlag)
					{
						ma.shengyinBoFang(5, 0);
					}//倒计时为0时，播放可以开船的声音
					else if(DaoJiShiFlag==0&&SoundEffectFlag)
					{
						ma.shengyinBoFang(6, 0);
					}
				}				
				
				if(DaoJiShiFlag<0)
				{
					DAOJISHI_FLAG=false;
					isJiShi=true; 
					isAllowToClick=true;
					DEGREE_SPAN=2.5f;
					BOAT_A=0.025f;
					MyGLSurfaceView.yachtLeftOrRightAngleA=yachtLeftOrRightAngleValue;
					gameStartTime=System.currentTimeMillis();
					mgsv.kt.start(); 
					mgsv.tc.start();
					mgsv.tfe.start();  
				} 
				try
				{   
					Thread.sleep(80);   
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
