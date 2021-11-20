package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users){
        this.context=context;
        this.users=users;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.user_item_layout,parent,false);
        UserAdapter.UserHolder holder=new UserAdapter.UserHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User current=users.get(position);
        holder.user_name.setText(current.getName());
        holder.user_email.setText(current.getEmail());
        String s="Answers Posted:"+current.getAnswersPosted()+"\nQuestions Posted:"+current.getQuestionsPosted()+"\nComments:"+current.getComments()+"\nLast Login Status: ";
        if(current.getStat().equals(status.online)){
            s+="online";
        }
        else{
            s+= current.getLast_login_time();
        }
        holder.user_description.setText(s);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView user_name,user_description,user_email;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_description = itemView.findViewById(R.id.user_data);
            user_email=itemView.findViewById(R.id.user_email);
        }
    }
}
