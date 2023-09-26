package pfg.com.screenproc;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by FPENG3 on 2018/8/8.
 */

public class VideoDialGLSurfaceView extends GLSurfaceView {

    Renderer mRenderer;

    public VideoDialGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(3);
    }

    public VideoDialGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        mRenderer = renderer;
    }
}
