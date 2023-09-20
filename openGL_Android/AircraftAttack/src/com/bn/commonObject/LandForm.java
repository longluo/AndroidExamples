package com.bn.commonObject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.bn.core.MatrixState;

import android.opengl.GLES20;
import static com.bn.gameView.Constant.*;
//构建地形的类   地形平行于XZ平面    并且位于XZ平面的第四象限
//这里的山地要贴四副纹理,在着色器中根据高度来判定
public class LandForm
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    
    int uTuCengTexHandle;//土层纹理属性引用id  
    int uCaoDiTexHandle;//草地纹理属性引用id  
    int uShiTouTexHandle;//石头纹理属性引用id  
    int uShanDingTexHandle;//山顶纹理属性引用id  

    int muHightHandle;//渐变高度
    int muHightspanHandle;
    int uLandFlagHandle;//不同类型的山的标志如果为1表示地面上的山
    private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
    int vCount;//顶点数量
    private boolean ishuidutu=false;//是否是灰度图还是程序生成
    public LandForm(int terrainId,int mProgram)
    {
    	this.mProgram=mProgram;
    	if(terrainId==3||terrainId==5||terrainId==6)//如果是陆地上的山,则不绘制倒影
    	{
    		ishuidutu=true;
    	}
    	initVertexData(terrainId);
    	initShader();
    }
    public void initVertexData(int terrainId)
    {
    	int cols=LANDS_HEIGHT_ARRAY[terrainId][0].length-1;//列数
    	int rows=LANDS_HEIGHT_ARRAY[terrainId].length-1;//行数
    	//绘制程序生成的山
    	if(!ishuidutu)
    	{
	        //纹理的块数
	    	float textureSize=1f;
	    	float sizew=textureSize/cols;//列宽
	    	float sizeh=textureSize/rows;//行宽
	    	//顶点的集合
	    	ArrayList<Float> alVertex=new ArrayList<Float>();
	    	//纹理的 集合
	    	ArrayList<Float> alTexture=new ArrayList<Float>();
	        for(int i=0;i<rows;i++)
	        {
	        	for(int j=0;j<cols;j++)
	        	{        		
	        		//计算当前格子左上侧点坐标       
	        		float zsx=j*LAND_UNIT_SIZE;//当前点x坐标
	        		float zsz=i*LAND_UNIT_SIZE;//当前点z坐标 
	        		
	        		float s=j*sizew;  //s坐标
	    			float t=i*sizeh;  //t坐标
	    			
	        		if(LANDS_HEIGHT_ARRAY[terrainId][i][j]!=0||LANDS_HEIGHT_ARRAY[terrainId][i+1][j]!=0||LANDS_HEIGHT_ARRAY[terrainId][i][j+1]!=0)
	        		{
	        			//左上点
	            		alVertex.add(zsx);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i][j]);
	            		alVertex.add(zsz);
	            		//左下点
	            		alVertex.add(zsx);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i+1][j]);
	            		alVertex.add(zsz+LAND_UNIT_SIZE);
	            		//右上点
	            		
	            		alVertex.add(zsx+LAND_UNIT_SIZE);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i][j+1]);
	            		alVertex.add(zsz);
	        			
	        			alTexture.add(s);
	        			alTexture.add(t);
	        			
	        			alTexture.add(s);
	        			alTexture.add(t+sizeh);
	        			
	        			alTexture.add(s+sizew);
	        			alTexture.add(t);
            			//--------------------绘制倒影-------------        		
                		//左上点
                		alVertex.add(zsx);
	            		alVertex.add(-LANDS_HEIGHT_ARRAY[terrainId][i][j]);
	            		alVertex.add(zsz);
                		//右上点
                		alVertex.add(zsx+LAND_UNIT_SIZE);
	            		alVertex.add(-LANDS_HEIGHT_ARRAY[terrainId][i][j+1]);
	            		alVertex.add(zsz);
                		//左下点
                		alVertex.add(zsx);
	            		alVertex.add(-LANDS_HEIGHT_ARRAY[terrainId][i+1][j]);
	            		alVertex.add(zsz+LAND_UNIT_SIZE);
            			
            			alTexture.add(s);
	        			alTexture.add(t);
            			
            			alTexture.add(s+sizew);
	        			alTexture.add(t);

	        			alTexture.add(s);
	        			alTexture.add(t+sizeh);
	        		}
	        		if(LANDS_HEIGHT_ARRAY[terrainId][i][j+1]!=0||LANDS_HEIGHT_ARRAY[terrainId][i+1][j]!=0||LANDS_HEIGHT_ARRAY[terrainId][i+1][j+1]!=0){
	        			//右上点
	            		alVertex.add(zsx+LAND_UNIT_SIZE);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i][j+1]);
	            		alVertex.add(zsz);
	            		//左下点
	            		alVertex.add(zsx);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i+1][j]);
	            		alVertex.add(zsz+LAND_UNIT_SIZE);
	            		//右下点
	            		alVertex.add(zsx+LAND_UNIT_SIZE);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i+1][j+1]);
	            		alVertex.add(zsz+LAND_UNIT_SIZE);
	        			
	        			alTexture.add(s+sizew);
	        			alTexture.add(t);
	        			
	        			alTexture.add(s);
	        			alTexture.add(t+sizeh);
	        			
	        			alTexture.add(s+sizew);
	        			alTexture.add(t+sizeh);
            			//右上点
                		alVertex.add(zsx+LAND_UNIT_SIZE);
                		alVertex.add(-LANDS_HEIGHT_ARRAY[terrainId][i][j+1]);
                		alVertex.add(zsz);
                		//右下点
                		alVertex.add(zsx+LAND_UNIT_SIZE);
                		alVertex.add(-LANDS_HEIGHT_ARRAY[terrainId][i+1][j+1]);
                		alVertex.add(zsz+LAND_UNIT_SIZE);
                		//左下点
                		alVertex.add(zsx);
                		alVertex.add(-LANDS_HEIGHT_ARRAY[terrainId][i+1][j]);
                		alVertex.add(zsz+LAND_UNIT_SIZE);
            			
            			alTexture.add(s+sizew);
            			alTexture.add(t);
            			
            			alTexture.add(s+sizew);
            			alTexture.add(t+sizeh);
            			
            			alTexture.add(s);
            			alTexture.add(t+sizeh);
	        		}
	        	}
	        }
	        vCount=alVertex.size()/3;
	        float vertices[]=new float[vCount*3];
	        for(int i=0;i<vCount*3;i++)
	        {
	        	vertices[i]=alVertex.get(i);
	        }
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	    	//创建顶点纹理缓冲
	        float textures[]=new float[alTexture.size()];
	        for(int i=0;i<alTexture.size();i++)
	        {
	        	textures[i]=alTexture.get(i);
	        }
	        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
	        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
	        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
	        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
	        mTextureBuffer.position(0);//设置缓冲区起始位置
    	}
    	else//加载的灰度图,绘制的时候用trangle_strip方式的
    	{
	        //纹理的块数
	    	float textureSize=1f;
	    	float sizew=textureSize/cols;//列宽
	    	float sizeh=textureSize/rows;//行宽
	    	//顶点的集合
	    	ArrayList<Float> alVertex=new ArrayList<Float>();
	    	//纹理的 集合
	    	ArrayList<Float> alTexture=new ArrayList<Float>();
	        for(int i=0;i<rows;i++)
	        {
	        	for(int j=0;j<cols;j++)
	        	{        		
	        		//计算当前格子左上侧点坐标       
	        		float zsx=j*LAND_UNIT_SIZE;//当前点x坐标
	        		float zsz=i*LAND_UNIT_SIZE;//当前点z坐标 
	        		
	        		float s=j*sizew;  //s坐标
	    			float t=i*sizeh;  //t坐标
	    			
	    			if(i!=0&&j==0)
	    			{
	    				//左上点
		    			alVertex.add(zsx);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i][j]);
	            		alVertex.add(zsz);
	            		
	            		alTexture.add(s);
	        			alTexture.add(t);
	    			}
    				//左上点
	    			alVertex.add(zsx);
            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i][j]);
            		alVertex.add(zsz);
            		
            		alTexture.add(s);
        			alTexture.add(t);
            		//左下点
            		alVertex.add(zsx);
            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i+1][j]);
            		alVertex.add(zsz+LAND_UNIT_SIZE);
            		
            		alTexture.add(s);
        			alTexture.add(t+sizeh);
        			
	    			if(j==cols-1)
	    			{
	    				//右上点
		    			alVertex.add(zsx+LAND_UNIT_SIZE);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i][j+1]);
	            		alVertex.add(zsz);
	            		
	            		alTexture.add(s+sizew);
	        			alTexture.add(t);
	            		//右下点
	            		alVertex.add(zsx+LAND_UNIT_SIZE);
	            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i+1][j+1]);
	            		alVertex.add(zsz+LAND_UNIT_SIZE);
	            		
	            		alTexture.add(s+sizew);
	        			alTexture.add(t+sizeh);
	            		if(i!=rows-1)
	            		{
	            			//右下点
		            		alVertex.add(zsx+LAND_UNIT_SIZE);
		            		alVertex.add(LANDS_HEIGHT_ARRAY[terrainId][i+1][j+1]);
		            		alVertex.add(zsz+LAND_UNIT_SIZE);
		            		
		            		alTexture.add(s+sizew);
		        			alTexture.add(t+sizeh);
	            		}
	    			  }
	        	}
	        }
	        vCount=alVertex.size()/3;
	        float vertices[]=new float[vCount*3];
	        for(int i=0;i<vCount*3;i++)
	        {
	        	vertices[i]=alVertex.get(i);
	        }
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	    	//创建顶点纹理缓冲
	        float textures[]=new float[alTexture.size()];
	        for(int i=0;i<alTexture.size();i++)
	        {
	        	textures[i]=alTexture.get(i);
	        }
	        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
	        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
	        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
	        mTextureBuffer.position(0);//设置缓冲区起始位置
    	}
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
        //山地的三幅纹理图
        uShanDingTexHandle=GLES20.glGetUniformLocation(mProgram, "usTextureShanDing");  
        uTuCengTexHandle=GLES20.glGetUniformLocation(mProgram, "usTextureTuCeng");  
        uCaoDiTexHandle=GLES20.glGetUniformLocation(mProgram, "usTextureCaoDi");  
        muHightHandle=GLES20.glGetUniformLocation(mProgram, "uheight");  
        muHightspanHandle=GLES20.glGetUniformLocation(mProgram, "uheight_span");
        uShiTouTexHandle=GLES20.glGetUniformLocation(mProgram, "usTextureShiTou");  
        uLandFlagHandle=GLES20.glGetUniformLocation(mProgram, "uland_flag");  
    }
   public void drawSelf(int landFlag,int tex_terrain_shandingId,int texTuCengId,int texCaoDiId,int texShiTouId,float height,float height_span)
    {  
    	//制定使用某套shader程序
   	 	GLES20.glUseProgram(mProgram);        
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        GLES20.glUniform1f(muHightHandle, height);//传入渐变的高度
        GLES20.glUniform1f(muHightspanHandle, height_span);//传入渐变的高度
        GLES20.glUniform1i(uLandFlagHandle, landFlag);//传入山的标志
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texTuCengId);    
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texCaoDiId);    
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texShiTouId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex_terrain_shandingId);     
        GLES20.glUniform1i(uTuCengTexHandle, 0);
        GLES20.glUniform1i(uCaoDiTexHandle, 1);  
        GLES20.glUniform1i(uShiTouTexHandle, 2);  
        GLES20.glUniform1i(uShanDingTexHandle, 3);  
        if(!ishuidutu)
        {
        	//绘制纹理矩形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
        }
        else
        {
        	//绘制纹理矩形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vCount);
        }
    }
}
