package com.longluo.demo.spinner.spinnerday;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.longluo.demo.R;

import java.util.ArrayList;
import java.util.Calendar;

public class SpinnerDayActivity extends AppCompatActivity {

    private Spinner spYear;
    private Spinner spMonth;
    private Spinner spDay;
    private Button btnSpinnerSure;
    private ArrayList<String> dataYear = new ArrayList<String>();
    private ArrayList<String> dataMonth = new ArrayList<String>();
    private ArrayList<String> dataDay = new ArrayList<String>();
    private ArrayAdapter<String> adapterSpYear;
    private ArrayAdapter<String> adapterSpMonth;
    private ArrayAdapter<String> adapterSpDay;
    //选中年的index
    private int yearSelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_day);
        initView();
    }

    public void initView() {
        spYear = findViewById(R.id.sp_year);
        spMonth = findViewById(R.id.sp_month);
        spDay = findViewById(R.id.sp_day);

        btnSpinnerSure = findViewById(R.id.btn_spinner_sure);

        //制造数据源
        makeDateData();

        //年适配
        adapterSpYear = new ArrayAdapter<String>(this, R.layout.spinner_date_item, dataYear);
        adapterSpYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(adapterSpYear);
        spYear.setSelection(yearSelection);// 默认选中今年

        //月适配
        adapterSpMonth = new ArrayAdapter<String>(this, R.layout.spinner_date_item, dataMonth);
        adapterSpMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(adapterSpMonth);

        //日适配
        adapterSpDay = new ArrayAdapter<String>(this, R.layout.spinner_date_item, dataDay);
        adapterSpDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDay.setAdapter(adapterSpDay);

        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dataDay.clear();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.valueOf(spYear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, arg2);
                int dayofm = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= dayofm; i++) {
                    dataDay.add("" + (i < 10 ? "0" + i : i));
                }
                adapterSpDay.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        btnSpinnerSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SpinnerDayActivity.this, spYear.getSelectedItem() + "," + spMonth.getSelectedItem() + "," + spMonth.getSelectedItem(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 设置spinner数据源
     */
    public void makeDateData() {
        Calendar cal = Calendar.getInstance();
        int tempYearSelect = 0;
        // 年份设定为1921 - 2122
        for (int i = 1921; i < 2122; i++) {
            dataYear.add(i + "");
            if (i == cal.get(Calendar.YEAR)) {
                yearSelection = tempYearSelect;
            }
            tempYearSelect++;
        }
        // 12个月
        for (int i = 1; i <= 12; i++) {
            dataMonth.add("" + (i < 10 ? "0" + i : i));
        }
    }
}