package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
import com.bn.core.MatrixState;
 
//��ʼ��������
public class StartAndEnd
{
	//��ʼ��������������
	TextureRect tr;
	//��ʼ����������֧��
	Cylinder cylinder;
	
	//�зֵĽǶ�
	final float ANGLE_SPAN=20;
	final float UNIT_SIZE=1.0f;
	final float R=0.5f;
	final float width=24;
	final float cHeight=12;
	final float height=2;
	public StartAndEnd(int programId)
	{			
		cylinder=new Cylinder(programId,R,ANGLE_SPAN,cHeight);
		tr=new TextureRect(programId,width-R,height);
	}
	//�ܵĻ��Ʒ���drawSelf
	public void drawSelf(int zzTexId,int tfTexId,float y,int dyFlag)
	{
		if(dyFlag==0)//����ʵ��
		{
			//���֧��
			MatrixState.pushMatrix();
			MatrixState.translate(-width, 0, 0);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//�Ҳ�֧��
			MatrixState.pushMatrix();
			MatrixState.translate(width, 0, 0);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//����
			MatrixState.pushMatrix();
			MatrixState.translate(0, cHeight-height, 0); 
			tr.drawSelf(tfTexId);
			MatrixState.popMatrix();
		}
		else if(dyFlag==1)//���Ƶ�Ӱ
		{
			//ʵ�ʻ���ʱY�����
			float yTranslate=y;
			//���о������ʱ�ĵ���ֵ
			float yjx=(0-yTranslate)*2;
			
			//�رձ������
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //���֧��
			MatrixState.pushMatrix();
			MatrixState.translate(-width, 0, 0);
			MatrixState.translate(0, yjx, 0);
			MatrixState.scale(1, -1, 1);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//�Ҳ�֧��
			MatrixState.pushMatrix();
			MatrixState.translate(width, 0, 0);
			MatrixState.translate(0, yjx, 0);
			MatrixState.scale(1, -1, 1);
			cylinder.drawSelf(zzTexId);
			MatrixState.popMatrix();
			//����
			MatrixState.pushMatrix();
			MatrixState.translate(0, cHeight-height, 0); 
			MatrixState.translate(0, (0-yTranslate-cHeight+height)*2, 0); 
			MatrixState.scale(1, -1, 1); 
			tr.drawSelf(tfTexId);
			MatrixState.popMatrix();
			//�򿪱������
            GLES20.glEnable(GLES20.GL_CULL_FACE);
		}
	} 
	
	//�ڲ��ࡪ��Բ��
	private class Cylinder
	{
		//�Զ���Shader���������
		int mProgram;
		//�ܱ任���������id
		int muMVPMatrixHandle;
		//�������Ե�����id
		int maPositionHandle;
		//�����������������id
		int maTexCoorHandle;
		
		//�����������ݻ���
		FloatBuffer mVertexBuffer;
		//���������������ݻ���
		FloatBuffer mTexCoorBuffer;
		int vCount=0;//��������
		
