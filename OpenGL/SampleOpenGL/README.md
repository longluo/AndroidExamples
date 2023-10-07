Sample OpenGL Android application

Sesstion 1 : OpenGL ES 範例 - GLSurfaceView 實作
創建 GLSurfaceView 與 SampleGLRenderer 物件並填满绿色背景. SampleGLRenderer 類别為 .Renderer 的實作.
使用 OpenGL ES2.0 的 Vexter shader 與 Fragment shader 繪製三角型.
SampleGLTexture 類别使用 OpenGL ES 1.0 繪製方形並贴上纹理 (texture). 方形繪製使用 glDrawArrays() 與参數 GL_TRIANGLE_STRIP.
使用 OpenGL ES 2.0 的 Vexter shader 與 Fragment shader 繪製方形並贴上纹理 (Texture).
(http://artistehsu.pixnet.net/blog/post/354896632)

Sesstion 2 : OpenGL ES 範例 - 着色器编譯除錯
着色器原始碼與程式執行時利用 glCompileShader() 编譯, 本文說明编譯着色器原始碼的除錯方式.
(http://artistehsu.pixnet.net/blog/post/355288171)

Session 3 : OpenGL ES 範例 - 像素着色器 (Fragment Shader)
Fragment shader 又稱為像素 (Pixel) 着色器. 本文使用像素着色器修改原始圖片 (纹理, Texture), 將圖片分成四個象限缩圖, 第一象限為原色, 其餘為過濾 R, G , B 後的输出.
(http://artistehsu.pixnet.net/blog/post/355285899)
