package com.consulting.request.Popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;

import com.consulting.request.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerPopup extends Activity {

    private int year, month, day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.date_picker);

        Calendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);

        Button btn = findViewById(R.id.date_ok);
        btn.setOnClickListener(e -> {
            Intent i = new Intent();

            i.putExtra("year", year);
            i.putExtra("month", month);
            i.putExtra("day", day);
            setResult(1, i);
            finish();
        });

        DatePicker dp = findViewById(R.id.datePicker);
        dp.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1;
                day = i2;
            }
        });
    }
}
