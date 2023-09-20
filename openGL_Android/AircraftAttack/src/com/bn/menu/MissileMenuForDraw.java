package com.bn.menu;

import android.opengl.GLES20;

import com.bn.commonObject.BallTextureByVertex;
import com.bn.commonObject.CylinderForDraw;
import com.bn.commonObject.TextureRect;
import com.bn.core.MatrixState;

/*
 * 主要用于绘制导弹菜单
 */
public class MissileMenuForDraw {
	private TextureRect rect;// 纹理矩形
	private BallTextureByVertex halfBall;// 弹头用的半球
	private CylinderForDraw cylinder;// 圆柱
	private TextureRect rect_tail;// 纹理矩形
	private float rect_width = 0.8f;// 纹理矩形的宽度
	private float rect_height = 2.5f;// 纹理矩形的高度
	private float tail_width = 2.3f;// 尾翼的宽度
	private float tail_height = 1.2f;// 尾翼的高度
	private float rect_offset = (float) (rect_width / 2 / Math.tan(Math
			.toRadians(22.5f)));// 纹理矩形沿Z轴负方向的偏移量
	private float radius = (float) (rect_width / 2 / Math.sin(Math
			.toRadians(22.5f)));// 半球和圆柱的半径
	private float halfBall_span = rect_height / 2;// 半球的偏移量
	private float cylinder_length = 0.2f;// 圆柱的长度

	public MissileMenuForDraw(int mProgram) {
		rect = new TextureRect(rect_width, rect_height, mProgram);// 创建纹理矩形对象
		halfBall = new BallTextureByVertex(radius, mProgram, 0);// 创建半球
		cylinder = new CylinderForDraw(radius, cylinder_length, mProgram); // 创建圆柱
		rect_tail = new TextureRect(tail_width, tail_height, mProgram);// 创建纹理矩形炮弹尾翼
	}

	public void drawSelft(int[] tex_RectId) {
		// ------------------------绘制导弹中间部分
		MatrixState.pushMatrix();
		// 绘制第一个面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[0]);
		MatrixState.popMatrix();
		// 绘制第二个面
		MatrixState.pushMatrix();
		MatrixState.rotate(45, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[1]);
		MatrixState.popMatrix();
		// 绘制第三个面
		MatrixState.pushMatrix();
		MatrixState.rotate(90, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[2]);
		MatrixState.popMatrix();
		// 绘制第四个面
		MatrixState.pushMatrix();
		MatrixState.rotate(135, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[3]);
		MatrixState.popMatrix();
		// 绘制第五个面
		MatrixState.pushMatrix();
		MatrixState.rotate(180, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[4]);
		MatrixState.popMatrix();
		// 绘制第六个面
		MatrixState.pushMatrix();
		MatrixState.rotate(225, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[5]);
		MatrixState.popMatrix();
		// 绘制第七个面
		MatrixState.pushMatrix();
		MatrixState.rotate(270, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[6]);
		MatrixState.popMatrix();
		// 绘制第八个面
		MatrixState.pushMatrix();
		MatrixState.rotate(315, 0, 1, 0);
		MatrixState.translate(0, 0, rect_offset);
		rect.drawSelf(tex_RectId[7]);
		MatrixState.popMatrix();
		MatrixState.popMatrix();
		// ------------------------绘制导弹头部分
		MatrixState.pushMatrix();
		MatrixState.translate(0, halfBall_span, 0);
		halfBall.drawSelf(tex_RectId[8]);
		MatrixState.translate(0, -cylinder_length / 2, 0);
		cylinder.drawSelf(tex_RectId[9]);
		MatrixState.popMatrix();
		// ------------------------绘制导弹尾部分
		MatrixState.pushMatrix();
		MatrixState.translate(0, -halfBall_span, 0);
		MatrixState.rotate(180, 0, 0, 1);
		halfBall.drawSelf(tex_RectId[8]);
		MatrixState.translate(0, -cylinder_length / 2, 0);
		cylinder.drawSelf(tex_RectId[9]);
		MatrixState.popMatrix();
		// 绘制扇叶
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		MatrixState.pushMatrix();
		MatrixState.translate(0, -halfBall_span - tail_height / 2, 0);
		rect_tail.drawSelf(tex_RectId[10]);
		MatrixState.rotate(120, 0, 1, 0);
		rect_tail.drawSelf(tex_RectId[10]);
		MatrixState.rotate(120, 0, 1, 0);
		rect_tail.drawSelf(tex_RectId[10]);
		MatrixState.popMatrix();
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}
}
