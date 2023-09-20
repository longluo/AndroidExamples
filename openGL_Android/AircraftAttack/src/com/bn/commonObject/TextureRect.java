package com.bn.commonObject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
//纹理矩形     其中该矩形是平行于XY平面的,关于原点中心对称
//这个也是用于绘制水面
//这个用于绘制按钮
//矩形的宽度和高度分别为 width,height
public class TextureRect 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    int uIsButtonDownHandle;//按钮是否按下引用Id
    int uTypeHandle;//按钮的属性
    int uCurrAlphaHandle;//当前按钮的不透明度
	int uWidthHandle;//当前按钮的宽度
	int uCurrXHandle;//传入的X坐标
    
    
    int maSTOffset;	//水面纹理图的偏移量引用id
    int uBloodValueHandle;//生命值的引用
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;  
    private boolean isFlow;//当前是否是水面
    private boolean flag_flow_go=true;//水面是否运动
    private float currStartST=0;	//水面纹理坐标的当前起始坐标0~1
    //按钮标志位,用来改变按钮的不透明度
    private int index=0;//对象id,如果是1,表示当前为按钮,如果为2,表示绘制物体的生命值
    public  int button_type=0;//按钮的类型  0表示正常显示,1表示按下才透明的按钮,2表示循环变换的按钮,3表示卷动的面板
    public  float bloodValue;//物体的生命值
    public  int isButtonDown;//按钮是否按下0表示没有按下,1表示按下
    public float currAlpha;//当前的不透明度
    public float buttonWidth;//按钮的宽度
    public float currX;//传入的X值
    
    
    public float flowSpeed;//流动的速度
    public float[] textures;//定义纹理坐标
    //普通构造器
    public TextureRect(float width,float height,int mProgram)
    {    	
    	//初始化顶数据的initVertexDate方法
    	initVertexData(width,height,false,1);
    	//初始化着色器的initShader方法        
    	initShader(mProgram);
    }
    //按钮/生命值构造器
    public TextureRect(float width,float height,int mProgram,int index,int button_type)//1为按钮,2为生命值
    {   
    	this.index=index;
    	this.button_type=button_type;
    	//初始化顶点坐标与着色数据
    	initVertexData(width,height,false,1);
    	//初始化shader        
    	initShader(mProgram);
    }
    //水面构造器
    public TextureRect(float width,float height,int mProgram,boolean isWater,int n) 
    {
    	//初始化顶点坐标与着色数据
    	initVertexData(width,height,false,n);
    	//初始化shader        
    	initShader(mProgram);
    }
    //纹理流动的构造器
    public TextureRect(float width,float height,int mProgram,boolean isFlow,float speed)
    {
    	this.isFlow=isFlow;
    	this.flowSpeed=speed;
    	//初始化顶点坐标与着色数据
    	initVertexData(width,height,false,1);
    	//初始化shader        
    	initShader(mProgram);
    	//启动一个线程定时换帧
    	new Thread()
    	{
    		public void run()
    		{
    			while(flag_flow_go)
    			{
    				//所谓水面定时换帧只是修改每帧起始角度即可，
    				//水面顶点Y坐标的变化由顶点着色单元完成
    				currStartST=(currStartST+0.00008f*flowSpeed)%1;
        			try 
        			{
    					Thread.sleep(10);  
    				}
        			catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
    			} 
    		}    
    	}.start();  
    }
    //绘制数字的构造器,主要是将纹理坐标传入进来
    public TextureRect(float width,float height,float[] textures,int mProgram)//传入宽高和纹理坐标数组
    {
    	this.textures=textures;
    	initVertexData(width,height,true,1);
    	initShader(mProgram);
    }
    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float width,float height,boolean hasTexture,int n)
    {
        vCount=4;
        float vertices[]=new float[]
        {
        	-width/2,height/2,0,
        	-width/2,-height/2,0,
        	width/2,height/2,0,
        	width/2,-height/2,0 
        };
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        if(!hasTexture)//如果没有传入纹理
        {
        	textures=new float[]//顶点颜色值数组，每个顶点4个色彩值RGBA
	        {
	        	0,0, 0,n, 
	        	n,0, n,n        		
	        };        
        }
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
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
        if(isFlow)
        {
        	//获取水面纹理图偏移量的引用id
            maSTOffset=GLES20.glGetUniformLocation(mProgram, "stK");  
        }
        if(index==1)//当前为按钮
        {
        	uIsButtonDownHandle=GLES20.glGetUniformLocation(mProgram, "isButtonDown");//按钮是否按下
        	uTypeHandle=GLES20.glGetUniformLocation(mProgram, "type");//按钮的类型
        	uCurrAlphaHandle=GLES20.glGetUniformLocation(mProgram, "currAlpha");//当前按钮的不透明度
        	uWidthHandle=GLES20.glGetUniformLocation(mProgram, "width");//当前按钮的宽度
        	uCurrXHandle=GLES20.glGetUniformLocation(mProgram, "currX");//传入的X坐标
        }
        else if(index==2)//当前为物体的生命值
        {
        	uBloodValueHandle=GLES20.glGetUniformLocation(mProgram, "ublood");
        }
    }
    public void drawSelf(int texId)
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram);        
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         if(isFlow)
         {
        	//将水面纹理图的st偏移量传入shader程序
             GLES20.glUniform1f(maSTOffset, currStartST);
         }
         if(index==1)//如果当前为按钮
         {
        	 GLES20.glUniform1i(uIsButtonDownHandle, isButtonDown);//按钮是否按下
        	 GLES20.glUniform1i(uTypeHandle, button_type);//按钮的类型
        	 GLES20.glUniform1f(uCurrAlphaHandle, currAlpha);//按钮的不透明度
        	 GLES20.glUniform1f(uWidthHandle, buttonWidth);//按钮的宽度
        	 GLES20.glUniform1f(uCurrXHandle, currX);//传入的X值
         }
         else if(index==2)//如果当前为物体生命值矩形框
         {
        	 GLES20.glUniform1f(uBloodValueHandle, bloodValue);
         }
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
         GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vCount); 
    }
}

