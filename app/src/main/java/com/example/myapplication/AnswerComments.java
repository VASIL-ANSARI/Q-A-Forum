package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AnswerComments extends AppCompatActivity {

    TextView question_title,comment_t,answer_t;
    RecyclerView comments,answers;
    AppCompatButton btn_comments,btn_answers;
    ImageButton btn_expand;
    AlertDialog alertDialog;
    List<Comments> comment;
    List<Answer> answer;
    AnswersCommentsAdapter answersCommentsAdapter;
    AnswersAdapter answersAdapter;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference ref=db.collection("Ques_Ans");

    String id;
    public static final String MyPREFERENCES = "MyPrefs" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_comments);

        getSupportActionBar().hide();

        comment=new ArrayList<>();
        answer=new ArrayList<>();
        id=getIntent().getStringExtra("Id_Ques");



        question_title=findViewById(R.id.question_t);
        comment_t=findViewById(R.id.comment_title_id);
        answer_t=findViewById(R.id.answer_title_id);
        comments=findViewById(R.id.comments_recycler_view);
        answers=findViewById(R.id.answers_recycler_view);

        ref.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                QuestionFormat questionFormat=documentSnapshot.toObject(QuestionFormat.class);
                question_title.setText(questionFormat.getTitle());
            }
        });

        retreive_from_firebase();

        btn_comments=findViewById(R.id.post_comments);

        btn_answers=findViewById(R.id.post_answers);
        btn_expand=findViewById(R.id.btn_expand_comments);

        btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comments.getVisibility()==View.VISIBLE) {
                    comments.setVisibility(View.GONE);
                    btn_expand.setRotation(0);
                }
                else{
                    comments.setVisibility(View.VISIBLE);
                    btn_expand.setRotation(180);
                }
            }
        });

        btn_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogin()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerComments.this);
                    builder.setTitle("Enter your Comment:");
                    builder.setIcon(R.drawable.insert_comments);
                    final EditText edit_text = new EditText(AnswerComments.this);
                    edit_text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    edit_text.setTextColor(Color.BLACK);
                    edit_text.setHint("Enter your comment here...");
                    builder.setView(edit_text);

                    //alertDialog.addContentView(edit_text,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!edit_text.getText().toString().trim().equals("")){
                                SharedPreferences sharedPreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                String email=sharedPreferences.getString("email",null);
                                db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                            User user=snapshot.toObject(User.class);
                                            if(user.getEmail().equals(email)){
                                                Comments comm=new Comments("",user.getId(),id,user.getName(),edit_text.getText().toString(),new Date());
                                                db.collection("Comm").add(comm).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        comm.setId(documentReference.getId());
                                                        db.collection("Comm").document(comm.getId()).set(comm);
                                                        comment.add(comm);
                                                        Collections.sort(comment);
                                                        answersCommentsAdapter.notifyDataSetChanged();
                                                        if(comment.size() > 0){
                                                            comment_t.setText("Comments"+"("+comment.size()+")");
                                                        }
                                                        else{
                                                            comment_t.setText("Comments(0)");
                                                        }
                                                        user.updateComments();
                                                        db.collection("User").document(user.getId()).set(user);
                                                        Toast.makeText(AnswerComments.this,"You just posted a comment", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(AnswerComments.this,"Please specify some comment",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();

                }
                else{
                    Toast.makeText(AnswerComments.this,"You need to login to post comments", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogin()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerComments.this);
                    builder.setTitle("Enter your Answer:");
                    builder.setIcon(R.drawable.auto_fix);
                    final EditText edit_text = new EditText(AnswerComments.this);
                    edit_text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    edit_text.setTextColor(Color.BLACK);
                    edit_text.setHint("Enter your answer here...");
                    builder.setView(edit_text);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            SharedPreferences sharedPreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            String email=sharedPreferences.getString("email",null);
                            db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                        User user=snapshot.toObject(User.class);
                                        if(user.getEmail().equals(email)){
                                            Answer ans=new Answer("",user.getId(),user.getName(),edit_text.getText().toString(),id,new Date());
                                            db.collection("Ans").add(ans).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    ans.setId(documentReference.getId());
                                                    db.collection("Ans").document(ans.getId()).set(ans);
                                                    answer.add(ans);
                                                    Collections.sort(answer);
                                                    answersAdapter.notifyDataSetChanged();
                                                    if(answer.size() > 0){
                                                        answer_t.setText("Answers"+"("+answer.size()+")");
                                                    }
                                                    else{
                                                        answer_t.setText("Answers(0)");
                                                    }
                                                    user.updateAnswers();
                                                    db.collection("User").document(user.getId()).set(user);
                                                    ref.document(ans.getQuesId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            QuestionFormat ques=documentSnapshot.toObject(QuestionFormat.class);
                                                            ques.update_reply_count(UnitChange.UP);
                                                            ref.document(ans.getQuesId()).set(ques);
                                                            Toast.makeText(AnswerComments.this,"You just posted an answer", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();

                }
                else{
                    Toast.makeText(AnswerComments.this,"You need to login to post answers", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void retreive_from_firebase() {
        answers.setLayoutManager(new GridLayoutManager(this,1));
        comments.setLayoutManager(new GridLayoutManager(this, 1));

        db.collection("Ans").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    Answer ans=snapshot.toObject(Answer.class);
                    Log.d("message",ans.getQuesId());
                    if(ans.getQuesId().equals(id)) {
                        answer.add(ans);
                        Collections.sort(answer);
                    }
                }

                answers.setItemAnimator(new DefaultItemAnimator());

                Log.d("message",answer.size()+"");
                if(answer.size() > 0){
                    answer_t.setText("Answers"+"("+answer.size()+")");
                }
                else{
                    answer_t.setText("Answers(0)");
                }
                answersAdapter=new AnswersAdapter(getApplicationContext(),answer);
                answers.setAdapter(answersAdapter);
                db.collection("Comm").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                            Comments comm=snapshot.toObject(Comments.class);
                            if(comm.getQuesId().equals(id)) {
                                comment.add(comm);
                                Collections.sort(comment);
                            }
                        }
                        if(comment.size() > 0){
                            comment_t.setText("Comments"+"("+comment.size()+")");
                        }
                        else{
                            comment_t.setText("Comments(0)");
                        }
                    }
                });

                comments.setItemAnimator(new DefaultItemAnimator());

                answersCommentsAdapter = new AnswersCommentsAdapter(getApplicationContext(), comment);
                comments.setAdapter(answersCommentsAdapter);

            }
        });

    }

    boolean isLogin(){
        SharedPreferences sharedPreferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String email=sharedPreferences.getString("email",null);
        if(email==null)
            return false;
        Boolean login=sharedPreferences.getBoolean("islogin"+" "+email,false);
        return login;

    }
}