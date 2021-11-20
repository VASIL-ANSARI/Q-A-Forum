package com.example.myapplication;

import android.graphics.Color;

public class ThemeView {

    String description;
    int imagePath;
    boolean selected;
    int textColor,backgroundColor,tagTextColor,tagBgColor;

    public ThemeView(){
    }

    public ThemeView(int imagePath,int textColor,int backgroundColor,int tagTextColor,int tagBgColor) {
        this.imagePath = imagePath;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.tagTextColor = tagTextColor;
        this.tagBgColor = tagBgColor;

        selected = false;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTagTextColor() {
        return tagTextColor;
    }

    public void setTagTextColor(int tagTextColor) {
        this.tagTextColor = tagTextColor;
    }

    public int getTagBgColor() {
        return tagBgColor;
    }

    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = tagBgColor;
    }
}
