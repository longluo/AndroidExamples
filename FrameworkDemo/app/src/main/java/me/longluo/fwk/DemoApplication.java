package me.longluo.fwk;

import android.app.Application;

import com.hjq.toast.Toaster;

import timber.log.Timber;


public class DemoApplication extends Application {

    private static final String TAG = DemoApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();

        Timber.d("onCreate");

        initSdk();
    }

    private void initSdk() {
        Toaster.init(this);

        Timber.plant(new Timber.DebugTree());
    }
}
