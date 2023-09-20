package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static com.bn.clp.Constant.*;
import android.opengl.GLES20;
import com.bn.core.MatrixState;
//绘制时间  计圈 氮气 暂停 换人称视角 加速按钮的类
public class DrawTime
{
	//数字矩形块的宽度和高度
	float SHUZI_KUANDU=0.1f;
	float SHUZI_GAODU=0.12f;
	
	//记录总时间的数组 
	public static long timeTotal[]=new long[3];
	
	//数字的绘制矩形
	WenLiJuXing[] shuzi=new WenLiJuXing[10];
	//冒号的绘制矩形
	WenLiJuXing maohao; 
	//“time”的绘制矩形
	WenLiJuXing timeText;
	//“lap”的绘制矩形
	WenLiJuXing lapText;
	//斜杠的绘制矩形
	WenLiJuXing xiegan;
	//"氮气"图片的绘制矩形
	WenLiJuXing n2;
	//乘号的绘制矩形
	WenLiJuXing chenhao;
	
	//使用氮气的绘制矩形
	WenLiJuXing kejiasu;
	//不可加速的氮气的绘制矩形
	WenLiJuXing bukejiasu;
	
	//第一和第三人称按钮
//	WenLiJuXing firstView;
//	WenLiJuXing thirdView;
	
	//暂停、恢复按钮
	WenLiJuXing pauseButton;
	WenLiJuXing resumeButton;
	
	//迷你地图功能按钮
	WenLiJuXing miniMapButton;
	
	WenLiJuXing shache;
	WenLiJuXing noshache;
	
