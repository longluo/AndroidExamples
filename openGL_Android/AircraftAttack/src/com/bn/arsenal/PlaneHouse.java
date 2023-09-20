package com.bn.arsenal;

import static com.bn.gameView.Constant.house_height;

import com.bn.commonObject.*;
import com.bn.core.MatrixState;
public class PlaneHouse {
	public int col, row;//所在的行列
	public CubeForDraw cub;//房子
	Light_Tower lt;//烟囱
	float tx,ty,tz;
	public PlaneHouse(float tx,float ty,float tz,CubeForDraw cub,Light_Tower lt,int col,int row){
		this.cub=cub;
		this.lt=lt;
		this.col=col;
		this.row=row;
		this.cub=cub;
		this.tx=tx;this.ty=ty;this.tz=tz;
	}
	public void drawSelf(int cubtexId,int cubtexId1,int smallcubtexId,int smallcubtexId2,int lttexId,int  ii,int jj,int rowR,int colR){
		if(row<ii||row>rowR||col<jj||col>colR)
		{
			return;
		}
		
		MatrixState.pushMatrix();
		MatrixState.translate(tx,ty,tz);
		cub.drawSelf(cubtexId, cubtexId1);
		MatrixState.translate(30, -house_height/2, 70);
		lt.drawSelf(lttexId);
		MatrixState.translate(50, house_height/2*0.3f, 80);
		MatrixState.scale(0.5f, 0.3f, 0.6f);
		cub.drawSelf(smallcubtexId, smallcubtexId2);
		MatrixState.popMatrix();
	}
}
