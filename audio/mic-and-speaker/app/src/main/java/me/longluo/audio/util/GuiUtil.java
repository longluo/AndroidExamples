package me.longluo.audio.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;


public class GuiUtil {

    static public void setOnClickListenerToAllButtons(ViewGroup vg, View.OnClickListener ocl) {
        int n = vg.getChildCount();
        View v;
        for (int i = 0; i < n; i++) {
            v = vg.getChildAt(i);
            if (v instanceof ViewGroup) {
                setOnClickListenerToAllButtons((ViewGroup) v, ocl);
            } else if (v instanceof Button) {
                v.setOnClickListener(ocl);
            } else if (v instanceof ImageButton) {
                v.setOnClickListener(ocl);
            }
        }
    }

    static public void link(@NonNull EditText et, @NonNull final SetGetter setGetter) {

        // first time (init)
        // get value from settings and set on et
        String value = setGetter.get();
        if (value != null) {
            et.setText(value);
        }
        setGetter.set(value);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                setGetter.set(e.toString());
            }
        });

    }

    static public void link(@NonNull CheckBox cb, @NonNull final SetGetter setGetter) {
        try {
            boolean b = Boolean.parseBoolean(setGetter.get());
            cb.setChecked(b);
            setGetter.set("" + b);
            cb.setOnCheckedChangeListener((compoundButton, b1) -> setGetter.set("" + b1));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    static public void link(@NonNull SeekBar sb, @NonNull final SetGetter setGetter) {
        try {
            int val = Integer.parseInt(setGetter.get());
            sb.setProgress(val);
            setGetter.set("" + val);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    setGetter.set("" + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void link(@NonNull RadioButton rb, @NonNull final SetGetter setGetter) {
        try {
            boolean b = Boolean.parseBoolean(setGetter.get());
            rb.setChecked(b);
            setGetter.set("" + b);
            rb.setOnCheckedChangeListener((compoundButton, b1) -> setGetter.set("" + b1));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void link(@NonNull RadioGroup rg, @NonNull final SetGetter setGetter) {

        String current = setGetter.get();

        int n = rg.getChildCount();
        RadioButton rb;
        for (int i = 0; i < n; i++) {
            rb = (RadioButton) rg.getChildAt(i);
            if (rb.getText().toString().equals(current)) {
                rb.setChecked(true);
                break;
            }
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb1 = group.findViewById(checkedId);
            setGetter.set(rb1.getText().toString());
        });
    }
}