package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SimilarAnswersAdapter extends RecyclerView.Adapter<SimilarAnswersAdapter.QAHolder>{

    List<Pair<String,String>> questions;
    Context context;
    List<Integer> indexes;

    public SimilarAnswersAdapter(Context context, List<String> questions_title, List<String> questions_body,List<Integer> indexes) {
        questions=new ArrayList<>();
        for(int i=0;i<questions_title.size();i++){
            questions.add(new Pair(questions_title.get(i),questions_body.get(i)));
        }
        this.context=context;
        this.indexes=indexes;

    }

    @NonNull
    @Override
    public SimilarAnswersAdapter.QAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.frame_answers,
                        parent, false);

        //View view=inflater.inflate(R.layout.tag_layout,parent,false);
        SimilarAnswersAdapter.QAHolder holder=new SimilarAnswersAdapter.QAHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarAnswersAdapter.QAHolder holder, int position) {

        Pair<String,String> ques=questions.get(position);
        String Id=indexes.get(position).toString();
        holder.setData(ques.first);

        holder.get_ans.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,QuestionAnswerActivity.class);
                intent.putExtra("Id",Id);
                intent.putExtra("Title",ques.first);
                intent.putExtra("Body",ques.second);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QAHolder extends RecyclerView.ViewHolder{

        TextView similar_ques;
        ImageButton get_ans;

        public QAHolder(@NonNull View itemView) {
            super(itemView);
            similar_ques=itemView.findViewById(R.id.similar_ques_data);
            get_ans=itemView.findViewById(R.id.similar_show_ans);
        }

        public void setData(String q) {
            similar_ques.setText(q);
        }
    }
}
