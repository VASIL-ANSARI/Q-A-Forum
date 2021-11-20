package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SimilarAnswersActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference ref=db.collection("Ques_Ans");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_answers);

        getSupportActionBar().hide();

        String id=getIntent().getStringExtra("User_Id");

        recyclerview=findViewById(R.id.recyclerView);

        List<QuestionFormat> questions=new ArrayList<>();
        ref.orderBy("postingDate").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    QuestionFormat questionFormat=snapshot.toObject(QuestionFormat.class);
                    if(questionFormat.getUser_id().equals(id)) {
                        questions.add(questionFormat);
                    }
                }
                setList(questions);
            }
        });

    }

    private void setList(List<QuestionFormat> lists){

        QuestionUserAdapter qa=new QuestionUserAdapter(SimilarAnswersActivity.this,lists);
        recyclerview.setAdapter(qa);

        LinearLayoutManager lm=new LinearLayoutManager(SimilarAnswersActivity.this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(lm);

        recyclerview.setItemAnimator(new DefaultItemAnimator());
    }
}