		//RΪԲ���ײ��İ뾶��rΪԲ���ϲ��İ뾶��angle_span��ʾ�����зֵĽǶ�
		public Cylinder(int programId,float R,float angle_span,float height)
		{
			initVertexData(R,angle_span,height);
			initShader(programId);
		}
		//��ʼ���������ݵķ���
		public void initVertexData(float R,float angle_span,float height)
		{
			List<Float> tempList=new ArrayList<Float>();
			for(float vAngle=0;vAngle<360;vAngle=vAngle+angle_span)
			{
				float x0=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float y0=height; 
				float z0=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x1=(float) (R*Math.cos(Math.toRadians(vAngle))); 
				float y1=-height;
				float z1=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x2=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y2=-height;
				float z2=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				float x3=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y3=height;
				float z3=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				tempList.add(x0); tempList.add(y0); tempList.add(z0);
				tempList.add(x1); tempList.add(y1); tempList.add(z1);
				tempList.add(x3); tempList.add(y3); tempList.add(z3);

				tempList.add(x3); tempList.add(y3); tempList.add(z3);
				tempList.add(x1); tempList.add(y1); tempList.add(z1);
				tempList.add(x2); tempList.add(y2); tempList.add(z2);
			}
			vCount=tempList.size()/3;//��������
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
		//��ʼ����ɫ�������initShader����
		public void initShader(int programId)
		{
			mProgram=programId;
			//��ö����������ݵ�����
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//�����������������id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//�Զ���Ļ��Ʒ���
		public void drawSelf(int texId)
		{
			//ʹ��ĳ��ָ����Shader����
			GLES20.glUseProgram(mProgram);
			//�����ձ任�����뵽Shader������
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//���붥����������
			GLES20.glVertexAttribPointer
			(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT, 
				false, 
				3*4, 
				mVertexBuffer
			);
			//����������������
			GLES20.glVertexAttribPointer
			(
				maTexCoorHandle, 
				2, 
				GLES20.GL_FLOAT, 
				false, 
				2*4, 
				mTexCoorBuffer
			);
			//��������λ����������
	        GLES20.glEnableVertexAttribArray(maPositionHandle);  
	        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
	        
	        //������
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
	        
	        //������������
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
		//�Զ��з�����������������ķ���
	    public float[] generateTexCoor(int bw,int bh,float width,float height)
	    {
	    	float[] result=new float[bw*bh*6*2]; 
	    	float sizew=width/bw;//����
	    	float sizeh=height/bh;//����
	    	int c=0;
	    	for(int i=0;i<bh;i++)
	    	{
	    		for(int j=0;j<bw;j++)
	    		{
	    			//ÿ����һ�����Σ������������ι��ɣ��������㣬12����������
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
	//�·��ĵ���
	private class TextureRect
	{
		//�Զ�����Ⱦ������ɫ�����������
		int mProgram;
		//�ܱ任���������id
		int muMVPMatrixHandle;
		//�������Ե�����id
		int maPositionHandle;
		//�����������������id
		int maTexCoorHandle;
		
		//�����������ݻ���
		FloatBuffer mVertexBuffer;
		//���������������ݻ���
		FloatBuffer mTexCoorBuffer;
		int vCount=0;//��������
		
		//RΪԲ���ײ��İ뾶��rΪԲ���ϲ��İ뾶
		public TextureRect(int programId,float width,float height)
		{
			initVertexData(width,height);
			initShader(programId);
		}
		//��ʼ���������ݵķ���
		public void initVertexData(float width,float height)
		{
			float[] vertex=new float[]
            {
				-width,height,0,
				-width,-height,0,
				width,-height,0,
				
				-width,height,0,
				width,-height,0,
				width,height,0,
				
				-width,height,0,
				width,-height,0,
				-width,-height,0,
				
				-width,height,0,
				width,height,0,
				width,-height,0,
            };
			vCount=12;
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
            {
 				0,0,  0,1,  1,1,
 				0,0,  1,1,  1,0,
 				
 				0,0,  1,1,  0,1,
 				0,0,  1,0,  1,1
            };
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		//��ʼ����ɫ������ķ���
		public void initShader(int programId)
		{
			mProgram=programId;
			//��ö����������ݵ�����
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//�����������������id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//�Զ���Ļ��Ʒ���
		public void drawSelf(int texId)
		{
			//ʹ��ĳ��ָ����Shader����
			GLES20.glUseProgram(mProgram);
			//�����ձ任�����뵽Shader������
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//������������
			GLES20.glVertexAttribPointer
			(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT, 
				false, 
				3*4, 
				mVertexBuffer
			);
			//����������������
			GLES20.glVertexAttribPointer
			(
				maTexCoorHandle, 
				2, 
				GLES20.GL_FLOAT, 
				false, 
				2*4, 
				mTexCoorBuffer
			);
			//��������λ����������
	        GLES20.glEnableVertexAttribArray(maPositionHandle);  
	        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
	        
	        //������
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
	        
	        //������������
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
	}
}