package com.consulting.request;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.consulting.request.Core.Question;
import com.consulting.request.Fragments.QuestionBoxFragmentParents;
import com.consulting.request.Fragments.QuestionBoxFragmentStudent;
import com.consulting.request.Fragments.QuestionSelect;

public class RequestActivity extends AppCompatActivity {
    private FragmentManager fmanager;

    private boolean isHome = true;
    private long back_time = 0;

    @Override
    public void onBackPressed() {
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

        fmanager.beginTransaction().replace(R.id.questionBox, new QuestionSelect(this)).commit();
    }

    public void toQuestionFragmentParents() {

        fmanager.beginTransaction()
                .setCustomAnimations(R.anim.to_left, R.anim.from_left)
                .replace(R.id.questionBox, new QuestionBoxFragmentParents(this))
                .commit();
        isHome = false;
    }

    public void toQuestionFragmentStudent() {
        fmanager.beginTransaction()
                .setCustomAnimations(R.anim.to_left, R.anim.from_left)
                .replace(R.id.questionBox, new QuestionBoxFragmentStudent(this))
                .commit();
        isHome = false;
    }
}