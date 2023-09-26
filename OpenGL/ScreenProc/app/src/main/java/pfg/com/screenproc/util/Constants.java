package pfg.com.screenproc.util;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class Constants {

    public static final int BYTES_PER_FLOAT = 4;

    public static final String texture_vertex_shader =
            "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "void main() {" +
                    "  v_TextureCoordinates = a_TextureCoordinates;" +
                    "  gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String texture_fragment_shader =
            "precision mediump float;" +
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

    public static final String simple_vertex_shader =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            // "uniform mat4 uMVPMatrix;" +
            "attribute vec4 a_Position;" +
                    "attribute vec4 a_Color;"+
                    "varying vec4 v_Color;"+
                    "uniform mat4 u_Matrix;"+
                    //"uniform mat4 u_Translation;"+
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    //"  gl_Position = u_Matrix * vPosition * u_Translation;" +
                    "  gl_Position = u_Matrix * a_Position;" +
                    "  gl_PointSize = 10.0;" +
                    "  v_Color = a_Color;" +
                    "}";

    public static final String simple_fragment_shader =
            "precision mediump float;" +
                    //"uniform vec4 vColor;" +
                    "varying vec4 v_Color;"+
                    "void main() {" +
                    //"  gl_FragColor = vColor;" +
                    "  gl_FragColor = v_Color;" +
                    "}";


    public static final String video_vertex_shader =
            "attribute vec4 aPosition;" +
                    "attribute vec4 aTexCoord;" +
                    "varying vec2 vTexCoord;" +
                    "uniform mat4 uMatrix;" +
                    "uniform mat4 uSTMatrix;"+
                    "void main() {" +
                    "    vTexCoord = (uSTMatrix * aTexCoord).xy;"+
                    "    gl_Position = uMatrix*aPosition;" +
                    "}";

    /*
     * 注意要有"\n"，不然会出现如下错误：
     * L0001: Typename expected, found 'samplerExternalOES'
     */
    public static final String video_fragment_shader =
            "#extension GL_OES_EGL_image_external : require\n"+
            "precision mediump float;" +
                    "varying vec2 vTexCoord;" +
                    "uniform samplerExternalOES sTexture;"+
                    "void main() {" +
                    "  gl_FragColor=texture2D(sTexture, vTexCoord);" +
                    "}";

}
