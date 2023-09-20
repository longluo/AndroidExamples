uniform mat4 uMVPMatrix; //总变换矩阵

attribute vec3 aPosition;  //顶点位置
uniform int uraodon;//扰动值

attribute vec2 aTexCoor;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量
void main()     
{                  
   vec4 rPosition=vec4(aPosition,1);
   float raodonR=0.0;
   if(uraodon==1)
   {
     
     if(rPosition.y<0.46)
     {
       raodonR=sqrt(rPosition.y)/4.0;
       rPosition.x=rPosition.x+raodonR;
     }
     else
     {
       raodonR=sqrt(1.12-rPosition.y)/4.0;
       rPosition.x=rPosition.x+raodonR;
     }
     
   }
   else if(uraodon==3)
   {
     
     if(rPosition.y<0.46)
     {
       raodonR=sqrt(rPosition.y)/4.0;
       rPosition.x=rPosition.x-raodonR;
     }
     else
     {
       raodonR=sqrt(1.12-rPosition.y)/4.0;
       rPosition.x=rPosition.x-raodonR;
     }
     
   }
   
      gl_Position =uMVPMatrix * rPosition; //根据总变换矩阵计算此次绘制此顶点位置
   
  
   
   
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      