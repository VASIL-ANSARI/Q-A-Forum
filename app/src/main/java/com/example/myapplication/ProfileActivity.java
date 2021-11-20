package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    TextView QuestionPosted,QuestionAnswered,emailTxt,nameTxt;
    ImageView imageView;
    String Id;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();
        Id=null;


        QuestionPosted=findViewById(R.id.txt_ques_posted_id);
        imageView=findViewById(R.id.imageView2);
        QuestionAnswered=findViewById(R.id.txt_question_answered_id);
        emailTxt=findViewById(R.id.editTextTextEmailAddress);
        nameTxt=findViewById(R.id.editTextName);
        btn=findViewById(R.id.btn_my_posted_questions_id);

        String id=getIntent().getStringExtra("User Id");
        db.collection("User").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User user=documentSnapshot.toObject(User.class);
                Id=user.getId();
                if(user.getPlatform()!=null && user.getPlatform().equals("Git")){
                    imageView.setImageDrawable(getDrawable(R.drawable.github_icon));
                }
                else{
                    imageView.setImageDrawable(getDrawable(R.drawable.img_5));
                }
                QuestionPosted.setText(user.getQuestionsPosted()+"");
                QuestionAnswered.setText(user.getAnswersPosted()+"");
                emailTxt.setText(user.getEmail());
                nameTxt.setText(user.getName());
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,SimilarAnswersActivity.class);
                intent.putExtra("User_Id",Id);
                startActivity(intent);
                finish();
            }
        });

    }
}