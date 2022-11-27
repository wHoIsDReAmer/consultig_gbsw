package com.consulting.request;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MailActivity extends AppCompatActivity {

    Button btn_send;
    TextView message;

    @Override
    public void onBackPressed() {
        // ignore
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        getSupportActionBar().hide();

        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(e -> {
            btn_send.setEnabled(false);
            btn_send.setTextColor(Color.GRAY);
            message.setText(Html.fromHtml("<u>메일이 오지 않았나요? 클릭. </u>"));
        });

        message = findViewById(R.id.mail_message);
        message.setOnClickListener(e -> {
            btn_send.setEnabled(true);
            btn_send.setTextColor(Color.WHITE);
            message.setText("");
        });
    }
}
