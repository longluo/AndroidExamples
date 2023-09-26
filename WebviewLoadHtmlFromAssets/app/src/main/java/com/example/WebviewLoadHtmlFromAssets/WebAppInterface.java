package com.example.WebviewLoadHtmlFromAssets;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class WebAppInterface {

    private Context mContext;

    @Nullable
    private CallbackData mSlideCallback;

    public WebAppInterface(Context context) {
        mContext = context;
    }

    public void setSlideCallback(CallbackData callback) {
        mSlideCallback = callback;
    }

    @JavascriptInterface
    public void getSlideData(String data) {
        Log.d("webtest", "data: " + data);

        Toast.makeText(mContext,  "getSlideData: " + data, Toast.LENGTH_LONG).show();

        if (mSlideCallback != null) {
            mSlideCallback.onSlideSuccess(data);
        }
    }

    public interface CallbackData {
        void onSlideSuccess(String data);
    }
}