	public DrawTime(int mProgram)
	{
		for(int i=0;i<10;i++)
		{
			shuzi[i]=new WenLiJuXing
            (
            	SHUZI_KUANDU,
            	SHUZI_GAODU,
            	 new float[]
	             {
	           	  0.1f*i,0, 0.1f*i,0.26f, 0.1f*(i+1),0.26f,
	           	  0.1f*i,0, 0.1f*(i+1),0.26f,  0.1f*(i+1),0
	             }
             ); 
		}
		
		maohao=new WenLiJuXing
		(
			SHUZI_KUANDU,
        	SHUZI_GAODU,
        	 new float[]
             {
           	  0.725f,0.46f, 0.725f,0.71f, 0.8f,0.71f,
           	  0.725f,0.46f, 0.8f,0.71f,  0.8f,0.46f
             }
		);
		
		timeText=new WenLiJuXing
		(
				SHUZI_KUANDU*4,
	        	SHUZI_GAODU,
	        	 new float[]
	             {
	           	  0.025f,0.48f, 0.025f,0.7f, 0.31f,0.7f,
	           	  0.025f,0.48f, 0.31f,0.7f, 0.31f,0.48f
	             }
		);
		
		lapText=new WenLiJuXing
		(
				SHUZI_KUANDU*3,
	        	SHUZI_GAODU,
	        	 new float[]
	             {
	           	  0.33f,0.48f, 0.33f,0.7f, 0.625f,0.7f,
	           	  0.33f,0.48f, 0.625f,0.7f, 0.625f,0.48f
	             }
		);
		
		xiegan=new WenLiJuXing
		(
				SHUZI_KUANDU,
	        	SHUZI_GAODU,
	        	 new float[]
	             {
	           	  0.63f,0.445f, 0.63f,0.71f, 0.71f,0.71f,
	           	  0.63f,0.445f, 0.71f,0.71f, 0.71f,0.445f
	             }
		);
		
		n2=new WenLiJuXing
		(
				SHUZI_KUANDU*2,
	        	SHUZI_GAODU*2,
	        	 new float[]
	             {
	           	  0.81f,0.3f, 0.81f,0.72f, 0.95f,0.72f,
	           	  0.81f,0.3f, 0.95f,0.72f, 0.95f,0.3f
	             }
		);
		
		chenhao=new WenLiJuXing
		(
				SHUZI_KUANDU,
	        	SHUZI_GAODU,
	        	 new float[]
	             {
	           	  0.02f,0.795f, 0.02f,0.945f, 0.095f,0.945f,
	           	  0.02f,0.795f, 0.095f,0.945f, 0.095f,0.795f
	             }
		);
		
		noshache=new WenLiJuXing
		(
				Self_Adapter_Data_TRASLATE[screenId][17],
	        	SHUZI_GAODU*1.5f,  
	        	 new float[]
	             {
	           	  0,0f, 0,1f, 0.5f,1f,
	           	  0f,0f, 0.5f,1f, 0.5f,0f
	             }
		);
		
		shache=new WenLiJuXing
		(
				Self_Adapter_Data_TRASLATE[screenId][17],
	        	SHUZI_GAODU*1.5f,
	        	 new float[]
	             {
					0.5f,0f, 0.5f,1f, 1f,1f,
			        0.5f,0f, 1f,1f, 1f,0f 
	             }
		);
		
		kejiasu=new WenLiJuXing
		(
				Self_Adapter_Data_TRASLATE[screenId][8],
	        	SHUZI_GAODU*5,
	        	 new float[]
	             {
	           	  0,0f, 0,1f, 0.5f,1f,
	           	  0f,0f, 0.5f,1f, 0.5f,0f
	             }
		);
		
		bukejiasu=new WenLiJuXing
		(
				Self_Adapter_Data_TRASLATE[screenId][8],
	        	SHUZI_GAODU*5,
	        	 new float[]
	             {
	           	  0.5f,0f, 0.5f,1f, 1f,1f,
	           	  0.5f,0f, 1f,1f, 1f,0f
	             }
		);
		
//		firstView=new WenLiJuXing
//		(
//				Self_Adapter_Data_TRASLATE[screenId][2],
//	        	SHUZI_GAODU*2,
//	        	 new float[]
//	             {
//	           	  0.15f,0.73f, 0.15f,1f, 0.29f,1f,
//	           	  0.15f,0.73f, 0.29f,1f, 0.29f,0.73f
//	             }
//		);
//		
//		thirdView=new WenLiJuXing
//		(
//				Self_Adapter_Data_TRASLATE[screenId][2],
//	        	SHUZI_GAODU*2,
//	        	 new float[]
//	             {
//	           	  0.3f,0.73f, 0.3f,1f, 0.44f,1f,
//	           	  0.3f,0.73f, 0.44f,1f, 0.44f,0.73f
//	             }
//		);
		
		pauseButton=new WenLiJuXing
		(
				Self_Adapter_Data_TRASLATE[screenId][5],
	        	SHUZI_GAODU*2,
	        	 new float[]
	             {
					0.47f,0.73f, 0.47f,1f, 0.61f,1f,
					0.47f,0.73f, 0.61f,1f, 0.61f,0.73f
	             }
		);
		
		resumeButton=new WenLiJuXing
		(
				Self_Adapter_Data_TRASLATE[screenId][5],
	        	SHUZI_GAODU*2,
	        	 new float[]
	             {
					0.63f,0.73f, 0.63f,1f, 0.77f,1f, 
					0.63f,0.73f, 0.77f,1f, 0.77f,0.73f
	             }
		);
		
		miniMapButton=new WenLiJuXing
		(
				SHUZI_GAODU*2,
	        	SHUZI_GAODU*2,
	        	 new float[]
	             {
					0.75f,0.75f, 0.75f,1f, 0.95f,1f,
					0.75f,0.75f, 0.95f,1f, 0.95f,0.75f
	             }
		);
		
		//初始化shader
		initShader(mProgram);
	}
	
