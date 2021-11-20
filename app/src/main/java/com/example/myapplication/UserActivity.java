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

public class UserActivity extends AppCompatActivity {

    UserAdapter qa;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference ref=db.collection("User");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().hide();

        List<User> users=new ArrayList<>();
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    User q=snapshot.toObject(User.class);
                    users.add(q);
                    RecyclerView rv=findViewById(R.id.user_list);
                    qa=new UserAdapter(UserActivity.this,users);
                    rv.setAdapter(qa);

                    LinearLayoutManager lm=new LinearLayoutManager(UserActivity.this);
                    lm.setOrientation(LinearLayoutManager.VERTICAL);
                    rv.setLayoutManager(lm);

                    rv.setItemAnimator(new DefaultItemAnimator());
                }
            }
        });

    }
}