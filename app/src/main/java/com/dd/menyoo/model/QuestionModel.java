package com.dd.menyoo.model;

/**
 * Created by Administrator on 01-Mar-16.
 */
public class QuestionModel {
    String question;
    String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public QuestionModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
