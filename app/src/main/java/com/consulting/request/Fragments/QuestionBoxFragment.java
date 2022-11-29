package com.consulting.request.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.consulting.request.Core.Question;
import com.consulting.request.Core.QuestionBox;
import com.consulting.request.Core.QuestionType;
import com.consulting.request.Popup.DatePickerPopup;
import com.consulting.request.R;
import com.consulting.request.RequestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class QuestionBoxFragment extends Fragment {
    private boolean isStudent = false;

    private JSONObject questionAnswer = new JSONObject();

    private int backgroundColor = Color.WHITE;
    private int foregroundColor = 0;
    private String uri;

    // ActivityResult ( for date pickup )
    private String ar_title;
    private TextView ar_tv;
    private Question ar_qu;
    private ActivityResultLauncher<Intent> ar_launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getData() == null) return;

                    int year = result.getData().getIntExtra("year", -1);
                    int month = result.getData().getIntExtra("month", -1) + 1;
                    int day = result.getData().getIntExtra("day", -1);

                    ar_tv.setText(year + "-" + month + "-" + day);
                    ar_qu.setValue(year + "-" + month + "-" + day);
                    try {
                        questionAnswer.put(ar_title, ar_qu.getValue());
                    } catch (JSONException e) {}
                }
            }
    );

    private Context ctx;
    public QuestionBoxFragment(Context ctx, boolean isStudent) {
        try {
            this.ctx = ctx;
            this.isStudent = isStudent;
            questionAnswer.put("__email__", PreferenceManager.getDefaultSharedPreferences(ctx).getString("email", "hehe"));

            if (!isStudent) { // Parents
                TypedValue typedValue = new TypedValue();
                ctx.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
                foregroundColor = typedValue.data;
                backgroundColor = Color.TRANSPARENT;

                uri = "http://goalsdhkdwk.cafe24app.com/api/request/parentApply";
            } else { // Student
                backgroundColor = Color.argb(177, 51, 132, 255);
                foregroundColor = Color.WHITE;

                uri = "http://goalsdhkdwk.cafe24app.com/api/request/studentApply";
            }
        } catch (Exception _) {}
    }

    private int dp(int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
    }

    public void addQuestionOnLayout(View view, QuestionBox box) {
        ArrayList<Question> questionList = box.getQuestionList();
        LinearLayout l1 = new LinearLayout(ctx);
        l1.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable border = new GradientDrawable();
        border.setColor(backgroundColor);
        border.setStroke(2, backgroundColor);
        border.setCornerRadius(20.0f);
        l1.setBackground(border);

        LinearLayout.LayoutParams l1p = new LinearLayout.LayoutParams(-2, -2);
        l1p.setMargins(dp(17), dp(17), dp(17), dp(17));
        l1p.width = LinearLayout.LayoutParams.MATCH_PARENT;
        TextView tv = new TextView(ctx);
        tv.setLayoutParams(l1p);
        tv.setText(box.getTitle());
        tv.setTextColor(foregroundColor);
        tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.neom));
        tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, ctx.getResources().getDisplayMetrics()));
        l1.addView(tv);

        RadioGroup rg = new RadioGroup(ctx);
        rg.setLayoutParams(l1p);
        for (Question q : questionList) {
            RadioButton rb = new RadioButton(ctx);

            if(android.os.Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][] {
                                        new int[]{-android.R.attr.state_enabled}, // Disabled
                                        new int[]{android.R.attr.state_enabled}   // Enabled
                                },
                        new int[] {
                                        foregroundColor, // disabled
                                        foregroundColor   // enabled
                                }
                );

                rb.setButtonTintList(colorStateList); // set the color tint list
                rb.invalidate(); // Could not be necessary
            }

            rb.setText(q.getText());
            rb.setTypeface(ResourcesCompat.getFont(ctx, R.font.neob));
            rb.setTextColor(foregroundColor);
            rg.addView(rb);
        }

        HashSet<Integer> specified = new HashSet<>();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    RadioButton radioButton = group.findViewById(checkedId);
                    int idx = group.indexOfChild(radioButton);
                    String text = radioButton.getText().toString();

                    Question question = box.getQuestion(text);
                    QuestionType type = question.getType();

                    switch (type) {
                        case SELECT:
                            questionAnswer.put(box.getTitle(), question.getText());
                            break;
                        case INPUT:
                            if (specified.contains(checkedId)) {
                                questionAnswer.put(box.getTitle(), question.getValue());
                                break;
                            }

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            EditText ed = new EditText(ctx);
                            ed.setTextSize(dp(6));
                            ed.setTextColor(foregroundColor);
                            ed.setTypeface(ResourcesCompat.getFont(ctx, R.font.neob));
                            ed.setLayoutParams(lp);
                            ed.setHint("여기에 입력해주세요!");
                            ed.getBackground().mutate().setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP);
                            ed.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    rg.check(checkedId);
                                    question.setValue(charSequence.toString());
                                    try {
                                        questionAnswer.put(box.getTitle(), question.getValue());
                                    } catch (JSONException e) {}
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {}
                            });
                            rg.addView(ed, idx + 1);
                            specified.add(checkedId);
                            break;
                        case DATE:
                            if (specified.contains(checkedId)) {
                                questionAnswer.put(box.getTitle(), question.getValue());
                                break;
                            }

                            TextView tv = new TextView(ctx);
                            tv.setText("글을 클릭해서 날짜를 선택해주세요!");
                            tv.setTextColor(foregroundColor);
                            tv.setPadding(dp(7), dp(7), dp(7), dp(7));
                            tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.neom));
                            tv.setTextSize(dp(6));
                            tv.setOnClickListener(e -> {
                                Intent i = new Intent(ctx, DatePickerPopup.class);
                                ar_tv = tv;
                                ar_qu = question;
                                ar_title = box.getTitle();
                                ar_launcher.launch(i);
                            });
                            rg.addView(tv, idx + 1);
                            specified.add(checkedId);
                            break;
                    }
                } catch (Exception e) {}
            }
        });

        l1.addView(rg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(12), 0, 0);

        l1.setLayoutParams(lp);

        l1.setAlpha(0);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                l1.setAlpha(interpolatedTime);
            }
        };
        animation.setDuration(500);
        l1.startAnimation(animation);

        ((LinearLayout)view).addView(l1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_box, container, false);

        ((TextView)layout.findViewById(R.id.textview_typeBox)).setText(isStudent ? "🎓 학생용" : "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 학부모용");

        // TODO

        new Thread(() -> {
            try {
                String url = isStudent ? "http://goalsdhkdwk.cafe24app.com/api/get/studentQuestionList" : "http://goalsdhkdwk.cafe24app.com/api/get/parentQuestionList";
                String result = Jsoup.connect(url)
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .ignoreContentType(true)
                        .get()
                        .text();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    String title = json.getString("title");
                    JSONArray arr = json.getJSONArray("questions");
                    QuestionBox qb = new QuestionBox(title);
                    for (int j = 0; j < arr.length(); j++) {
                        JSONObject json1 = arr.getJSONObject(j);
                        String name = json1.getString("question");
                        String type = json1.getString("type");

                        if (type.equals("select"))
                            qb.addQuestion(new Question(name, QuestionType.SELECT));
                        else if (type.equals("input"))
                            qb.addQuestion(new Question(name, QuestionType.INPUT));
                        else if (type.equals("date"))
                            qb.addQuestion(new Question(name, QuestionType.DATE));
                    }
                    ((Activity)ctx).runOnUiThread(() -> {
                        addQuestionOnLayout(layout, qb);
                    });
                }
                ((Activity)ctx).runOnUiThread(() -> {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.RIGHT;

                    Button button = new Button(ctx);
                    button.setText("신청하기");
                    button.setTextColor(Color.WHITE);
                    button.setLayoutParams(lp);
                    button.getBackground().mutate().setColorFilter(Color.rgb(77, 142, 255), PorterDuff.Mode.SRC);
                    button.setOnClickListener(e -> {
                        button.setEnabled(false);
                        new Thread(() -> {
                            try {
                                String requestResult = Jsoup.connect(uri)
                                        .header("content-type", "application/json")
                                        .header("accpet", "application/json")
                                        .ignoreContentType(true)
                                        .requestBody(questionAnswer.toString())
                                        .post()
                                        .text();
                                if (requestResult.contains("false")) {
                                    // Error
                                    ((Activity)ctx).runOnUiThread(() -> {
                                        Toast.makeText(ctx, requestResult, Toast.LENGTH_SHORT).show();
                                        button.setEnabled(true);
                                    });
                                } else {
                                    ((Activity)ctx).runOnUiThread(() -> {
                                        Toast.makeText(ctx, "상담을 성공적으로 신청하였습니다.", Toast.LENGTH_SHORT).show();
                                        ((RequestActivity)ctx).toMainActivity();
                                    });
                                }
                            } catch (Exception _) {
                                ((Activity)ctx).runOnUiThread(() -> {
                                    Toast.makeText(ctx, "상담을 신청하는 과정에서 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }).start();
                    });
                    layout.addView(button);
                });
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity)ctx).runOnUiThread(() -> {
                    Toast.makeText(ctx, "질문 목록을 가져오는데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
        return layout;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
