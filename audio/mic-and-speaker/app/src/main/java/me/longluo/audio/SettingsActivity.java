package me.longluo.audio;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import me.longluo.audio.util.GuiUtil;
import me.longluo.audio.util.SetGetter;
import me.longluo.audio.util.ThemeUtil;


public class SettingsActivity extends Activity {

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
        setContentView(R.layout.activity_settings);

        EditText etSampleRate = findViewById(R.id.et_sample_rate);
        GuiUtil.link(etSampleRate, new SetGetter() {
            @Override
            public void set(String value) {
                settings.setSampleRate(value);
            }

            @Override
            public String get() {
                return "" + settings.getSampleRate();
            }
        });

        RadioGroup rgTheme = findViewById(R.id.rg_theme);

        RadioButton rb;
        rb = findViewById(R.id.rb_theme_light);
        rb.setText(ThemeUtil.LIGHT);
        rb = findViewById(R.id.rb_theme_dark);
        rb.setText(ThemeUtil.DARK);
        rb = findViewById(R.id.rb_theme_system);
        rb.setText(ThemeUtil.SYSTEM);

        GuiUtil.link(rgTheme, new SetGetter() {
            @Override
            public void set(String value) {
                boolean mustRecreate = !settings.getTheme().equals(value);
                settings.setTheme(value);
                if (mustRecreate) {
                    settings.save();
                    recreate();
                }
            }

            @Override
            public String get() {
                return settings.getTheme();
            }
        });
    }

    @Override
    public void onBackPressed() {
        settings.save();

        setResult(RESULT_OK);
        super.onBackPressed();
        super.finish();
    }
}