package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class AnswersAdapter  extends RecyclerView.Adapter<AnswersAdapter.AnswersHolder>{

    private List<Answer> answers;
    private LayoutInflater inflater;
    private Context context;

    public AnswersAdapter(Context context, List<Answer> answers) {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.answers=answers;
    }

    @NonNull
    @Override
    public AnswersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.answer_item,parent,false);
        AnswersHolder holder=new AnswersHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersHolder holder, int position) {

        Answer current = answers.get(position);

        holder.name.setText(current.getName());
        holder.date.setText("Posted On : "+current.getDate());
        holder.description.setText(current.getDescription());
       // holder.description.setMovementMethod(LinkMovementMethod.getInstance());
//        holder.description.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://www.google.com"));
//                context.startActivity(intent);
//            }
//        });

        String text="Hi.. , I.. am.. Gyan.. and.. i.. will.. narrate.. you.. the.. answer.. posted.. by.. "+current.getName()+"... \n"+current.getDescription()+"\n"+"Thank you for listening... Good Bye";

        AsyncTask<Void, Void, Void> asyncTask=new AsyncTask<Void, Void, Void>(

        ) {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if(isCancelled()){
                    return null;
                }
                new TTS(text);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
            }
        };
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

//                Log.d("message", String.valueOf(current.getDate().getDay()));
//                Log.d("message", String.valueOf(current.getDate().getMonth()));
//                Log.d("message", String.valueOf(current.getDate().getYear()));
//                Log.d("message", String.valueOf(current.getDate().getDate()));
//
//                Date date = new Date();
//                String date_result="";
//
//                LocalDate first_date = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
//                LocalDate second_date = LocalDate.of(current.getDate().getYear(), current.getDate().getMonth(), current.getDate().getDay());
//                Period difference = Period.between(first_date, second_date);
//
//                //LocalDate pastDate = LocalDate.of(current.getDate());
//                LocalDate now = LocalDate.now();
//                long days = ChronoUnit.DAYS.between(, now);
//
//                Log.d("message", String.valueOf(days));

                Log.d("message",asyncTask.getStatus().name());

                if(asyncTask.getStatus().equals(AsyncTask.Status.RUNNING) || asyncTask.getStatus().equals(AsyncTask.Status.FINISHED)){
                    asyncTask.cancel(true);
                }
                else{
                    asyncTask.execute();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class AnswersHolder extends RecyclerView.ViewHolder{

        TextView name,description,date;
        ImageButton imageView;

        public AnswersHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_commenter_ans);
            description=itemView.findViewById(R.id.desc_commenter_ans);
            date=itemView.findViewById(R.id.post_date_commenter_ans);
            imageView=itemView.findViewById(R.id.text_to_speech);

            Linkify.addLinks(description,Linkify.ALL);

            description.setMovementMethod(BetterLinkMovementMethod.newInstance().setOnLinkClickListener(new BetterLinkMovementMethod.OnLinkClickListener() {
                @Override
                public boolean onClick(TextView textView, String url) {

                    //webView.loadUrl(url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);

                    Toast.makeText(context,"url : "+url,Toast.LENGTH_SHORT).show();
                    return true;

                }
            }));
            //Linkify.addLinks(description,Linkify.WEB_URLS);
        }
    }

    class TTS implements TextToSpeech.OnInitListener{



        private TextToSpeech tts=new TextToSpeech(context,this);

        private String message;

        public TTS(String message) {
            this.message = message;
        }

        @Override
        public void onInit(int i) {
            if(i==TextToSpeech.SUCCESS){
                Locale local=Locale.US;
                int result=tts.setLanguage(local);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "This Language is not supported", Toast.LENGTH_SHORT).show();
                } else {
                    speakOut(message);
                    //tts.shutdown();
                }
            }else{
                Toast.makeText(context, "An error occurred! Please try later", Toast.LENGTH_SHORT).show();
            }
        }

        private void speakOut(String message){
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
