package com.example.myapplication;

import android.util.Log;
import android.util.Pair;

import com.google.firebase.firestore.Exclude;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


enum UnitChange {
    UP,DOWN;
}

public class QuestionFormat implements Comparable<QuestionFormat> {
    private String title,body,Id;
    private Date postingDate;
    private int reply_count, vote_count;
    private List<String> tag_list;
    private List<Integer> indexes;
    private List<String> question_body;
    private List<String> question_title;
    private String user_id;
    private boolean isSelected;

    public QuestionFormat() {
    }

    public QuestionFormat(String title,String body,Date postingDate,List<String> tag_list,String user_id,List<Integer> indexes) {
        this.title=title;
        this.body=body;
        this.postingDate=postingDate;
        this.tag_list=tag_list;
        this.indexes=indexes;
        this.user_id=user_id;

    }

    public QuestionFormat(String title,String body,Date postingDate,List<String> tag_list,String user_id) {
        this.title=title;
        this.body=body;
        this.postingDate=postingDate;
        this.tag_list=tag_list;
        this.user_id=user_id;

        reply_count=0;
        vote_count=0;
        isSelected=false;

    }

    public QuestionFormat(String title,String body,Date postingDate,List<String> tag_list) {
        this.title=title;
        this.body=body;
        this.postingDate=postingDate;
        this.tag_list=tag_list;

        reply_count=0;
        vote_count=0;
        isSelected=false;

    }


    public String getTitle(){
        return title;
    }
    public String getBody(){
        return body;
    }

    public Date getPostingDate(){
        return postingDate;
    }

    public int getReply_count() {
        return reply_count;
    }
    public int getVote_count() {
        return vote_count;
    }
    public List<String> getTag_list(){
        return tag_list;
    }

    public void setTitle(String title){
        this.title=title;
    }
    public void setBody(String body){
        this.body=body;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }

    public void update_vote_count(UnitChange change){
        if(change==UnitChange.UP)
            vote_count++;
        else
            vote_count--;
    }
    public void update_reply_count(UnitChange change){
        if(change==UnitChange.UP)
            reply_count++;
        else
            reply_count--;
    }

    public List<Integer> getIndexes(){
        return indexes;
    }


    @Exclude
    public List<String> getQuestion_body() {
        return question_body;
    }

    public void setQuestion_body(List<String> question_body) {
        this.question_body = question_body;
    }

    @Exclude
    public List<String> getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(List<String> question_title) {
        this.question_title = question_title;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public int compareTo(QuestionFormat questionFormat) {
        return this.getTitle().compareTo(questionFormat.getTitle());
    }

    public static class ByBody implements Comparator<QuestionFormat>{
        @Override
        public int compare(QuestionFormat questionFormat, QuestionFormat t1) {
            return questionFormat.getBody().compareTo(t1.getBody());
        }
    }

    public static class ByReply implements Comparator<QuestionFormat>{
        @Override
        public int compare(QuestionFormat questionFormat, QuestionFormat t1) {
            return questionFormat.getReply_count()>t1.getReply_count()?1:(questionFormat.getReply_count()<t1.getReply_count())?-1:0;
        }
    }
    public static class ByVotes implements Comparator<QuestionFormat>{
        @Override
        public int compare(QuestionFormat questionFormat, QuestionFormat t1) {
            return questionFormat.getVote_count()>t1.getVote_count()?1:(questionFormat.getVote_count()<t1.getVote_count())?-1:0;
        }
    }

    public static class ByDate implements Comparator<QuestionFormat>{
        @Override
        public int compare(QuestionFormat questionFormat, QuestionFormat t1) {

            Date date1 = questionFormat.getPostingDate();
            Date date2 = t1.getPostingDate();

            return date1.compareTo(date2);

        }
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
