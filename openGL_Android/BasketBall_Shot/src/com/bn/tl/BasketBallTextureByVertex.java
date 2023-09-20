package com.bn.tl;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES20;
import static com.bn.tl.Constant.*;
//用于绘制的球
public class BasketBallTextureByVertex 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵引用
    int muCameraMatrixHandle;//摄像机矩阵引用
    int muProjMatrixHandle;//投影矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用 
    int maNormalHandle; //顶点法向量属性引用 
    int maLightLocationHandle;//光源位置属性引用
    int maCameraHandle; //摄像机位置属性引用
    int muIsShadow;//是否绘制阴影属性引用  
    int muIsLanBanShdow;//是否为篮板上的阴影
    int muIsShadowFrag;//是否绘制阴影属性引用
    int muBallTexHandle;//桌球纹理属性引用
    int muTableTexHandle;//用于绘制阴影的桌面纹理属性引用 
    int muPlaneN;//平面法向量引用
    int muPlaneV;//平面上的一个点
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;   
    public BasketBallTextureByVertex(float scale)
    {    	
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(scale); 
    }
    //初始化顶点数据的initVertexData方法
    public void initVertexData(float scale)
    {
    	//顶点坐标数据的初始化
    	ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList    	
        for(float vAngle=90;vAngle>-90;vAngle=vAngle-QIU_SPAN)//垂直方向angleSpan度一份
        {
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-QIU_SPAN)//水平方向angleSpan度一份
        	{
        		//纵向横向各到一个角度后计算对应的此点在球面上的四边形顶点坐标
        		//并构建两个组成四边形的三角形
        		
        		double xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		
        		xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-QIU_SPAN));
        		float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y2=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-QIU_SPAN)));
        		
        		xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-QIU_SPAN));
        		float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-QIU_SPAN)));
        		float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-QIU_SPAN)));
        		float y3=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-QIU_SPAN)));
        		
        		xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-QIU_SPAN)));
        		float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-QIU_SPAN)));
        		float y4=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));   
        		
        		//构建第一三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//构建第二三角形
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
        	}
        } 	
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        //将alVertix中的坐标值转存到一个float数组中
        float vertices[]=new float[vCount*3];
    	for(int i=0;i<alVertix.size();i++)
    	{
    		vertices[i]=alVertix.get(i);
    	}
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //创建绘制顶点法向量缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为int型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mNormalBuffer.position(0);//设置缓冲区起始位置     
        //顶点纹理坐标数据的初始化================begin============================
        float texCoor[]=generateTexCoor
    	(
   			 (int)(360/QIU_SPAN), //纹理图切分的列数
   			 (int)(180/QIU_SPAN)  //纹理图切分的行数
   	    );
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置

    }
    //初始化着色器的intShader方法
    public void initShader(int mProgram)
    {
        this.mProgram=mProgram; 
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        //获取程序中顶点法向量属性引用id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        //获取程序中是否绘制阴影属性引用id
        muIsShadow=GLES20.glGetUniformLocation(mProgram, "uisShadow"); 
        muIsShadowFrag=GLES20.glGetUniformLocation(mProgram, "uisShadowFrag");
        //获取是否绘制篮板上阴影属性应用ID
        muIsLanBanShdow=GLES20.glGetUniformLocation(mProgram, "uisLanbanFrag");
        //获取程序中摄像机矩阵引用id
        muCameraMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMCameraMatrix"); 
        //获取程序中投影矩阵引用id
        muProjMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMProjMatrix");  
        //获取桌球纹理属性引用id 
        muBallTexHandle=GLES20.glGetUniformLocation(mProgram, "usTextureBall"); 
        //获取程序中平面法向量引用id;
        muPlaneN=GLES20.glGetUniformLocation(mProgram, "uplaneN");
        //获取程序中平面上的点的引用的Id
        muPlaneV=GLES20.glGetUniformLocation(mProgram, "uplaneA");
    }
    public void drawSelf(int ballTexId,int isShadow,int planeId,int isLanbanYy)//0-no shadow 1-with shadow
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram); 
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //将位置、旋转变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);  
         //将光源位置传入shader程序   
         GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         //将摄像机位置传入shader程序   
         GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将是否绘制阴影属性传入shader程序 
         GLES20.glUniform1i(muIsShadow, isShadow);
         GLES20.glUniform1i(muIsShadowFrag, isShadow);     
         GLES20.glUniform1i(muIsLanBanShdow, isLanbanYy);
         //将摄像机矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muCameraMatrixHandle, 1, false, MatrixState.mVMatrix, 0); 
         //将投影矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muProjMatrixHandle, 1, false, MatrixState.mProjMatrix, 0); 
         //将平面位置传入程序
         GLES20.glUniform3fv(muPlaneV, 1, Constant.mianFXL[planeId][0]);
         //将平面法向量传入程序
         GLES20.glUniform3fv(muPlaneN, 1, Constant.mianFXL[planeId][1]);
         
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

         //传入顶点法向量数据
         GLES20.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		3,   
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         );   
         
         //允许顶点位置、纹理坐标、法向量数据数组
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);  
         
         //绑定纹理
         GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
         GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ballTexId);    
         GLES20.glUniform1i(muBallTexHandle, 0);
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;//列数
    	float sizeh=1.0f/bh;//行数
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
