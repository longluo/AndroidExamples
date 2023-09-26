package com.example.android.camera2.video.grafika;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.android.camera2.video.fragments.CameraFragmentGlSurface;

import java.lang.ref.WeakReference;

/**
 * Handles camera operation requests from other threads.  Necessary because the Camera
 * must only be accessed from one thread.
 * <p>
 * The object is created on the UI thread, and all handlers run there.  Messages are
 * sent from other threads, using sendMessage().
 */
public class CameraHandler extends Handler {


    public static final int MSG_SET_SURFACE_TEXTURE = 0;
    public static final String TAG = "CameraHandlerGrafika";

    // Weak reference to the Activity; only access this from the UI thread.
    private WeakReference<CameraFragmentGlSurface> mWeakActivity;

    public CameraHandler(CameraFragmentGlSurface fragment, HandlerThread thread) {
        super(thread.getLooper());
        mWeakActivity = new WeakReference<CameraFragmentGlSurface>(fragment);
    }

    /**
     * Drop the reference to the activity.  Useful as a paranoid measure to ensure that
     * attempts to access a stale Activity through a handler are caught.
     */
    public void invalidateHandler() {
        mWeakActivity.clear();
    }

    @Override  // runs on UI thread
    public void handleMessage(Message inputMessage) {
        int what = inputMessage.what;
        Log.d(TAG, "CameraHandler [" + this + "]: what=" + what);

        CameraFragmentGlSurface activity = mWeakActivity.get();
        if (activity == null) {
            Log.w(TAG, "CameraHandler.handleMessage: activity is null");
            return;
        }

        switch (what) {
            case MSG_SET_SURFACE_TEXTURE:
                activity.handleSetSurfaceTexture((SurfaceTexture) inputMessage.obj);
                break;
            default:
                throw new RuntimeException("unknown msg " + what);
        }
    }
}