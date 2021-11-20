package com.example.myapplication;

import java.util.Date;

public class Comments implements Comparable<Comments>{

    private String id,userId,name,description,quesId;
    private Date date;

    public Comments(){

    }

    public Comments(String id, String userId,String quesId, String name, String description, Date date) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.quesId=quesId;
        this.description = description;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getQuesId() {
        return quesId;
    }

    public void setQuesId(String quesId) {
        this.quesId = quesId;
    }

    @Override
    public int compareTo(Comments comments) {
        return comments.getDate().compareTo(this.date);
    }
}
