package pfg.com.screenproc;

import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;

/**
 * Created by FPENG3 on 2018/8/3.
 */

public interface IRenderer extends GLSurfaceView.Renderer {

    public void startRecord(EGLContext eglContext);

    public void startRecord();

    public void stopRecord();

    public void shutdown();
}