	 public void initShader(int mProgram)  
	 {
    	for(WenLiJuXing fl:shuzi)
    	{
    		fl.initShader(mProgram);
    	}
    	maohao.initShader(mProgram);
    	timeText.initShader(mProgram);
    	lapText.initShader(mProgram);
    	xiegan.initShader(mProgram);
    	n2.initShader(mProgram);
    	chenhao.initShader(mProgram);
    	kejiasu.initShader(mProgram);
    	bukejiasu.initShader(mProgram);
    	pauseButton.initShader(mProgram);
    	resumeButton.initShader(mProgram);
//    	firstView.initShader(mProgram);
//    	thirdView.initShader(mProgram);
    	miniMapButton.initShader(mProgram);
    	shache.initShader(mProgram);
    	noshache.initShader(mProgram);
	 }
	 //算总时间的方法
	 public void toTotalTime(long ms)
	 {
		timeTotal[0]=(long) Math.floor((ms%1000)/10);
		timeTotal[1]=(long) Math.floor((ms%60000)/1000);
		timeTotal[2]=(long) Math.floor((ms/60000));		 		
	 }
	 //绘制计时器和lap标志
	 public void drawSelf(int timeTexId,int currLap,int numberOfN2,int goTexId,int shacheTexId,boolean isShaChe)
	 {
		 MatrixState.pushMatrix();
		 MatrixState.translate(-SHUZI_KUANDU*8+0.025f, SHUZI_GAODU+0.02f, 0);
		 MatrixState.rotate(90, 1, 0, 0);
		 lapText.drawSelf(timeTexId);//lap图标
		 MatrixState.popMatrix();
			
		 MatrixState.pushMatrix();
		 MatrixState.translate(-SHUZI_KUANDU*8, 0, 0);
		 MatrixState.rotate(90, 1, 0, 0);
		 xiegan.drawSelf(timeTexId);//lap中的斜杠
		 MatrixState.popMatrix();		 
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(-SHUZI_KUANDU*9, 0, 0);
		 lapDrawSelf(timeTexId,currLap);//lap中的数字
		 MatrixState.popMatrix();
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(SHUZI_KUANDU*4-0.05f, SHUZI_GAODU+0.02f, 0);
		 MatrixState.rotate(90, 1, 0, 0);
		 timeText.drawSelf(timeTexId);//time图标
		 MatrixState.popMatrix();
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(-0.055f, 0, 0);
		 timeDrawSelf(timeTexId,2);//绘制时间中的分
		 MatrixState.popMatrix();
					
		 MatrixState.pushMatrix();
		 MatrixState.translate(SHUZI_GAODU*3-0.055f, 0, 0);
		 timeDrawSelf(timeTexId,1);//绘制时间中的秒
		 MatrixState.popMatrix();
					
		 MatrixState.pushMatrix();
		 MatrixState.translate(SHUZI_GAODU*6-0.055f, 0, 0);
		 timeDrawSelf(timeTexId,0);//绘制时间中的毫秒
		 MatrixState.popMatrix();
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(SHUZI_KUANDU*13, SHUZI_GAODU-0.05f, 0);
		 MatrixState.rotate(90, 1, 0, 0);
		 n2.drawSelf(timeTexId);//氮气图标
		 MatrixState.popMatrix();
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(SHUZI_KUANDU*15-0.025f, SHUZI_GAODU-0.05f, 0);
		 MatrixState.rotate(90, 1, 0, 0);
		 chenhao.drawSelf(timeTexId);//乘号图标
		 MatrixState.popMatrix();
		 
		 MatrixState.pushMatrix();
		 MatrixState.translate(SHUZI_KUANDU*16, SHUZI_GAODU-0.05f, 0);
		 drawNumberOfN2(timeTexId,numberOfN2);//氮气数量
		 MatrixState.popMatrix();
		 //加速按钮
		 if(numberOfN2>0)
		 { 
			 MatrixState.pushMatrix();
			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][6], Self_Adapter_Data_TRASLATE[screenId][7], 0);
			 MatrixState.rotate(90, 1, 0, 0);
			 kejiasu.drawSelf(goTexId);
			 MatrixState.popMatrix();
		 }
		 else
		 {
			 MatrixState.pushMatrix();
			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][6], Self_Adapter_Data_TRASLATE[screenId][7], 0);
			 MatrixState.rotate(90, 1, 0, 0);
			 bukejiasu.drawSelf(goTexId);
			 MatrixState.popMatrix();
		 }
		 //刹车按钮
		 if(isShaChe)
		 {
			 MatrixState.pushMatrix();
			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][15], Self_Adapter_Data_TRASLATE[screenId][16], 0);
			 MatrixState.rotate(90, 1, 0, 0);
			 shache.drawSelf(shacheTexId);
			 MatrixState.popMatrix();
		 }
		 else
		 {
			 MatrixState.pushMatrix();
			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][15], Self_Adapter_Data_TRASLATE[screenId][16], 0);
			 MatrixState.rotate(90, 1, 0, 0);
			 noshache.drawSelf(shacheTexId);
			 MatrixState.popMatrix();
		 }
