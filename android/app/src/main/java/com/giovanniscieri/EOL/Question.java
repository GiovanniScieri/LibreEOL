package com.giovanniscieri.EOL;

import java.util.ArrayList;

public class Question {
    private String idQuestion;
    private String question;
    private String type;

    private ArrayList<Answer> answersList;

    public String getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(String idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAnswersList(ArrayList<Answer> answersList) {
        this.answersList = answersList;
    }

    public ArrayList<Answer> getAnswersList() {
        return answersList;
    }
}
