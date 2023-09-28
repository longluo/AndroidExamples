// 定义一个属性，顶点坐标
attribute vec4 v_Position;
// 定义一个属性，纹理坐标
attribute vec2 f_Position;
// varying 可用于相互传值
varying vec2 ft_Position;

uniform mat4 u_Matrix;
void main(){
    ft_Position = f_Position;
    gl_Position = v_Position *u_Matrix;
}
