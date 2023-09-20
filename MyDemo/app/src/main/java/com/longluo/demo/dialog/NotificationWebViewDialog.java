package com.longluo.demo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.longluo.demo.R;


public class NotificationWebViewDialog extends Dialog implements View.OnClickListener {

    private View contentView;
    private Activity context;
    private ImageView img_close;
    private WebView webView;
    private String webUrl = "";

    public NotificationWebViewDialog(Activity _context, String webUrl) {
        super(_context, R.style.Dialog_Fullscreen);
        context = _context;
        this.webUrl = webUrl;
        contentView = LayoutInflater.from(context).inflate(R.layout.notification_webview_dialog, null);
        this.setContentView(contentView);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ((FrameLayout.LayoutParams) contentView.getLayoutParams()).leftMargin = 50;
        ((FrameLayout.LayoutParams) contentView.getLayoutParams()).rightMargin = 50;
        ((FrameLayout.LayoutParams) contentView.getLayoutParams()).topMargin = 30;
        ((FrameLayout.LayoutParams) contentView.getLayoutParams()).bottomMargin = 30;
        webView = contentView.findViewById(R.id.webview);
        img_close = contentView.findViewById(R.id.img_close);
        img_close.setOnClickListener(this);
        webView.loadUrl(webUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startBrowser(context, url);
                closeDialog();
                return true;
            }
        });
    }

    /**
     * 外部ブラウザを開く
     *
     * @param context 環境
     * @param url
     */
    private void startBrowser(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        try {
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * ダイアログを閉じる
     */
    private void closeDialog() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                closeDialog();
                break;
        }
    }
}
