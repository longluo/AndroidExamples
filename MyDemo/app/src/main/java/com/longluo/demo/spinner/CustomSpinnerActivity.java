package com.longluo.demo.spinner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.longluo.demo.R;
import com.longluo.demo.spinner.widget.SpinnerTextView;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerActivity extends AppCompatActivity {

    private SpinnerTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_spinner);

        textView = findViewById(R.id.custom_text);
        //设置数据源
        textView.setDataList(getSpinnerLeftData());
        textView.setOnItemSelectListener(new SpinnerTextView.OnItemSelectListener() {
            @Override
            public void OnItemSelect(int position, String text) {
                //选中回调TODO

            }
        });
    }

    private List<String> getSpinnerLeftData() {
        List listLeft = new ArrayList<String>();
        listLeft.add("W");
        listLeft.add("99");
        return listLeft;
    }
}