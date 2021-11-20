package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Views.SimpleViewAnimator;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionHolder>{
    private List<QuestionFormat> questions;
    private LayoutInflater inflater;
    private  Context context;
    private RecyclerView.RecycledViewPool
            viewPool = new RecyclerView.RecycledViewPool();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference ref=db.collection("Ques_Ans");

    public QuestionAdapter(MainActivity context, List<QuestionFormat> questions){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.questions=questions;
    }

    @NonNull
    @Override
    public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.question_layout,parent,false);
        QuestionHolder holder=new QuestionHolder(view);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionHolder holder, int position) {
        QuestionFormat current= questions.get(position);
        holder.setData(current);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(
                holder.tag_list.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
        layoutManager
                .setInitialPrefetchItemCount(
                        current
                                .getTag_list()
                                .size());

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        TagAdapter childItemAdapter
                = new TagAdapter(
                current.getTag_list(),context);

        holder.tag_list.setLayoutManager(
                layoutManager
        );

        holder.tag_list.setAdapter(
                childItemAdapter
        );

        holder.tag_list.setRecycledViewPool(viewPool);

        LinearLayoutManager anotherManager=new LinearLayoutManager(
                holder.list_questions.getContext(),LinearLayoutManager.VERTICAL,
                false
        );
        anotherManager.setInitialPrefetchItemCount(current.getQuestion_title().size());

        SimilarAnswersAdapter similarAnswersAdapter=new SimilarAnswersAdapter(context,current.getQuestion_title(),current.getQuestion_body(),current.getIndexes());

        holder.list_questions.setLayoutManager(anotherManager);
        holder.list_questions.setAdapter(similarAnswersAdapter);



        holder.imageUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current.update_vote_count(UnitChange.UP);
                List<String> temp_title=current.getQuestion_title();
                List<String> temp_body=current.getQuestion_body();
                current.setQuestion_title(null);
                current.setQuestion_body(null);
                ref.document(current.getId()).set(current);
                current.setQuestion_title(temp_title);
                current.setQuestion_body(temp_body);
                //ref.whereEqualTo("title",current.getTitle()).whereEqualTo("body",current.getBody()).getFirestore();
                notifyDataSetChanged();
            }
        });

        holder.imageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current.update_vote_count(UnitChange.DOWN);
                List<String> temp_title=current.getQuestion_title();
                List<String> temp_body=current.getQuestion_body();
                current.setQuestion_title(null);
                current.setQuestion_body(null);
                ref.document(current.getId()).set(current);
                current.setQuestion_title(temp_title);
                current.setQuestion_body(temp_body);
                notifyDataSetChanged();
            }
        });

        holder.imageAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //current.update_reply_count(UnitChange.UP);
                Intent intent =new Intent(context,AnswerComments.class);
                intent.putExtra("Id_Ques",current.getId());
                context.startActivity(intent);
                //ref.document(current.getId()).set(current);
                //notifyDataSetChanged();
            }
        });

        holder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context,AnswerComments.class);
                intent.putExtra("Id_Ques",current.getId());
                context.startActivity(intent);
            }
        });

        holder.imageShowAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.simpleViewAnimator.getVisibility() == View.GONE) {
                    holder.simpleViewAnimator.setVisibility(View.VISIBLE);
                    holder.imageShowAns.setImageResource(R.drawable.arrow_up);
                }
                else{
                    holder.simpleViewAnimator.setVisibility(View.GONE);
                    holder.imageShowAns.setImageResource(R.drawable.ic_arrow_down_black);
                    //holder.imageShowAns.animate().rotation(0);
                }
                notifyDataSetChanged();
            }
        });


        if(current.isSelected()==true){
            holder.itemView.setBackgroundColor(Color.RED);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                    current.setSelected(false);
                }
            },5000);
//            new CountDownTimer(5000,50){
//                @Override
//                public void onTick(long l) {
//
//                }
//
//                @Override
//                public void onFinish() {
//
//                }
//            }.start();
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class QuestionHolder extends RecyclerView.ViewHolder{
        private TextView title,body,counts;
        private RecyclerView tag_list,list_questions;
        private ImageButton imageUp,imageDown,imageAns,imageComment,imageShowAns;
        private SimpleViewAnimator simpleViewAnimator;
        public static final String MyPREFERENCES = "MyPrefs" ;


        public QuestionHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.question_title);
            body=itemView.findViewById(R.id.question_body);
            counts=itemView.findViewById(R.id.ans_vote_date);
            imageDown=itemView.findViewById(R.id.down_vote_button);
            imageUp=itemView.findViewById(R.id.up_vote_button);

            imageAns=itemView.findViewById(R.id.add_ans_button);
            imageComment=itemView.findViewById(R.id.add_com_button);
            imageShowAns=itemView.findViewById(R.id.show_ans);
            list_questions=itemView.findViewById(R.id.list_ques);

            CardView cardView=itemView.findViewById(R.id.question_format);

            SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            int imgPath=sharedPreferences.getInt("theme",-1);

            if(imgPath!=-1){
                int textColor=sharedPreferences.getInt("theme_text",-1);
                int bgColor=sharedPreferences.getInt("theme_bg",-1);
                int tagText=sharedPreferences.getInt("theme_tag_text",-1);
                int tagBg=sharedPreferences.getInt("theme_tag_bg",-1);
                //Log.d("message",textColor+" "+bgColor+" ");

                title.setTextColor(textColor);
                body.setTextColor(textColor);
                counts.setTextColor(textColor);

                cardView.setCardBackgroundColor(bgColor);

            }

            ViewCompat.setNestedScrollingEnabled(list_questions, false);

            simpleViewAnimator=itemView.findViewById(R.id.container_list_questions);



            tag_list=itemView.findViewById(R.id.tag_list);
        }

        public void setData(QuestionFormat question) {
            title.setText(question.getTitle());
            body.setText(question.getBody());

            int ans_count= question.getReply_count();
            int vote_count= question.getVote_count();

            String text="Answers: "+ans_count+", Votes: "+vote_count;
            SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            text+=", Posted on: "+format.format(question.getPostingDate());
            counts.setText(text);



        }

//        private void loadData(){
//
//            ArrayList<SimilarAnswersModel> arrayList = new ArrayList();
//
//
//            RecyclerView.Adapter<SimilarAnswersModel, SimilarAnswersViewHolder> adapter = new RecyclerView.Adapter<SimilarAnswersModel, SimilarAnswersViewHolder>(R.layout.frame_answers,
//                    SimilarAnswersViewHolder.class, SimilarAnswersModel.class, arrayList) {
//                @Override
//                public void onBindViewHolder(@NonNull SimilarAnswersModel holder, int position) {
//
//                }
//            };
//
//            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//            list_questions.setLayoutManager(layoutManager);
//            list_questions.setAdapter(adapter);
//        }
    }
}
