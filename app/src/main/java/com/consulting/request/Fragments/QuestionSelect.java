package com.consulting.request.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.consulting.request.R;
import com.consulting.request.RequestActivity;

public class QuestionSelect extends Fragment {
    private Context ctx;

    private Button btn_student;
    private Button btn_parents;

    private RequestActivity mainActivity;

    public QuestionSelect(RequestActivity mainActivity) {
        this.ctx = (Context) mainActivity;
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select, container, false);
        btn_student = layout.findViewById(R.id.btn_student);
        btn_parents = layout.findViewById(R.id.btn_parents);

        btn_student.setOnClickListener(e -> {
            mainActivity.toQuestionFragmentStudent();
        });

        btn_parents.setOnClickListener(e -> {
            mainActivity.toQuestionFragmentParents();
        });
        return layout;
    }
}
