package com.consulting.request.Core;

import java.util.ArrayList;

public class QuestionBox {
    private ArrayList<Question> qList = new ArrayList<>();
    private int current;
    private String text;
    public QuestionBox(String title) {
        this.text = title;
    }

    public QuestionBox addQuestion(Question q) {
        this.qList.add(q);
        return this;
    }

    public QuestionBox addQuestion(String title, String value) {
        Question q = new Question(QuestionType.INPUT);
        q.setText(title);
        q.setValue(value);
        this.qList.add(q);
        return this;
    }

    public QuestionBox addQuestion(String title) {
        Question q = new Question(QuestionType.SELECT);
        q.setText(title);
        this.qList.add(q);
        return this;
    }

        public ArrayList<Question> getQuestionList() {
        return this.qList;
    }

    public int getSelect() {
        return this.current;
    }

    public void setSelect(int select) {
        this.current = select;
    }

    public String getTitle() {
        return this.text;
    }
}