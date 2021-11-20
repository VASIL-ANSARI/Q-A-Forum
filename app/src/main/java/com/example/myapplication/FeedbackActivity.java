package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {

    SeekBar seekBar;
    EditText edit_text;
    AppCompatButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        seekBar=findViewById(R.id.rating_seek_bar);
        btn=findViewById(R.id.submit_suggestions_btn_id);
        edit_text=findViewById(R.id.edit_text_suggestions_id);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = seekBar.getProgress();
                if (p <= 2 && edit_text.getText().toString().trim().equals("")) {
                    Toast.makeText(FeedbackActivity.this, "Please specify some suggestions for our improvement", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedbackActivity.this, "You give rating " + (p+1) + " to this app. Thank you for your feedback", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}