package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AnswersCommentsAdapter  extends RecyclerView.Adapter<AnswersCommentsAdapter.AnswersCommentsHolder>{

    private List<Comments> comment;
    private LayoutInflater inflater;
    private Context context;

    public AnswersCommentsAdapter(Context context,List<Comments> comment){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.comment=comment;
    }

    @NonNull
    @Override
    public AnswersCommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.comment_item,parent,false);
        AnswersCommentsHolder holder=new AnswersCommentsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersCommentsHolder holder, int position) {
        Comments current = comment.get(position);

        holder.name.setText(current.getName());
        holder.description.setText(current.getDescription());
        holder.date.setText("Posted On : "+current.getDate());

    }

    @Override
    public int getItemCount() {
        return comment.size();
    }

    public class AnswersCommentsHolder extends RecyclerView.ViewHolder{

        TextView name,description,date;

        public AnswersCommentsHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name_commenter);
            description=itemView.findViewById(R.id.desc_commenter);
            date=itemView.findViewById(R.id.post_date_commenter);
        }
    }
}
