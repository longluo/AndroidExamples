package pfg.com.screenproc;

import android.app.Application;
import android.content.Context;

/**
 * Created by FPENG3 on 2018/8/8.
 */

public class MyApplication extends Application {

    static Context mAppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
    }

    static public Context getContext() {
        return mAppContext;
    }
}
