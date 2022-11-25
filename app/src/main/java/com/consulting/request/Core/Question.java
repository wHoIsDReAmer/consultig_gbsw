package com.consulting.request.Core;

public class Question {
    private final QuestionType questionType;
    private String text;
    private String value;

    public Question(QuestionType type) {
        this.questionType = type;
    }

    public Question(String text, QuestionType type) {
        this.text = text;
        this.questionType = type;
    }

    public Question setText(String text) {
        this.text = text;
        return this;
    }

    public Question setValue(String value) {
        this.value = value;
        return this;
    }

    public QuestionType getType() {
        return this.questionType;
    }

    public String getText() {
        return this.text;
    }

    public String getValue() {
        return this.value;
    }
}
