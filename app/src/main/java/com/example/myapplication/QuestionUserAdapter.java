package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionUserAdapter extends  RecyclerView.Adapter<QuestionUserAdapter.QuestionUserHolder>{

    private List<QuestionFormat> questions;
    private Context context;
    private LayoutInflater inflater;

    public QuestionUserAdapter(Context context, List<QuestionFormat> questions){
        this.context = context;
        this.questions=questions;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public QuestionUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.similar_layout,parent,false);
        QuestionUserHolder holder=new QuestionUserHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionUserHolder holder, int position) {
        QuestionFormat current=questions.get(position);
        holder.title.setText(current.getTitle());
        holder.body.setText(current.getBody());
        holder.desc.setText("Answers: "+current.getReply_count()+",  Votes: "+current.getVote_count()+",  Posted on: "+current.getPostingDate());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class QuestionUserHolder extends RecyclerView.ViewHolder{

        TextView title,body,desc;

        public QuestionUserHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.question_title1);
            body=itemView.findViewById(R.id.question_body1);
            desc=itemView.findViewById(R.id.ans_vote_date1);

        }
    }
}
