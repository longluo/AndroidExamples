package com.longluo.demo.dialog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.longluo.demo.R;


public class DialogActivity extends AppCompatActivity {

    private NotificationWebViewDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        showWebViewDialog("https://www.eiken.or.jp/eiken/apply/");
    }

    private void showWebViewDialog(String webUrl) {
        dialog = new NotificationWebViewDialog(this, webUrl);
        dialog.show();
    }
}