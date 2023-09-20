package com.longluo.demo.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.longluo.demo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialogActivity extends AppCompatActivity {

    //TextInputLayout til_edit_start;
    Button btn_show_date_picker_dialog;
    Calendar calendar = Calendar.getInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker_dialog);
        btn_show_date_picker_dialog = findViewById(R.id.btn_show_date_picker_dialog);
        btn_show_date_picker_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true, DatePickerDialogActivity.this, R.style.DatePickerDialogStyle, calendar);
            }
        });
    }


    public void showDatePickerDialog(Boolean isStartDateEdit, Context context, int themeResId, final Calendar calendar) {
        DatePickerDialog.OnDateSetListener listener = (view, year, monthOfYear, dayOfMonth) -> {
            String month = monthOfYear < 9 ? "0" + (monthOfYear + 1) : (monthOfYear + 1) + "";
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
            String dayStr = year + "/" + month + "/" + day;
            Date searchStart;
            Date searchEnd;
            //固定フォーマット
            String dateFormat = "yyyy/MM/dd";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, themeResId, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> datePickerDialog.dismiss());
        datePickerDialog.show();
    }
}