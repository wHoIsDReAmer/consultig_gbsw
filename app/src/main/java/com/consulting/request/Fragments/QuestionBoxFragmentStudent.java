package com.consulting.request.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.consulting.request.Core.Question;
import com.consulting.request.Core.QuestionBox;
import com.consulting.request.Core.QuestionType;
import com.consulting.request.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class QuestionBoxFragmentStudent extends Fragment {
    private HashMap<String, String> questionAnswer = new HashMap<>();

    private int backgroundColor = Color.rgb(51, 132, 255);
    private int foregroundColor = 0;

    private Context ctx;

    public QuestionBoxFragmentStudent(Context ctx) {
        questionAnswer.put("__mode__", "student");
        this.ctx = ctx;
        foregroundColor = Color.WHITE;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
    }

    public void addQuestionOnLayout(View view, QuestionBox box) {
        foregroundColor = Color.WHITE;

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
                checkedId--; // sync the index
                Question question = questionList.get(checkedId);
                QuestionType type = question.getType();

                switch (type) {
                    case SELECT:
                        questionAnswer.put(box.getTitle(), question.getText());
                        break;
                    case INPUT:
                        if (specified.contains(checkedId))
                            break;

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
                        lp.setMargins(0, dp(17), 0, dp(17));

                        EditText ed = new EditText(ctx);
                        ed.setLayoutParams(lp);
                        ed.setTextColor(foregroundColor);
                        ed.setTypeface(ResourcesCompat.getFont(ctx, R.font.neor));
                        ed.getBackground().mutate().setColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP);

                        int finalCheckedId = checkedId;
                        ed.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                rg.check(finalCheckedId+1);
                                questionAnswer.put(box.getTitle(), s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        rg.addView(ed, checkedId+1);
                        specified.add(checkedId);
                        break;
                    case DATE:

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
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_box_students, container, false);
        layout.setOrientation(LinearLayout.VERTICAL);



        return layout;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
