package com.example.myapplication;

import java.util.Date;

public class Answer implements Comparable<Answer>{
    private String id,userId,name,description,quesId;
    private Date date;

    public Answer(){

    }

    public Answer(String id, String userId, String name, String description, String quesId, Date date) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.quesId = quesId;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Answer answer) {
        return answer.getDate().compareTo(this.date);
    }
}
