// 定义一个属性，顶点坐标
attribute vec4 v_Position;
// 定义一个属性，纹理坐标
attribute vec2 f_Position;
// varying 可用于相互传值
varying vec2 ft_Position;
void main() {
    // 赋值 ft_Position，gl_Position 变量是 gl 内置的
    ft_Position = f_Position;
    gl_Position = v_Position;
}
