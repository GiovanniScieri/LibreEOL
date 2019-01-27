package com.giovanniscieri.EOL;

public class Answer {
    private String idAnswer;
    private String text;
    private boolean checked = false;


    public void setIdAnswer(String idAnswer) {
        this.idAnswer = idAnswer;
    }

    public String getIdAnswer() {
        return idAnswer;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
