package com.consulting.request;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.consulting.request.Core.ConsultingResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.w3c.dom.Text;

import java.util.concurrent.atomic.AtomicBoolean;

public class RecentActivity extends AppCompatActivity {
    private SwipeRefreshLayout layout_swipe;
    private LinearLayout layout_body;

    private float dp(float value) {
        return getResources().getDisplayMetrics().scaledDensity * value;
    }

    private int dpi(float value) {
        return (int) (getResources().getDisplayMetrics().scaledDensity * value + 0.5f);
    }

    private final ConsultingResult.Result[] types = {ConsultingResult.Result.WAITING, ConsultingResult.Result.SUCCESS, ConsultingResult.Result.DENY};

    private void addRecentOnLayout(LinearLayout view, ConsultingResult res) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gd = new GradientDrawable();
        if (res.getType() == ConsultingResult.Result.WAITING)
            gd.setColor(Color.rgb(94, 94, 94));
        else if (res.getType() == ConsultingResult.Result.SUCCESS)
            gd.setColor(Color.rgb(51, 255, 122));
        else gd.setColor(Color.rgb(255, 81, 51));
        gd.setCornerRadius(dp(5));
        layout.setBackground(gd);

        LinearLayout inner = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inner.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv1 = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpi(10), dpi(10), dpi(10), dpi(10));
        tv1.setLayoutParams(lp);
        tv1.setText(res.getType().getTitle());
        tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv1.setTextColor(Color.WHITE);
        tv1.setTypeface(ResourcesCompat.getFont(this, R.font.neob));
        inner.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(res.getDate());
        tv2.setTypeface(ResourcesCompat.getFont(this, R.font.neob));
        tv2.setTextColor(Color.WHITE);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv2.setLayoutParams(lp);
        inner.addView(tv2);

        LinearLayout.LayoutParams lp_img = new LinearLayout.LayoutParams(dpi(20), dpi(20));
        lp_img.setMargins(0, dpi(10), 0, 0);
        ImageView iv = new ImageView(this);
        iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.bottom_arrow, getTheme()));
        iv.setLayoutParams(lp_img);
        inner.addView(iv);
        layout.addView(inner);

        LinearLayout foot = new LinearLayout(this);
        foot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        foot.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams __lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        __lp.setMargins(dpi(10), dpi(10), dpi(10), dpi(10));
        TextView tv_info = new TextView(this);
        tv_info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        tv_info.setTextColor(Color.rgb(211, 211, 211));
        tv_info.setLayoutParams(__lp);
        tv_info.setTypeface(ResourcesCompat.getFont(this, R.font.neob));
        tv_info.setText("이름: " + res.getName() + "\n학번: " + res.getSid() + "\n상담 종류: " + res.getConsultingType() + "\n학부모 여부: " + (res.isParents() ? "O" : "X"));
        foot.addView(tv_info);

        LinearLayout foot_right = new LinearLayout(this);
        foot_right.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        foot_right.setOrientation(LinearLayout.HORIZONTAL);
        foot_right.setGravity(Gravity.RIGHT);

        LinearLayout.LayoutParams ___lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ___lp.setMargins(dpi(10), 0, 0, dpi(10));

        TextView tv_msg = new TextView(this);
        tv_msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        tv_msg.setTypeface(ResourcesCompat.getFont(this, R.font.neom));
        tv_msg.setTextColor(Color.WHITE);
        tv_msg.setLayoutParams(___lp);
        if (res.getType() == ConsultingResult.Result.SUCCESS) {
            tv_msg.setText("선생님의 메시지 : " + res.getMessage());
            foot_right.addView(tv_msg);
        } else if (res.getType() == ConsultingResult.Result.DENY) {
            tv_msg.setText("선생님의 메시지 : " + res.getMessage());
            foot_right.addView(tv_msg);
        }

        foot.addView(foot_right);
        foot.setVisibility(View.GONE);

        layout.addView(foot);

        AtomicBoolean isOpen = new AtomicBoolean(false);
        foot.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final float footHeight = foot.getMeasuredHeight();
        Animation open = new Animation() {
            @Override
            protected void applyTransformation(float iT, Transformation t) {
                foot.getLayoutParams().height = iT == 1 ?
                        -2 : (int) (footHeight * iT);
                foot.requestLayout();
            }
        };
        open.setDuration(300);

        Animation close = new Animation() {
            @Override
            protected void applyTransformation(float iT, Transformation t) {
                if (iT == 1)
                    foot.setVisibility(View.GONE);
                else
                    foot.getLayoutParams().height = (int) footHeight - (int) (iT * footHeight);
                foot.requestLayout();
            }
        };
        close.setDuration(300);

        layout.setOnClickListener(e -> {
            if (!isOpen.get()) {
                foot.setVisibility(View.VISIBLE);
                foot.getLayoutParams().height = 1;
                foot.startAnimation(open);
            } else {
                foot.setVisibility(View.VISIBLE);
                foot.startAnimation(close);
            }

            isOpen.set(!isOpen.get());
        });

        LinearLayout.LayoutParams _lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _lp.setMargins(0, dpi(16), 0, 0);
        layout.setLayoutParams(_lp);

        view.addView(layout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        getSupportActionBar().hide();

        layout_swipe = findViewById(R.id.layout_swipe);
        layout_body = findViewById(R.id.layout_body);

        ConsultingResult.Result type = types[getIntent().getIntExtra("type", 0)];

        new Thread(() -> {
            try {
                String result = Jsoup.connect("http://goalsdhkdwk.cafe24app.com/api/get/studentRequestList")
                        .header("content-type", "application/json")
                        .header("accept", "application/json")
                        .ignoreContentType(true)
                        .requestBody("{\"mail\": \"" + RequestActivity.sharedPreferences.getString("email", "hehe") + "\"}")
                        .post()
                        .text();
                JSONObject json = new JSONObject(result);
                JSONArray json2 = null;

                if (type == ConsultingResult.Result.WAITING)
                    json2 = json.getJSONArray("waiting");
                else if (type == ConsultingResult.Result.SUCCESS)
                    json2 = json.getJSONArray("success");
                else
                    json2 = json.getJSONArray("deny");

                for (int i = 0; i < json2.length(); i++) {
                    JSONObject _json = json2.getJSONObject(i);
                    String time = _json.getString("time");
                    String date = _json.getString("date");
                    String name = _json.getString("student_name");
                    String sid = _json.getString("student_number");
                    String consultingType = _json.getString("type");
                    String message = String.valueOf(_json.getString("teacher_suggest"));
                    boolean is_parent = _json.getBoolean("is_parent");

                    ConsultingResult res = new ConsultingResult(type);
                    res.setDate(date + " " + time);
                    res.setName(name);
                    res.setSid(sid);
                    res.setConsultingType(consultingType);
                    res.setMessage(message);
                    res.setParents(is_parent);
                    runOnUiThread(() -> {
                        addRecentOnLayout(layout_body, res);
                    });
                }
            } catch (Exception e) {

            }
        }).start();
    }
}
