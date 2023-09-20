precision mediump float;
//接收从顶点着色器过来的参数
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying vec3 vPosition;

uniform float colorR;//颜色值的R分量
uniform float colorG;//颜色值的G分量
uniform float colorB;//颜色值的B分量
uniform float colorA;

void main()                         
{    

     //将计算出的颜色给此片元
	   vec4 finalColor;

	   		finalColor=vec4(colorR,colorG,colorB,colorA);


	   gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元颜色值
}