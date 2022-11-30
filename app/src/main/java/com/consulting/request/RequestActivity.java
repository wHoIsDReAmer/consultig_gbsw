package com.consulting.request;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.consulting.request.Fragments.QuestionBoxFragment;
import com.consulting.request.Fragments.QuestionSelect;
import com.consulting.request.Services.MainService;
import com.consulting.request.Services.MainWorker;
import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;

public class RequestActivity extends AppCompatActivity {
    private FragmentManager fmanager;
    public static SharedPreferences sharedPreferences;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getSupportActionBar().hide();

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent(ctx, RecentActivity.class);
                switch (item.getItemId()) {
                    case R.id.btn_recent_waiting:
                        intent.putExtra("type", 0);
                        startActivity(intent);
                        break;
                    case R.id.btn_recent_success:
                        intent.putExtra("type", 1);
                        startActivity(intent);
                        break;
                    case R.id.btn_recent_deny:
                        intent.putExtra("type", 2);
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

        MainWorker.ctx = this;

        Intent s = new Intent(this, MainService.class);
        startService(s);

        if (sharedPreferences.getString("email", "hehe").equals("hehe")) {
            MailActivity.sharedPreferences = sharedPreferences;
            Intent i = new Intent(this, MailActivity.class);
            startActivity(i);
        } else {
            new Thread(() -> {
                try {
                    String result = Jsoup.connect("http://goalsdhkdwk.cafe24app.com/api/mail/checkValid")
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .ignoreContentType(true)
                            .requestBody("{\"mail\":\"" + sharedPreferences.getString("email", "") + "\"}")
                            .post()
                            .toString();
                    if (result.contains("false")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "이메일 정보가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        });
                        sharedPreferences.edit().remove("email").apply();
                        Intent i = Intent.makeRestartActivityTask(getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent());
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "서버에 연결할 수 없거나 알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void toQuestionFragment(boolean isStudent) {
        fmanager.beginTransaction()
                .setCustomAnimations(R.anim.to_left, R.anim.from_left)
                .replace(R.id.questionBox, new QuestionBoxFragment(this, isStudent))
                .commit();
        isHome = false;
    }

    public void toMainActivity() {
        if (!isHome) {
            isHome = true;
            fmanager.beginTransaction()
                    .setCustomAnimations(R.anim.to_left, R.anim.from_left)
                    .replace(R.id.questionBox, new QuestionSelect(this))
                    .commit();
            return;
        }
    }
}