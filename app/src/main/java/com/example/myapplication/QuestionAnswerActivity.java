package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class QuestionAnswerActivity extends AppCompatActivity {

    TextView question_title;
    WebView question,answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        question_title=findViewById(R.id.txt_question);
        question=findViewById(R.id.web_question);
        answer=findViewById(R.id.web_answer);

        String id=getIntent().getStringExtra("Id");
        String title=getIntent().getStringExtra("Title");
        String body=getIntent().getStringExtra("Body");

        question_title.setText(title);

        question.getSettings().setJavaScriptEnabled(true);
        question.loadDataWithBaseURL(null,body,"text/html","utf-8",null);

        answer.getSettings().setJavaScriptEnabled(true);
        answer.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("android_asset"))
                        return false;
                return true;
            }
        });
        Log.d("message", String.valueOf((Integer.parseInt(id)+1)));
        String path = "file:///android_asset/HTML files/" + "file " + (id + 1) + ".html";
        answer.loadUrl(path);
        //answer.loadDataWithBaseURL(null,body,"text/html","utf-8",null);
    }
}