package com.consulting.request.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class QuestionBoxFragment extends Fragment {
    private boolean isStudent = false;

    private HashMap<String, String> questionAnswer = new HashMap<>();

    private int backgroundColor = Color.WHITE;
    private int foregroundColor = 0;

    // ActivityResult ( for date pickup )
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
                }
            }
    );

    private Context ctx;
    public QuestionBoxFragment(Context ctx, boolean isStudent) {
        this.ctx = ctx;
        this.isStudent = isStudent;

        if (!isStudent) { // Parents
            questionAnswer.put("__mode__", "parents");
            TypedValue typedValue = new TypedValue();
            ctx.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
            foregroundColor = typedValue.data;
        } else { // Student
            questionAnswer.put("__mode__", "student");
            backgroundColor = Color.rgb(51, 132, 255);
            foregroundColor = Color.WHITE;
        }

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
                        ed.setTextSize(dp(7));
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
                            ar_launcher.launch(i);
                        });
                        rg.addView(tv, idx + 1);
                        specified.add(checkedId);
                        break;
                }
            }
        });

        l1.addView(rg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(12), 0, 0);

        l1.setLayoutParams(lp);
        ((LinearLayout)view).addView(l1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_box, container, false);

        ((TextView)layout.findViewById(R.id.textview_typeBox)).setText(isStudent ? "🎓 학생용" : "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 학부모용");
        
        QuestionBox qb_job = new QuestionBox("어떤 상담을 원하시나요?");
        qb_job.addQuestion("진로")
                .addQuestion("진학")
                .addQuestion(new Question("기타", QuestionType.INPUT));

        addQuestionOnLayout(layout, qb_job);

        QuestionBox qb_day = new QuestionBox("어떤 날에 상담을 진행할까요?");

        QuestionBox qb_when = new QuestionBox("상담 시간을 어떻게 할까요?");
        qb_when.addQuestion("아침시간")
                .addQuestion("점심시간")
                .addQuestion("저녁시간")
                .addQuestion(new Question("기타", QuestionType.INPUT));
        addQuestionOnLayout(layout, qb_when);


        /* Don't touch this code */
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT;

        Button button = new Button(ctx);
        button.setText("신청하기");
        button.setTextColor(Color.WHITE);
        button.setLayoutParams(lp);
        button.getBackground().mutate().setColorFilter(Color.rgb(77, 142, 255), PorterDuff.Mode.SRC);
        button.setOnClickListener(e -> {

        });
        layout.addView(button);
        return layout;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
