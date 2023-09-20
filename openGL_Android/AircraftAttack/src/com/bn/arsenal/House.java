package com.bn.arsenal;
import static com.bn.gameView.Constant.ARSENAL_X;
import static com.bn.gameView.Constant.ARSENAL_Y;
import static com.bn.gameView.Constant.ARSENAL_Z;
import static com.bn.gameView.Constant.isOvercome;
import android.opengl.GLES20;

import com.bn.commonObject.NumberForDraw;
import com.bn.commonObject.TextureRect;
import com.bn.core.MatrixState;
public class House 
{
	public float house_width=ARSENAL_X;
	public float house_height=ARSENAL_Y;//房屋的大小
	public float house_length=ARSENAL_Z;
	public float house_roof_width=ARSENAL_X+15;
	public float house_roof_height=45;//屋顶矩形的大小
	public float roof_front_Angle=10;//屋顶与平面的夹角
	public TextureRect house_front;//前后
	public TextureRect house_side;//侧面
	public TextureRect house_roof;//屋顶
	public Triangle triangle;//侧面三角形
	public Annulus annulus;//颜色圆环
	public NumberForDraw nm;//数字引用
	public TextureRect backgroundRect;//背景
	public float annulusR=250;
	public float annulusr=200;
	float roofx=0;
	float roofy=house_height+house_length/2*(float)Math.tan(Math.toRadians(roof_front_Angle))-
	house_roof_height*(float)Math.sin(Math.toRadians(roof_front_Angle))/2;
	float roofz=house_roof_height*(float)Math.cos(Math.toRadians(roof_front_Angle))/2;
	public House(int mProgram,TextureRect backgroundRect,NumberForDraw nm)
	{
		this.nm=nm;
		this.backgroundRect=backgroundRect;
		house_front=new TextureRect( house_width, house_height-1.5f,mProgram);
		house_side=new TextureRect( house_length, house_height,mProgram);
		house_roof=new TextureRect( house_roof_width, house_roof_height,mProgram);
		annulus=new Annulus(mProgram,annulusR,annulusr,15);
		triangle=new Triangle(mProgram,house_length,house_length/2*(float)Math.tan(Math.toRadians(roof_front_Angle))-0.5f);
	}
	public void drawSelf(int texFrot,int texSide,int texRoof,int texAnnulus,float yuanAngle,
			int backgroundRectId,int numberID,int blood,float yAngle
	){
		
		
		MatrixState.pushMatrix();
		MatrixState.translate(0, house_height/2, house_length/2);
		house_front.drawSelf(texFrot);
		MatrixState.translate(0, 0, -house_length);
		MatrixState.rotate(180, 0, 1, 0);
		house_front.drawSelf(texFrot);
		MatrixState.popMatrix();

		MatrixState.pushMatrix();
		MatrixState.translate(-house_width/2, house_height/2,0);
		MatrixState.rotate(-90, 0, 1, 0);
		house_side.drawSelf(texSide);
		MatrixState.translate(0, 0, -house_width);
		MatrixState.rotate(180, 0, 1, 0);
		house_side.drawSelf(texSide);
		MatrixState.translate(0, house_height/2, 0);
		triangle.drawSelf(texSide);
		MatrixState.translate(0, 0, -house_width);
		MatrixState.rotate(180, 0, 1, 0);
		triangle.drawSelf(texSide);
		MatrixState.popMatrix();
		
		MatrixState.pushMatrix();
		MatrixState.translate(roofx,roofy,roofz);
		MatrixState.rotate(roof_front_Angle-90, 1, 0, 0);
		house_roof.drawSelf(texRoof);
		MatrixState.rotate(180, 0, 1, 0);
		house_roof.drawSelf(texRoof);
		MatrixState.popMatrix();
		MatrixState.pushMatrix();
		MatrixState.translate(roofx,roofy,-roofz);
		MatrixState.rotate(90-roof_front_Angle, 1, 0, 0);
		house_roof.drawSelf(texRoof);
		MatrixState.rotate(180, 0, 1, 0);
		house_roof.drawSelf(texRoof);
		MatrixState.popMatrix();
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		MatrixState.pushMatrix();
		MatrixState.translate(0,4,0);
		MatrixState.rotate(yuanAngle, 0, 1, 0);
		annulus.drawSelf(texAnnulus);//地下圆环
		MatrixState.popMatrix();
		
		
    	if(blood>=0&&!isOvercome){
    		MatrixState.pushMatrix();
    		MatrixState.translate(0,house_height+60,0f);  
    		MatrixState.rotate(yAngle, 0,1, 0);
    		MatrixState.scale(0.75f, 0.8f, 0.1f);
    		backgroundRect.bloodValue=blood*2-100+6;
    		backgroundRect.drawSelf(backgroundRectId);      	  
        	MatrixState.popMatrix();
    	}  
    	GLES20.glDisable(GLES20.GL_BLEND);
    	    	
		
	}
}
