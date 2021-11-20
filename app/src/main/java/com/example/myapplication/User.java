package com.example.myapplication;
enum status{
    online,offline;
}
public class User {
    private String Id,name,email,last_login_time,last_login_date,platform;
    status stat;
    private int answersPosted,questionsPosted,comments;

    public User(){

    }


    public User(String name, String email, String last_login_time, String last_login_date,status stat) {
        this.name = name;
        this.email = email;
        this.last_login_time = last_login_time;
        this.last_login_date = last_login_date;
        this.stat=stat;

        answersPosted=0;
        questionsPosted=0;
        comments=0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getLast_login_date() {
        return last_login_date;
    }

    public void setLast_login_date(String last_login_date) {
        this.last_login_date = last_login_date;
    }

    public status getStat() {
        return stat;
    }

    public void setStat(status stat) {
        this.stat = stat;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getAnswersPosted() {
        return answersPosted;
    }

    public void setAnswersPosted(int answersPosted) {
        this.answersPosted = answersPosted;
    }

    public int getQuestionsPosted() {
        return questionsPosted;
    }

    public void setQuestionsPosted(int questionsPosted) {
        this.questionsPosted = questionsPosted;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void updateQuestions(){
        questionsPosted++;
    }

    public void updateAnswers(){
        answersPosted++;
    }

    public void updateComments(){
        comments++;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
