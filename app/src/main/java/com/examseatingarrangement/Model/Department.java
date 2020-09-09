package com.examseatingarrangement.Model;

import java.util.ArrayList;

public class Department {
    String name, login_id,login_password,question,answer;
    ArrayList<String> classes;

    public Department() {
    }

    public Department(String name, String login_id, String login_password, String question, String answer, ArrayList<String> classes) {
        this.name = name;
        this.login_id = login_id;
        this.login_password = login_password;
        this.question = question;
        this.answer = answer;
        this.classes = classes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getLogin_password() {
        return login_password;
    }

    public void setLogin_password(String login_password) {
        this.login_password = login_password;
    }

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

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
}
