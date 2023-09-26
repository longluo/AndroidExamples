package com.example.WebviewLoadHtmlFromAssets;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    WebView mWebViewTest;

    WebView mWebViewAliSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadTest();
        loadAliSDK();
    }

    private void loadTest() {
        mWebViewTest = findViewById(R.id.wv_test1);

        mWebViewTest.getSettings().setJavaScriptEnabled(true);
        mWebViewTest.setWebViewClient(new WebViewClient());

        mWebViewTest.loadUrl(Constants.TEST_URL);

//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
    }

    private void loadAliSDK() {
        mWebViewAliSDK = findViewById(R.id.wv_ali);

        mWebViewAliSDK.setBackgroundColor(Color.TRANSPARENT);
        mWebViewAliSDK.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        mWebViewAliSDK.clearHistory();
        mWebViewAliSDK.clearCache(true);

        WebSettings webSettings = mWebViewAliSDK.getSettings();

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUserAgentString(Constants.USER_AGENT);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setJavaScriptEnabled(true);

        mWebViewAliSDK.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                injectJavaScriptFunction();
            }
        });

        WebAppInterface webAppInterface = new WebAppInterface(this);
        webAppInterface.setSlideCallback(new WebAppInterface.CallbackData() {
            @Override
            public void onSlideSuccess(String data) {
                Log.d("webtest", "onSlideSuccess data: " + data);

                Toast.makeText(MainActivity.this, "onSlideSuccess: " + data, Toast.LENGTH_LONG).show();
            }
        });

        // 建立JavaScript调用Java接口的桥梁。
        mWebViewAliSDK.addJavascriptInterface(webAppInterface, Constants.JAVASCRIPT_OBJ);

        mWebViewAliSDK.loadUrl(Constants.ALI_SDK_URL);

//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
    }

    private void injectJavaScriptFunction() {
        mWebViewAliSDK.loadUrl(
                "javascript: " +
                        "window.androidObj.tokenToAndroid = function(message) { " +
                        Constants.JAVASCRIPT_OBJ + ".getSlideData(message) }"
        );
    }

    public void onBackPressed() {
        if (mWebViewTest.getUrl().contains("index.html")) {
            super.onBackPressed();
        } else {
            mWebViewTest.goBack();
        }
    }
}
