//星空着色器
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float uPointSize;//点尺寸
attribute vec3 aPosition;  //顶点位置
void main(){     
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置   
   gl_PointSize=uPointSize;
}                 