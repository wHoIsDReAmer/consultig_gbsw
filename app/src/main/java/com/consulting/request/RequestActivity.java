package com.consulting.request;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.consulting.request.Fragments.QuestionBoxFragment;
import com.consulting.request.Fragments.QuestionSelect;
import com.google.android.material.navigation.NavigationView;

public class RequestActivity extends AppCompatActivity {
    private FragmentManager fmanager;

    private Context ctx = this;

    private boolean isHome = true;
    private long back_time = 0;

    private ImageView btn_menu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
            return;
        }

        if (!isHome) {
            isHome = true;
            fmanager.beginTransaction()
                    .setCustomAnimations(R.anim.to_left, R.anim.from_left)
                    .replace(R.id.questionBox, new QuestionSelect(this))
                    .commit();
            return;
        }

        if (System.currentTimeMillis() - back_time > 2000) {
            Toast.makeText(this, "한번 더 누르면 종료됩니다!", Toast.LENGTH_SHORT).show();
            back_time = System.currentTimeMillis();
        } else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        fmanager = getSupportFragmentManager();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getSupportActionBar().hide();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btn_recent:
                        Intent intent = new Intent(ctx, RecentActivity.class);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);

        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(e -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });

        fmanager.beginTransaction().replace(R.id.questionBox, new QuestionSelect(this)).commit();

        Intent i = new Intent(this, MailActivity.class);
        startActivity(i);
    }

    public void toQuestionFragment(boolean isStudent) {
        fmanager.beginTransaction()
                .setCustomAnimations(R.anim.to_left, R.anim.from_left)
                .replace(R.id.questionBox, new QuestionBoxFragment(this, isStudent))
                .commit();
        isHome = false;
    }
}