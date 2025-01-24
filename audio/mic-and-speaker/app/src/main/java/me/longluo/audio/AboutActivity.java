package me.longluo.audio;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.widget.TextView;

import me.longluo.noisoid.Noisoid;
import me.longluo.audio.util.ThemeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AboutActivity extends Activity {

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = new Settings(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE));
        initGUI();
    }

    private void initGUI() {
        // we use our own title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ThemeUtil.set(this, settings.getTheme());
        setContentView(R.layout.activity_about);

        setAppVersion(BuildConfig.VERSION_NAME, BuildConfig.TIMESTAMP);

        TextView tv;
        String s;

        tv = findViewById(R.id.tv_dev_website);
        s = tv.getText().toString();
        s = "<a href=\"" + s + "\">" + s + "</a>";
        setAsLink(tv, s);

        tv = findViewById(R.id.tv_insta_page);
        s = tv.getText().toString();
        s = "<a href=\"https://instagram.com/" + s.substring(1) + "\">" + s + "</a>";
        setAsLink(tv, s);
        findViewById(R.id.ll_insta_page).setOnClickListener(
                (view) -> findViewById(R.id.tv_insta_page).performClick());

        tv = findViewById(R.id.tv_youtube_channel);
        s = tv.getText().toString();
        s = "<a href=\"https://youtube.com/" + s + "\">" + s + "</a>";
        setAsLink(tv, s);
        findViewById(R.id.ll_youtube_channel).setOnClickListener(
                (view) -> findViewById(R.id.tv_youtube_channel).performClick());

        tv = findViewById(R.id.tv_github);
        s = tv.getText().toString();
        s = "<a href=\"https://github.com/" + s + "\">" + s + "</a>";
        setAsLink(tv, s);
        findViewById(R.id.ll_github).setOnClickListener(
                (view) -> findViewById(R.id.tv_github).performClick());

        tv = findViewById(R.id.tv_app_uses_libs);
        s = tv.getText().toString();
        s += "\n" + Noisoid.about();
        tv.setText(s);
    }

    @SuppressWarnings("SameParameterValue")
    private void setAppVersion(String versionName, long timeStamp) {
        TextView tvAppVersion = findViewById(R.id.tv_app_version);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        Date buildDate = new Date(timeStamp);
        String s = "v" + versionName + " (" + dateFormat.format(buildDate) + ")";
        tvAppVersion.setText(s);
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHTML(String s) {
        if (Build.VERSION.SDK_INT < 24) { return Html.fromHtml(s); }
        // else:
        return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
    }

    private void setAsLink(TextView tv, String s) {
        tv.setText(fromHTML(s));
        tv.setClickable(true);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        //tv.setLinkTextColor(getResources().getColor(an));
    }
}