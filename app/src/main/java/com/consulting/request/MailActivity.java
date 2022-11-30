package com.consulting.request;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;

import java.util.concurrent.atomic.AtomicBoolean;

public class MailActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;

    private Button btn_send;
    private TextView message;
    private EditText edit_mail;
    private EditText edit_code;

    @Override
    public void onBackPressed() {
        // ignore
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        getSupportActionBar().hide();

        AtomicBoolean isMailSend = new AtomicBoolean(false);

        edit_mail = findViewById(R.id.edit_mail);
        edit_code = findViewById(R.id.edit_code);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(e -> {
            if (!isMailSend.get()) {
                edit_mail.setVisibility(View.GONE);
                edit_code.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        String result = Jsoup.connect("http://goalsdhkdwk.cafe24app.com/api/mail/certify")
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .ignoreContentType(true)
                                .requestBody("{\"mail\":\"" + edit_mail.getText().toString() + "\"}")
                                .post()
                                .toString();

                        runOnUiThread(() -> {
                            message.setText(Html.fromHtml("<u>메일이 오지 않았나요? 클릭. </u>"));

                            if (result.contains("true"))
                                btn_send.setText("인증");
                            else {
                                if (result.contains("1"))
                                    Toast.makeText(this, "조금 있다가 다시 요청해주세요.", Toast.LENGTH_SHORT).show();
                                if (result.contains("3"))
                                    Toast.makeText(this, "다시 인증해주세요!", Toast.LENGTH_SHORT).show();
                                if (result.contains("4"))
                                    Toast.makeText(this, "인증코드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception ignore) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                            edit_mail.setVisibility(View.VISIBLE);
                            edit_code.setVisibility(View.GONE);
                        });
                        ignore.printStackTrace();
                        isMailSend.set(false);
                    }
                }).start();
            } else {
                new Thread(() -> {
                    try {
                        String result = Jsoup.connect("http://goalsdhkdwk.cafe24app.com/api/mail/checkCode")
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .ignoreContentType(true)
                                .requestBody("{\"mail\":\"" + edit_mail.getText().toString() + "\", \"mailCode\": \"" + edit_code.getText().toString() + "\"}")
                                .post()
                                .toString();

                        if (result.contains("true")) {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "인증 성공! 서비스 이용 가능합니다.", Toast.LENGTH_SHORT).show();
                            });
                            sharedPreferences.edit().putString("email", edit_mail.getText().toString()).apply();
                            finish();
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "인증 실패..", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception ignore) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                            edit_mail.setVisibility(View.VISIBLE);
                            edit_code.setVisibility(View.GONE);
                        });
                    }
                }).start();
            }
            isMailSend.set(!isMailSend.get());
        });

        message = findViewById(R.id.mail_message);
        message.setOnClickListener(e -> {
            btn_send.setText("보내기");
            edit_mail.setVisibility(View.VISIBLE);
            edit_code.setVisibility(View.GONE);
            message.setText("");
            isMailSend.set(false);
        });
    }
}
