package com.consulting.request;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RecentActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        getSupportActionBar().hide();
    }

}
