package com.example.myapplication;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagHolder> {
    public List<String> tags;
    public LayoutInflater inflater;
    public Context context;

    public TagAdapter(List<String> tags,Context context){
        //inflater=LayoutInflater.from(context);
        this.tags=tags;
        this.context=context;
    }

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.tag_layout,
                        parent, false);

        //View view=inflater.inflate(R.layout.tag_layout,parent,false);
        TagHolder holder=new TagHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
        String tag=tags.get(position);
        holder.setData(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class TagHolder extends RecyclerView.ViewHolder {
        private TextView tag_text;
        public static final String MyPREFERENCES = "MyPrefs" ;

        public TagHolder(View view) {
            super(view);
            tag_text=view.findViewById(R.id.tag_text);
            CardView cardView=itemView.findViewById(R.id.tag_format);

            SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            int imgPath=sharedPreferences.getInt("theme",-1);

            if(imgPath!=-1){
                int tagText=sharedPreferences.getInt("theme_tag_text",-1);
                int tagBg=sharedPreferences.getInt("theme_tag_bg",-1);

                tag_text.setTextColor(tagText);
                cardView.setCardBackgroundColor(tagBg);
            }

        }

        public void setData(String tag) {
            tag_text.setText(tag);
        }
    }
}
