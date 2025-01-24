package me.longluo.audio;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import me.longluo.audio.util.ThemeUtil;


public class Settings {

    private static final String THEME_DEFAULT = ThemeUtil.SYSTEM;

    private static final long SAMPLE_RATE_DEFAULT = 48000;

    private long sampleRate;

    private String theme;

    private final SharedPreferences sp;

    public Settings(SharedPreferences sp) {
        this.sp = sp;
        setTheme(sp.getString(Keys.theme, THEME_DEFAULT));
        setSampleRate(sp.getLong(Keys.sampleRate, SAMPLE_RATE_DEFAULT));
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setSampleRate(String sampleRate) {
        try {
            this.sampleRate = Long.parseLong(sampleRate);
        } catch (Exception e) {
            this.sampleRate = SAMPLE_RATE_DEFAULT;
        }
    }

    public long getSampleRate() {
        return sampleRate;
    }

    @SuppressLint("ApplySharedPref")
    void save() {
        sp.edit()
                .putString(Keys.theme, getTheme())
                .putLong(Keys.sampleRate, getSampleRate())
                .commit();
    }
}