//		 //换视角按钮
//		 if(isFirstPersonView)
//		 {
//			 MatrixState.pushMatrix();
//			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][0], Self_Adapter_Data_TRASLATE[screenId][1], 0);
//			 MatrixState.rotate(90, 1, 0, 0);
//			 firstView.drawSelf(timeTexId);
//			 MatrixState.popMatrix();
//		 }
//		 else
//		 {
//			 MatrixState.pushMatrix();
//			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][0], Self_Adapter_Data_TRASLATE[screenId][1], 0);
//			 MatrixState.rotate(90, 1, 0, 0);
//			 thirdView.drawSelf(timeTexId);
//			 MatrixState.popMatrix();
//		 }
		 //暂停按钮
		 if(!isPaused)
		 {
			 MatrixState.pushMatrix();
			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][3], Self_Adapter_Data_TRASLATE[screenId][4], 0);
			 MatrixState.rotate(90, 1, 0, 0);
			 pauseButton.drawSelf(timeTexId);
			 MatrixState.popMatrix();
		 }
		 else
		 {
			 MatrixState.pushMatrix();
			 MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][3], Self_Adapter_Data_TRASLATE[screenId][4], 0);
			 MatrixState.rotate(90, 1, 0, 0);
			 resumeButton.drawSelf(timeTexId);
			 MatrixState.popMatrix();
		 }
	 }
	 
	 //时间绘制者
	 public void timeDrawSelf(int texId,int number)
	 {
		String scoreStr;
		if(timeTotal[number]<10)
		{
			scoreStr="0"+timeTotal[number]+"";
		}
		else
		{
			scoreStr=timeTotal[number]+"";
		}
		
		for(int i=0;i<scoreStr.length();i++)
		{
			char c=scoreStr.charAt(i);
			
			 MatrixState.pushMatrix();
	         MatrixState.translate(i*SHUZI_KUANDU, 0, 0);
	         MatrixState.rotate(90, 1, 0, 0);
	         shuzi[c-'0'].drawSelf(texId);		         
	         MatrixState.popMatrix();
		}
		if(number!=0)
		{
			MatrixState.pushMatrix();
	        MatrixState.translate(2*SHUZI_KUANDU+0.02f, 0, 0);
	        MatrixState.rotate(90, 1, 0, 0);
	        maohao.drawSelf(texId);		         
	        MatrixState.popMatrix();
		}		
	 }
	 //圈数绘制者
	 public void lapDrawSelf(int texId,int currLap)
	 {
		String curr=currLap+"";
		String total=maxOfTurns+"";
		
		char c=curr.charAt(0);
		MatrixState.pushMatrix();
        MatrixState.rotate(90, 1, 0, 0);
        shuzi[c-'0'].drawSelf(texId);
        MatrixState.popMatrix();
        
        c=total.charAt(0);
		MatrixState.pushMatrix();
		MatrixState.translate(SHUZI_KUANDU*2, 0, 0);
        MatrixState.rotate(90, 1, 0, 0);
        shuzi[c-'0'].drawSelf(texId);
        MatrixState.popMatrix();
	 }	 
	 //氮气的数量绘制者
	 public void drawNumberOfN2(int texId,int number)
	 {
		 String numberStr=number+"";
		  
		 char c=numberStr.charAt(0);
		 MatrixState.pushMatrix();
		 MatrixState.rotate(90, 1, 0, 0);
		 shuzi[c-'0'].drawSelf(texId);
		 MatrixState.popMatrix();
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
	    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
	    int vCount;//顶点数量
	    int texId;//纹理Id
			
	    public WenLiJuXing(float width,float height,float[] textures){//传入宽高和纹理坐标数组
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
	        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	        
	        //创建顶点纹理数据缓冲
	        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
	        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
	        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
	        mTextureBuffer.position(0);//设置缓冲区起始位置

	        //加载顶点着色器的脚本内容
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
}