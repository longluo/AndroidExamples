package com.bn.clp;

import static com.bn.clp.Constant.*;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

public class Boat  
{
	List<LoadedObjectVertexNormal> lovo=new ArrayList<LoadedObjectVertexNormal>();
	//str位文件的名称，color位每一个部分对应的颜色（其为二维数组）
	public Boat(String [] str,MyGLSurfaceView mv,int programId)
	{
		for(int i=0;i<str.length;i++)
		{
			lovo.add
			(
				LoadUtil.loadFromFileVertexOnly
				(
					str[i], 
					mv.getResources(), 
					programId
			     )
			 );
		}
	}
	public void drawSelf(float bx,float by,float bz,float yAngle,int dyFlag,int[] texId)
	{	
		for(int i=0;i<lovo.size();i++)
		{
			if(dyFlag==0)//绘制实体
			{
				MatrixState.pushMatrix();
				MatrixState.translate(bx, by, bz);
				MatrixState.rotate(yAngle, 0, 1, 0);
				MatrixState.rotate(head_Angle, 1, 0, 0);
				lovo.get(i).drawSelf(texId[i]);
				MatrixState.popMatrix();
			}
			else if(dyFlag==1)//绘制倒影
			{
				//实际绘制时Y的零点 
				float yTranslate=by;   
				//进行镜像绘制时的调整值
				float yjx=(WATER_HIGH_ADJUST-yTranslate)*2;
				
				//关闭背面剪裁
	            GLES20.glDisable(GLES20.GL_CULL_FACE);
				MatrixState.pushMatrix();
				MatrixState.translate(bx, by, bz);
				MatrixState.rotate(yAngle, 0, 1, 0);
				MatrixState.rotate(-head_Angle, 1, 0, 0);
				MatrixState.translate(0, yjx, 0);
				MatrixState.scale(1, -1, 1);
				lovo.get(i).drawSelf(texId[i]);
				MatrixState.popMatrix();
				//打开背面剪裁
	            GLES20.glEnable(GLES20.GL_CULL_FACE);
			}
		}
	}
}