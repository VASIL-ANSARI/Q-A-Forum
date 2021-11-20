package com.example.myapplication;

public class Tag {

    // string course_name for storing course_name
    // and imgid for storing image id.
    private String name,description;

    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {        return name;
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
}
