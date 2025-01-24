package me.longluo.audio;

import android.app.Application;

import timber.log.Timber;


/**
 * Author: admin
 * Date: 2024/12/3 11:38
 * Description:
 * History:
 */
public class AudioApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initSdk();
    }

    private void initSdk() {
        Timber.plant(new Timber.DebugTree());
    }

}
