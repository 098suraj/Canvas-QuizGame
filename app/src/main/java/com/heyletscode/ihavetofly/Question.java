package com.heyletscode.ihavetofly;

public class Question {
    private String question;
    private  String a;
    private  String b;
    private  String c;
    private  String correct_answer;
    private  String score;
    public Question(){}

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Question(String question, String a, String b, String c, String correct_answer) {
        this.question = question;
        this.a = a;
        this.b = b;
        this.c = c;
        this.correct_answer = correct_answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }
}
