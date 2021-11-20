package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;

import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.speechly.client.slu.StreamConfig;
import com.speechly.client.speech.Client;
import com.speechly.ui.SpeechlyButton;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    org.tensorflow.lite.Interpreter interpreter;
    FloatingActionButton button,tags_btn,ques_btn,users_btn;
    List<Pair<String,String>> question;
    Button ques_add,cancel_dialog;
    EditText title,description;
    Spinner spinner;
    List<String> categories;
    List<QuestionFormat> questions;
    List<String> strings;
    QuestionAdapter qa;
    List<QuestionFormat> qts;
    SharedPreferences sharedPreferences;
    String notificationStatus;
    SpeechlyButton speechlyBtn;

    private ProgressDialog progressBar;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;


    List<QuestionFormat> qqts;

    AlertDialog alertDialog;
    LinearLayout ques,users,tags;

    ImageButton settings,sortButton;
    SearchView searchView;
    Switch switchPush;

    CoordinatorLayout layout;

    String searchBy;

//    GoogleSignInClient mGoogleSignInClient;
//    GoogleApiClient mGoogleApiClient;
//    static final int RC_SIGN_IN = 100;

    private Uri uri;
    private File file;
    private Intent camIntent;
    private Intent galIntent;
    private Intent cropIntent;

    public static final String MyPREFERENCES = "MyPrefs" ;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference ref=  db.collection("Ques_Ans");
    RecyclerView rv;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv=findViewById(R.id.question_list);
        layout=findViewById(R.id.layout_main);

        String connectivityStatus=getConnectivityStatusString(MainActivity.this);
        if(connectivityStatus==null || connectivityStatus.equals("No internet is available") ){

            Toast.makeText(MainActivity.this,"You are not connected to internet Please Connect and try Again!",Toast.LENGTH_LONG).show();

            Timer timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MainActivity.this.finish();
                    System.exit(0);
                }
            },5000);
        }

        else {

            enableRuntimepermission();

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_bar);


            View action_bar = getSupportActionBar().getCustomView();
            settings = action_bar.findViewById(R.id.settings_id);
            searchView = action_bar.findViewById(R.id.search_id);
            switchPush = action_bar.findViewById(R.id.switch_id);
            sortButton = action_bar.findViewById(R.id.sort_btn);

            searchBy = "title";

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("message","search clicked");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.search_dialog_box, null);
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();

                    AppCompatRadioButton tag_radio = dialogView.findViewById(R.id.radio_tag_search);
                    AppCompatRadioButton title_radio = dialogView.findViewById(R.id.radio_title_search);
                    AppCompatRadioButton body_radio = dialogView.findViewById(R.id.radio_body_search);

                    title_radio.setChecked(true);

                    tag_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                searchBy = "tag";
                                title_radio.setChecked(false);
                                body_radio.setChecked(false);
                            }
                        }
                    });
                    title_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                searchBy = "title";
                                tag_radio.setChecked(false);
                                body_radio.setChecked(false);

                            }
                        }
                    });
                    body_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                searchBy = "body";
                                tag_radio.setChecked(false);
                                title_radio.setChecked(false);

                            }
                        }
                    });

                }
            });


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d("message", query);
                    filterData(query, searchBy);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Log.d("message",newText);
                    if (newText.trim().equals("")) {
                        Log.d("message", newText);
                        qts = new ArrayList<>();
                        ref.orderBy("postingDate").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                    QuestionFormat q = documentSnapshot.toObject(QuestionFormat.class);
                                    q.setId(documentSnapshot.getId());
                                    List<String> qt_b = new ArrayList<>();
                                    List<String> qt_t = new ArrayList<>();
                                    for (int i : q.getIndexes()) {
                                        qt_t.add(question.get(i).first);
                                        qt_b.add(question.get(i).second);
                                    }
                                    q.setQuestion_body(qt_b);
                                    q.setQuestion_title(qt_t);
                                    qts.add(q);
                                }
                                setList(qts);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.d("message","failed");
                            }
                        });
                    }
                    return true;
                }
            });

            switchPush.setChecked(false);

            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            int imgPath=sharedPreferences.getInt("theme",-1);

            if(imgPath!=-1){
                int textColor=sharedPreferences.getInt("theme_text",-1);
                int bgColor=sharedPreferences.getInt("theme_bg",-1);
                Log.d("message",textColor+" "+bgColor+" ");
            }

            if (isLogin() == false) {
                switchPush.setClickable(false);
            } else {
                switchPush.setClickable(true);
            }

            switchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        Log.d("message", "user");
                        List<QuestionFormat> temp = new ArrayList<>();
                        String userEmail = sharedPreferences.getString("email", null);
                        if (userEmail != null) {
                            db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        User user = snapshot.toObject(User.class);
                                        if (user.getEmail().equals(userEmail)) {
                                            for (QuestionFormat q : qts) {
                                                if (q.getUser_id().equals(user.getId())) {
                                                    temp.add(q);
                                                }
                                            }

                                            qqts = new ArrayList<>(qts);
                                            qts = new ArrayList<>(temp);
                                            setList(temp);
                                            qts = new ArrayList<>(qqts);
                                            break;
                                        }
                                    }
                                }
                            });
                        }

                    } else {
                        Log.d("message", "No User");
                        setList(qts);
                    }
                }
            });

            sortButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.sort_dialog_box, null);
                    builder.setView(dialogView);
                    alertDialog = builder.create();
                    AppCompatCheckBox title_check, body_check, date_check, answers_check, votes_check;
                    title_check = dialogView.findViewById(R.id.title_check);
                    body_check = dialogView.findViewById(R.id.body_check);
                    date_check = dialogView.findViewById(R.id.date_check);
                    answers_check = dialogView.findViewById(R.id.answers_check);
                    votes_check = dialogView.findViewById(R.id.votes_check);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (title_check.isChecked()) {
                                if (sortButton.getRotation() == 0) {
                                    Collections.sort(qts);
                                    sortButton.setRotation(180);
                                } else {
                                    Collections.sort(qts, Collections.reverseOrder());
                                    sortButton.setRotation(0);
                                }
                                qa.notifyDataSetChanged();
                            }
                            if (body_check.isChecked()) {
                                if (sortButton.getRotation() == 0) {
                                    Collections.sort(qts, new QuestionFormat.ByBody());
                                    sortButton.setRotation(180);
                                } else {
                                    Collections.sort(qts, new QuestionFormat.ByBody());
                                    Collections.reverse(qts);
                                    sortButton.setRotation(0);
                                }

                                qa.notifyDataSetChanged();
                            }
                            if (date_check.isChecked()) {
                                if (sortButton.getRotation() == 0) {
                                    Collections.sort(qts, new QuestionFormat.ByDate());
                                    sortButton.setRotation(180);
                                } else {
                                    Collections.sort(qts, new QuestionFormat.ByDate());
                                    Collections.reverse(qts);
                                    sortButton.setRotation(0);
                                }

                                qa.notifyDataSetChanged();
                            }
                            if (answers_check.isChecked()) {
                                if (sortButton.getRotation() == 0) {
                                    Collections.sort(qts, new QuestionFormat.ByReply());
                                    sortButton.setRotation(180);
                                } else {
                                    Collections.sort(qts, new QuestionFormat.ByReply());
                                    Collections.reverse(qts);
                                    sortButton.setRotation(0);
                                }

                                qa.notifyDataSetChanged();
                            }
                            if (votes_check.isChecked()) {
                                if (sortButton.getRotation() == 0) {
                                    Collections.sort(qts, new QuestionFormat.ByVotes());
                                    sortButton.setRotation(180);
                                } else {
                                    Collections.sort(qts, new QuestionFormat.ByVotes());
                                    Collections.reverse(qts);
                                    sortButton.setRotation(0);
                                }

                                qa.notifyDataSetChanged();
                            }


                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();


                }
            });


            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent(MainActivity.this,SettingsPage.class);
                    startActivity(intent);

//                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                    startActivity(intent);
                }
            });


            notificationStatus = sharedPreferences.getString("Status", null);

            if(notificationStatus!=null){
                if(notificationStatus.equals("on")){
                    FirebaseMessaging.getInstance().subscribeToTopic("QA");
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("QA");
                }
            }

            SharedPreferences settings = getSharedPreferences(MyPREFERENCES, 0);
            //settings.edit().putBoolean("dialogShown",true);
            //settings.edit().clear();
            boolean dialogShown = settings.getBoolean("dialogShown", false);

            if (!dialogShown) {

                AlertDialog.Builder builder0 = new AlertDialog.Builder(MainActivity.this);
                builder0.setTitle("Take Tour");
                builder0.setMessage("Take a short tour about this app");
                builder0.setCancelable(false);
                builder0.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Logged In-Out Button");
                        builder.setMessage("You can simply use it to log in or log out from your account");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                builder1.setTitle("Sort Button");
                                builder1.setMessage("It allows you to sort questions based on title,body,date,time,answers and votes");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                                        builder2.setTitle("Switch");
                                        builder2.setMessage("You can turn it on when logged in to view your posted questions");
                                        builder2.setCancelable(false);
                                        builder2.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
                                                builder3.setTitle("Search button");
                                                builder3.setMessage("Simply search by tags,title and body");
                                                builder3.setCancelable(false);
                                                builder3.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        AlertDialog.Builder builder4 = new AlertDialog.Builder(MainActivity.this);
                                                        builder4.setTitle("Action Button");
                                                        builder4.setCancelable(false);
                                                        builder4.setMessage("It allows you to add questions,get tag information as well as gain information about active and past users with their detailed activity");
                                                        builder4.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                                SharedPreferences.Editor editor = settings.edit();
                                                                editor.putBoolean("dialogShown", true);
                                                                editor.commit();
                                                                ShowStartupDialog();
                                                            }
                                                        });
                                                        AlertDialog alertDialog4 = builder4.create();
                                                        alertDialog4.show();
                                                        WindowManager.LayoutParams layoutParams4 = new WindowManager.LayoutParams();
                                                        layoutParams4.copyFrom(alertDialog4.getWindow().getAttributes());
                                                        layoutParams4.width = 500;
                                                        layoutParams4.height = 500;
                                                        layoutParams4.x = 0;
                                                        layoutParams4.y = 850;
                                                        alertDialog4.getWindow().setAttributes(layoutParams4);
                                                    }
                                                });
                                                AlertDialog alertDialog3 = builder3.create();
                                                alertDialog3.show();
                                                WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams();
                                                layoutParams3.copyFrom(alertDialog3.getWindow().getAttributes());
                                                layoutParams3.width = 500;
                                                layoutParams3.height = 400;
                                                layoutParams3.x = -100;
                                                layoutParams3.y = -850;
                                                alertDialog3.getWindow().setAttributes(layoutParams3);
                                            }
                                        });
                                        AlertDialog alertDialog2 = builder2.create();
                                        alertDialog2.show();
                                        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
                                        layoutParams2.copyFrom(alertDialog2.getWindow().getAttributes());
                                        layoutParams2.width = 500;
                                        layoutParams2.height = 500;
                                        layoutParams2.x = -350;
                                        layoutParams2.y = -1050;
                                        alertDialog2.getWindow().setAttributes(layoutParams2);
                                    }
                                });
                                AlertDialog alertDialog1 = builder1.create();
                                alertDialog1.show();
                                WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams();
                                layoutParams1.copyFrom(alertDialog1.getWindow().getAttributes());
                                layoutParams1.width = 500;
                                layoutParams1.height = 500;
                                layoutParams1.x = -80;
                                layoutParams1.y = -1050;
                                alertDialog1.getWindow().setAttributes(layoutParams1);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                        layoutParams.width = 500;
                        layoutParams.height = 500;
                        layoutParams.x = 50;
                        layoutParams.y = -1050;
                        alertDialog.getWindow().setAttributes(layoutParams);
                    }
                });
                builder0.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("dialogShown", true);
                        editor.commit();
                    }
                });

                builder0.show();
            }
            else{
                ShowStartupDialog();
            }


//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        // Build a GoogleSignInClient with the options specified by gso.
//        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
//                .enableAutoManage(MainActivity.this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

            //Log.d("message",mGoogleApiClient.toString());

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //grant the permission
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
            }


            button = findViewById(R.id.add_btn);
            ques = findViewById(R.id.container_questions);
            users = findViewById(R.id.container_users);
            tags = findViewById(R.id.container_tags);
            tags_btn = findViewById(R.id.fab_tags);
            ques_btn = findViewById(R.id.fab_questions);
            users_btn = findViewById(R.id.fab_users);
            speechlyBtn=findViewById(R.id.speechly_btn);

            TextView textView=findViewById(R.id.textSpeechly);

            Client speechlyClient= Client.Companion.fromActivity(MainActivity.this, UUID.fromString("7183f0cf-c274-44c4-9b2d-c07fe2f1da8c"), StreamConfig.LanguageCode.EN_US,"api.speechly.com",true);

            SpeechlyClass speechlyClass=new SpeechlyClass(speechlyBtn,speechlyClient,textView);

            speechlyBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            speechlyClient.stopContext();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setVisibility(View.GONE);
                                }
                            },500);
                        } finally {
                            List<String> res=speechlyClass.getList();
                            if(res!=null){
                                switch(res.get(0)){
                                    case "setting":
                                        Intent intent1=new Intent(MainActivity.this,SettingsPage.class);
                                        startActivity(intent1);
//                                        Intent intent1=new Intent(MainActivity.this,SettingsActivity.class);
//                                        startActivity(intent1);
                                        break;
                                    case "show_all_tags":
                                        Intent intent2 = new Intent(MainActivity.this, Tags.class);
                                        startActivity(intent2);
                                        break;
                                    case "show_all_users":
                                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                        startActivity(intent);
                                        break;
                                    case "reset":
                                    case "show_all":
                                        switchPush.setChecked(false);
                                        resetAll();
                                        break;
                                    case "show_mine":
                                        if(isLogin()==false){
                                            Toast.makeText(MainActivity.this,"You need to login to see your posted questions",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            switchPush.setChecked(true);
//                                            getUserQuestions(sharedPreferences.getString("email", null));
                                        }
                                        break;
                                    case "sort":
                                        sortByWord(res.get(1),res.get(2));
                                        break;
                                    case "search":
                                        searchByTag(res.get(1));
                                        break;
                                    case "posting":
                                        if(res.get(1)!=null){
                                            if(res.get(1).toLowerCase().equals("question")){
                                                postQuestion();
                                            }
                                        }
                                        break;
                                }
                            }
                        }

                    }
                    else if(event.getAction()==MotionEvent.ACTION_DOWN){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("I am listening. Try to speak something");
                        speechlyClient.startContext();
                    }
                    return true;
                }
            });
            retreive_from_firebase();

            onNewIntent(getIntent());


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (users.isShown()) {
                        users.animate().alpha(0.0f).setDuration(300);
                        tags.animate().alpha(0.0f).setDuration(300);
                        ques.animate().alpha(0.0f).setDuration(300);
                        users.setVisibility(View.INVISIBLE);
                        tags.setVisibility(View.INVISIBLE);
                        ques.setVisibility(View.INVISIBLE);
                        button.animate().rotation(0);
                    } else {
                        users.setVisibility(View.VISIBLE);
                        tags.setVisibility(View.VISIBLE);
                        ques.setVisibility(View.VISIBLE);
                        users.animate().alpha(1.0f).setDuration(300);
                        tags.animate().alpha(1.0f).setDuration(300);
                        ques.animate().alpha(1.0f).setDuration(300);
                        button.animate().rotation(45);
                    }
                }
            });

            tags_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    users.setVisibility(View.INVISIBLE);
                    tags.setVisibility(View.INVISIBLE);
                    ques.setVisibility(View.INVISIBLE);
                    button.animate().rotation(0);
                    Intent intent = new Intent(MainActivity.this, Tags.class);
                    startActivity(intent);
                }
            });


            ques_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                users.setVisibility(View.INVISIBLE);
                                                tags.setVisibility(View.INVISIBLE);
                                                ques.setVisibility(View.INVISIBLE);
                                                button.animate().rotation(0);
                                                postQuestion();
                }
            });

            users_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    users.setVisibility(View.INVISIBLE);
                    tags.setVisibility(View.INVISIBLE);
                    ques.setVisibility(View.INVISIBLE);
                    button.animate().rotation(0);
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);

                }
            });

            try {
                interpreter = new Interpreter(loadModelFile(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            strings = new ArrayList<>();

            BufferedReader objReader = null;
            try {
                String strCurrentLine;

                objReader = new BufferedReader(new InputStreamReader(this.getAssets().open("Name.csv")));

                Log.d("message", "found");

                while ((strCurrentLine = objReader.readLine()) != null) {

                    strings.add(strCurrentLine);
                }
                Log.d("message", strings.size() + " ");

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                try {
                    if (objReader != null)
                        objReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


            new AsyncTask<Integer, Double, Integer>() {
                //private ProgressDialog mDialog;

                @Override
                protected void onPreExecute() {
                    progressBar = new ProgressDialog(MainActivity.this);
                    progressBar.setCancelable(false);
                    progressBar.setTitle("Please wait ...");
                    progressBar.setMessage("Retrieving related data ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressBar.setProgress(0);
                    progressBar.setMax(100);
                    progressBar.show();
                    //mDialog = ProgressDialog.show(MainActivity.this,"Please wait...", "Retrieving related data ...", true);

                }

                @Override
                protected Integer doInBackground(Integer... index) {
                    if (isLogin() == false) {
                        switchPush.setClickable(false);
                        Snackbar snackbar = Snackbar
                                .make(layout, "Not logged in", Snackbar.LENGTH_LONG).setBackgroundTint(Color.RED);

                        snackbar.show();
                    } else {
                        switchPush.setClickable(true);
                        Snackbar snackbar = Snackbar
                                .make(layout, "You are logged in", Snackbar.LENGTH_LONG).setBackgroundTint(Color.GREEN);

                        snackbar.show();
                    }

                    question = new ArrayList<>();
                    final Double[] i = {0.0};

                    try (final Reader reader = new InputStreamReader(MainActivity.this.getAssets().open("finalised.json"))) {
                        final JSONParser jsonParser = new JSONParser();
                        jsonParser.parse(reader, new ContentHandler() {
                            private String key;
                            private Object value;

                            // A bunch of "default" methods
                            @Override
                            public void startJSON() {
                            }

                            @Override
                            public void endJSON() {
                            }

                            @Override
                            public boolean startObject() {
                                return true;
                            }

                            @Override
                            public boolean endObject() {
                                return true;
                            }

                            @Override
                            public boolean startArray() {
                                return true;
                            }

                            @Override
                            public boolean endArray() {
                                return true;
                            }

                            @Override
                            public boolean startObjectEntry(final String key) {
                                this.key = key;
                                return true;
                            }

                            @Override
                            public boolean endObjectEntry() {
                                i[0] = i[0] + 0.00164;
                                publishProgress(i[0]);
                                //Log.d("message",key + " => " + value);
                                Pair<String, String> p = new Pair(key, value);
                                question.add(p);
                                return true;
                            }

                            @Override
                            public boolean primitive(final Object value) {
                                this.value = value;
                                return true;
                            }
                        });
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(Double... values) {
                    //Log.d("message","Running");
                    //progressBar.setProgress(values[0]);
                    progressBar.setProgress(values[0].intValue());
                }

                @Override
                protected void onPostExecute(Integer integer) {


                    qts = new ArrayList<>();
                    for (QuestionFormat questionFormat : questions) {
                        List<String> qt_b = new ArrayList<>();
                        List<String> qt_t = new ArrayList<>();
                        for (int i : questionFormat.getIndexes()) {
                            qt_t.add(question.get(i).first);
                            qt_b.add(question.get(i).second);
                        }
                        questionFormat.setQuestion_body(qt_b);
                        questionFormat.setQuestion_title(qt_t);
                        qts.add(questionFormat);
                    }

                    Log.d("message",String.valueOf(qts.size()));

                    setList(qts);

                    progressBar.dismiss();


                }
            }.execute();

        }

    }

    AlertDialog aDialog;

    private void ShowStartupDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.start_up_dialog, null);
            builder.setView(dialogView);
            aDialog = builder.create();

            WebView webView = (WebView)dialogView.findViewById(R.id.txtDefault);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_asset/test.html");
            aDialog.setCanceledOnTouchOutside(false);
            aDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(aDialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 1.0f);
        int dialogWindowHeight = (int) (displayHeight * 0.82f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        aDialog.getWindow().setAttributes(layoutParams);


    }

    private void postQuestion() {

        if (!isLogin()) {
            Toast.makeText(MainActivity.this, "You need to login to post questions.", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.add_question_dialog_box, null);
            builder.setView(dialogView);
            alertDialog = builder.create();

            ques_add = dialogView.findViewById(R.id.add_button_dialog);
            cancel_dialog = dialogView.findViewById(R.id.cancel_button_dialog);
            title = dialogView.findViewById(R.id.title_edit_box);
            description = dialogView.findViewById(R.id.question_body_dialog);
            spinner = dialogView.findViewById(R.id.read_option_list);

            alertDialog.show();


            // Spinner click listener
            spinner.setOnItemSelectedListener(MainActivity.this);

            // Spinner Drop down elements
            categories = new ArrayList<String>();
            categories.add("Enter the text");
            categories.add("Read from Picture");

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, categories);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter);


            ques_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (title.getText().toString().trim().equals("") || description.getText().toString().trim().equals("")) {
                        Toast.makeText(MainActivity.this, "Specify title and description of your problem", Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialog.dismiss();

                        new AsyncTask<Void, Double, Void>() {
                            private ProgressDialog progressBar;

                            @Override
                            protected void onPreExecute() {
                                progressBar = new ProgressDialog(MainActivity.this);
                                progressBar.setCancelable(false);
                                progressBar.setTitle("Please wait ...");
                                progressBar.setMessage("Posting Question ...");
                                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressBar.setProgress(0);
                                progressBar.setMax(100);
                                progressBar.show();
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {

                                String t = title.getText().toString().trim();
                                String desc = description.getText().toString().trim();
                                List<String> tags = get_result(strings, t);
                                String q = t + " " + desc;
                                List<Integer> integers = new ArrayList<>();

                                SimilarityStrategy similarity = new JaroWinklerStrategy();
                                StringSimilarityService service = new StringSimilarityServiceImpl(similarity);
                                int index = -1;
                                double f = 0.0;
                                double similar;


                                Double j = 0.0;

                                Log.d("message", String.valueOf(question.size()));
                                for (int i = 0; i < question.size(); i++) {
                                    j = j + 0.00164;
                                    publishProgress(j);

                                    String s = question.get(i).first.toLowerCase() + " " + Jsoup.parse(question.get(i).second).text().toLowerCase();

                                    similar = service.score(q.toLowerCase(), s.toLowerCase());
                                    if (f < similar) {
                                        //Log.d("message",similar+" ");
                                        f = similar;
                                        index = i;
                                    }
                                    if (similar > 0.7) {
                                        integers.add(i);
                                    }
                                    if (integers.size() > 5)
                                        break;
                                }
                                if (integers.size() == 0) {
                                    integers.add(index);
                                }

                                addQuestion(integers, tags, t, desc);
                                publishProgress(100.00);
                                return null;
                            }

                            @Override
                            protected void onProgressUpdate(Double... values) {
                                progressBar.setProgress(values[0].intValue());
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                progressBar.dismiss();
                                MyFirebaseMessagingService service=new MyFirebaseMessagingService();
                                service.sendMessage(sharedPreferences.getString("email",null),qts.size()-1);
                                qa.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Question Added Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }.execute();

                    }


                }
            });
            cancel_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        }
    }

    private void searchByTag(String s) {
        if(s==null)
            return ;
        filterData(s,"tag");

    }

    private void sortByWord(String say, String order) {
        if(say==null){
            return ;
        }
        if(order==null){
            order="increasing";
        }
        Log.d("message","sort"+" "+say+" "+order);
        switch(say.toLowerCase()){
            case "title":
                if(order.equals("increasing")){
                    Collections.sort(qts);
                }
                else if(order.equals("decreasing")){
                    Collections.sort(qts, Collections.reverseOrder());
                }
                qa.notifyDataSetChanged();
                break;
            case "body":
                if(order.equals("increasing")){
                    Collections.sort(qts, new QuestionFormat.ByBody());
                }
                else if(order.equals("decreasing")){
                    Collections.sort(qts, new QuestionFormat.ByBody());
                    Collections.reverse(qts);
                }
                qa.notifyDataSetChanged();
                break;
            case "date":
            case "time":
                if(order.equals("increasing")){
                    Collections.sort(qts, new QuestionFormat.ByDate());
                }
                else if(order.equals("decreasing")){
                    Collections.sort(qts, new QuestionFormat.ByDate());
                    Collections.reverse(qts);
                }
                qa.notifyDataSetChanged();
                break;
            case "votes":
                if(order.equals("increasing")){
                    Collections.sort(qts, new QuestionFormat.ByVotes());
                }
                else if(order.equals("decreasing")){
                    Collections.sort(qts, new QuestionFormat.ByVotes());
                    Collections.reverse(qts);
                }
                qa.notifyDataSetChanged();
                break;
            case "answers":
            case "reply":
            case "replies":
                if(order.equals("increasing")){
                    Collections.sort(qts, new QuestionFormat.ByReply());
                }
                else if(order.equals("decreasing")){
                    Collections.sort(qts, new QuestionFormat.ByReply());
                    Collections.reverse(qts);
                }
                qa.notifyDataSetChanged();
                break;

        }
    }

    private void getUserQuestions(String email) {
        db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    User u=snapshot.toObject(User.class);
                    if(u.getEmail().equals(email)){
                        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                qts=new ArrayList<>();
                                for(QueryDocumentSnapshot s:queryDocumentSnapshots){
                                    QuestionFormat quesformat=s.toObject(QuestionFormat.class);
                                    if(quesformat.getUser_id().equals(u.getId())){
                                        List<String> qt_b = new ArrayList<>();
                                        List<String> qt_t = new ArrayList<>();
                                        for (int i : quesformat.getIndexes()) {
                                            qt_t.add(question.get(i).first);
                                            qt_b.add(question.get(i).second);
                                        }
                                        quesformat.setQuestion_body(qt_b);
                                        quesformat.setQuestion_title(qt_t);
                                        qts.add(quesformat);
                                    }
                                }
                                setList(qts);
                            }
                        });
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras() ;
        if (extras != null ) {
            if (extras.containsKey( "NotificationMessage" )) {
                Integer msg =  Integer.parseInt(extras.getString( "NotificationMessage" )) ;
                Log.d("message",msg.toString());
                rv.getLayoutManager().scrollToPosition(msg);
                qts.get(msg).setSelected(true);
                qa.notifyItemChanged(msg);
            }
        }
    }

    private void sendNotification() {

        Intent notificationIntent = new Intent(MainActivity. this, MainActivity. class ) ;
        notificationIntent.putExtra("NotificationMessage",String.valueOf(qts.size()-2));
        notificationIntent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        notificationIntent.setAction(Intent. ACTION_MAIN ) ;
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
        //change request code
        String Email=sharedPreferences.getString("email",null);
        PendingIntent resultIntent = PendingIntent. getActivity (MainActivity. this, qts.size()-2 , notificationIntent , 0 ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder
                (MainActivity. this, default_notification_channel_id )
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
                .setContentTitle( "User with email : "+Email+" just posted a question" )
                .setContentText( "Tap to see it..." )
                .setContentIntent(resultIntent) ;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
    }


//        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
//        builder.setSmallIcon(R.drawable.ic_baseline_circle_notifications);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_circle_notifications);
//        builder.setLargeIcon(bitmap);
//        builder.setContentTitle("User just posted a question");
//        builder.setContentText("Tap to see it.");
//        int notificationId = 1;
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(notificationId, builder.build());

    private void addQuestion(List<Integer> integers,List<String> tags, String t, String desc) {
        String userEmail=sharedPreferences.getString("email",null);
        List<Integer> finalIntegers =new ArrayList<>();
        for(int i=0;i<Math.min(integers.size(),5);i++){
            finalIntegers.add(integers.get(i));
        }
        db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    User user=snapshot.toObject(User.class);
                    if(user.getEmail().equals(userEmail)){
                        QuestionFormat questionFormat=new QuestionFormat(t,desc,new Date(),tags,user.getId(), finalIntegers);
                        save_to_firebase(questionFormat);
                        List<String> qts_b=new ArrayList<>();
                        List<String> qts_t=new ArrayList<>();
                        for(int i:questionFormat.getIndexes()){
                            qts_b.add(question.get(i).second);
                            qts_t.add(question.get(i).first);
                        }
                        questionFormat.setQuestion_body(qts_b);
                        questionFormat.setQuestion_title(qts_t);
                        qts.add(questionFormat);
                        qa.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    private void setList(List<QuestionFormat> lists){

        qa=new QuestionAdapter(MainActivity.this,lists);
        rv.setAdapter(qa);

        LinearLayoutManager lm=new LinearLayoutManager(MainActivity.this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(lm);

        rv.setItemAnimator(new DefaultItemAnimator());
    }


    private void filterData(String query, String searchBy) {
        qqts=new ArrayList<>(qts);
        List<QuestionFormat> temp=new ArrayList<>();
        if(searchBy.equals("tag") && !query.trim().equals("")){
            List<String> tag_strings= Arrays.asList(query.toLowerCase().split(","));
            for(QuestionFormat question:qts){
                for(String string:tag_strings){
                    if(question.getTag_list().contains(string)){
                        //Log.d("message",question.getTitle());
                        temp.add(question);
                        break;
                    }
                }
            }

        }
        else if(searchBy.equals("title") && !query.trim().equals("")){
            for(QuestionFormat question:qts){
                if(question.getTitle().contains(query)){
                    Log.d("message",question.getTitle());
                    temp.add(question);
                }
            }
        }
        else if(searchBy.equals("body") && !query.trim().equals("")){
            for(QuestionFormat question:qts){
                if(question.getBody().contains(query)){
                    Log.d("message",question.getTitle());
                    temp.add(question);
                }
            }
        }
        qqts=new ArrayList<>(qts);
        qts=new ArrayList<>(temp);
        setList(temp);
    }

    private void retreive_from_firebase() {
        questions=new ArrayList<>();
        ref.orderBy("postingDate").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){


                    QuestionFormat q=documentSnapshot.toObject(QuestionFormat.class);
                    q.setId(documentSnapshot.getId());
                    questions.add(q);
                }

               // Log.d("message","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("message","failed");
            }
        });


    }

    public void resetAll(){
        questions=new ArrayList<>();
        ref.orderBy("postingDate").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){


                    QuestionFormat q=documentSnapshot.toObject(QuestionFormat.class);
                    q.setId(documentSnapshot.getId());
                    questions.add(q);
                }

                qts = new ArrayList<>();
                for (QuestionFormat questionFormat : questions) {
                    List<String> qt_b = new ArrayList<>();
                    List<String> qt_t = new ArrayList<>();
                    for (int i : questionFormat.getIndexes()) {
                        qt_t.add(question.get(i).first);
                        qt_b.add(question.get(i).second);
                    }
                    questionFormat.setQuestion_body(qt_b);
                    questionFormat.setQuestion_title(qt_t);
                    qts.add(questionFormat);
                }

                Log.d("message",String.valueOf(qts.size()));
                setList(qts);

                // Log.d("message","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("message","failed");
            }
        });
    }


    private void save_to_firebase(QuestionFormat questionFormat) {

        final String[] id = {""};
        //QuestionFormat ques=new QuestionFormat(questionFormat.getTitle(),questionFormat.getBody(),questionFormat.getPostingDate(),questionFormat.getTag_list(),question);
        ref.add(questionFormat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                id[0] =documentReference.getId();
                questionFormat.setId(id[0]);
                ref.document(id[0]).set(questionFormat).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("User").document(questionFormat.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user=documentSnapshot.toObject(User.class);
                                user.updateQuestions();
                                db.collection("User").document(user.getId()).set(user);
                            }
                        });
                    }
                });
            }
        });

    }

    private MappedByteBuffer loadModelFile() throws IOException{
        AssetFileDescriptor assetFileDescriptor=this.getAssets().openFd("model_1.tflite");
        FileInputStream fileInputStream=new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel=fileInputStream.getChannel();
        long startOffset=assetFileDescriptor.getStartOffset();
        long length=assetFileDescriptor.getLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,length);
    }


    public List<String> get_result(List<String> strings, String words){
        List<String> lists=new ArrayList<>();
        StringTokenizer st = new StringTokenizer(words," ");
        while (st.hasMoreTokens()) {
            lists.add(st.nextToken().toLowerCase());
            //System.out.println(st.nextToken());
        }

        float[] input=new float[15000];
        for(int i=0;i<15000;i++){
            if(check(lists,strings.get(i))==true){
                input[i]=1.0f;
            }
            else{
                input[i]=0.0f;
            }
        }


        float[][] output=new float[1][20];
        interpreter.run(input,output);
        int index=0;
        String[] mapping=new String[]{".net", "android", "asp.net", "c", "c#", "c++", "css", "html",
                "ios", "iphone", "java", "javascript", "jquery", "mysql",
                "objective-c", "php", "python", "ruby", "ruby-on-rails", "sql"};
        List<String> tags=new ArrayList<String>();
        float max_val=0.0f;
        int max_index=-1;
        for(float val:output[0]){
            Log.d("message",val+" ");
            if(val>0.5f){
                tags.add(mapping[index]);
            }
            if(max_val<val){
                max_val=val;
                max_index=index;
            }
            index++;
        }
        if(tags.size()==0){
            tags.add(mapping[max_index]);
        }
        // tags.add(mapping[max_index]);
        return tags;
    }

    private boolean check(List<String> words, String s) {
        for(String str:words){
            if(str.equals(s)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selection=adapterView.getItemAtPosition(i).toString();
        if(selection.equals(categories.get(1))){
            adapterView.setSelection(0);
            //alertDialog.dismiss();
            openDialog();
        }

        //Toast.makeText(MainActivity.this,selection,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(MainActivity.this,"Nothing Selected",Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//           // Log.d("message","signed in...");
//
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
        if(requestCode ==0 && resultCode==RESULT_OK){
            cropImages();
        }
        else if(requestCode ==2){
            if(data!=null){
                uri=data.getData();
                cropImages();
            }
        }
        else if(requestCode ==1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();

                detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {

                        processExtractedText(firebaseVisionText);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this,
                                "Exception", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,
                    "Permission Granted , Now your application can access Camera and Microphone",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.this,
                    "Permission Not Granted , Now your application can not access Camera/Microphone",
                    Toast.LENGTH_SHORT).show();
        }

    }
    private void processExtractedText(FirebaseVisionText firebaseVisionText) {
        description.setText(null);
        if (firebaseVisionText.getBlocks().size() == 0) {
            description.setText("No Text Found");
            return;
        }
        for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
            description.append(block.getText());

        }
    }


    boolean isLogin(){
       // SharedPreferences.Editor editor=sharedPreferences.edit();
        String email=sharedPreferences.getString("email",null);
        if(email==null)
            return false;
        Boolean login=sharedPreferences.getBoolean("islogin"+" "+email,false);
        return login;

    }

//    void loginGoogle(){
//
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    void logoutGoogle(){
//        switchPush.setClickable(false);
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()){
//                            String email=sharedPreferences.getString("email",null);
//                            if(email!=null){
//                                Set<String> set=sharedPreferences.getStringSet(email,null);
//
//                                if(set!=null){
//                                    SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//                                    Date date = new Date();
////                                    User user=new User((String) set.toArray()[1],email,format.format(date),format.format(date.getDate()), com.example.myapplication.status.offline);
////                                    user.setId((String)set.toArray()[0]);
//                                    db.collection("User").document((String) set.toArray()[0]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                            User curr=documentSnapshot.toObject(User.class);
//                                            curr.setStat(com.example.myapplication.status.offline);
//                                            curr.setLast_login_date(format.format(date));
//                                            curr.setLast_login_time(format.format(date));
//                                            db.collection("User").document(curr.getId()).set(curr);
//                                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                                            editor.putBoolean("islogin"+" "+email,false);
//                                            editor.commit();
//                                        }
//                                    });
//
//                                }
//                            }
//                        }
//                    }
//                });
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            if (account != null) {
//
//                db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        boolean ff=false;
//                        for (QueryDocumentSnapshot snapshot :queryDocumentSnapshots){
//                            if(snapshot.toObject(User.class).getStat().equals(status.online) && snapshot.toObject(User.class).getEmail().equals(account.getEmail())){
//                                ff=true;
//                                Toast.makeText(MainActivity.this,
//                                        "This account is already logged in on another device.Go to your account settings for more details",
//                                        Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                        }
//                        if(ff==false){
//                            //Log.d("message",account.getEmail()+" "+account.getDisplayName()+" "+account.getPhotoUrl());
//                            switchPush.setClickable(true);
//                            Toast.makeText(MainActivity.this, "You have successfully logged in", Toast.LENGTH_SHORT).show();
//
//                            User user = new User(account.getDisplayName(), account.getEmail(), "", "", status.online);
//                            db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    boolean f = false;
//                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
//                                        User user1 = snapshot.toObject(User.class);
//                                        if (user1.getEmail().equals(user.getEmail())) {
//                                            f = true;
//                                            user1.setStat(status.online);
//                                            user1.setLast_login_time("");
//                                            user1.setLast_login_date("");
//                                            //user.setId(sharedPreferences.getStringSet(user.getEmail(),null).stream().collect(Collectors.toList()).get(0));
//                                            db.collection("User").document(user1.getId()).set(user1);
//                                            Set<String> set = new HashSet<String>();
//                                            set.add(user1.getId());
//                                            set.add(user1.getName());
//                                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                                            editor.putString("email", account.getEmail());
//                                            editor.putBoolean("islogin" + " " + account.getEmail(), true);
//                                            editor.putStringSet(account.getEmail(), set);
//                                            editor.commit();
//                                            break;
//                                        }
//                                    }
//                                    if (f == false) {
//                                        db.collection("User").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                            @Override
//                                            public void onSuccess(DocumentReference documentReference) {
//                                                user.setId(documentReference.getId());
//                                                db.collection("User").document(user.getId()).set(user);
//                                                user_list.add(user);
//                                                Set<String> set = new HashSet<String>();
//                                                set.add(user.getId());
//                                                set.add(user.getName());
//                                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                editor.putString("email", account.getEmail());
//                                                editor.putBoolean("islogin" + " " + account.getEmail(), true);
//                                                editor.putStringSet(account.getEmail(), set);
//                                                editor.commit();
//                                            }
//                                        });
//                                    }
//                                }
//                            });
//                        }
//                    }
//                });
//
//            }
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            //Log.w("message", "signInResult:failed code=" + e.getStatusCode());
//            //updateUI(null);
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        //Log.d("message","connection Failed");
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aDialog != null) {
            aDialog.dismiss();
            aDialog = null;
        }
        if(progressBar!=null){
            progressBar.dismiss();
            progressBar = null;
        }
    }

    private void enableRuntimepermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA) == true){
            Toast.makeText(MainActivity.this,"Camera Permission allows us to Camera App and Microphone allows us to record audio",Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{(Manifest.permission.CAMERA),(Manifest.permission.RECORD_AUDIO)},101);

        }

    }

    private void openDialog() {
        android.app.AlertDialog.Builder openDialog=new android.app.AlertDialog.Builder(MainActivity.this);
        openDialog.setIcon(R.drawable.ic_action_image);
        openDialog.setTitle("Choose your Image in...!!");

        openDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openCamera();
                dialogInterface.dismiss();

            }
        });
        openDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openGallery();
                dialogInterface.dismiss();
            }
        });
        openDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog dialog=openDialog.create();
        dialog.show();
    }

    private void openCamera(){
        camIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file=new File(Environment.getExternalStorageDirectory(),"file"+System.currentTimeMillis()+".jpg");
        uri= Uri.fromFile(file);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        camIntent.putExtra("return-data",true);
        startActivityForResult(camIntent,0);
    }

    private void openGallery(){
        galIntent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galIntent,"Select Image From Gallery"),2);
    }

    private void cropImages(){
        cropIntent=new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri,"image/*");
        cropIntent.putExtra("crop",true);
        cropIntent.putExtra("outputX",180);
        cropIntent.putExtra("outputY",180);
        cropIntent.putExtra("aspectX",3);
        cropIntent.putExtra("aspectY",4);
        cropIntent.putExtra("scaleUpIfNeeded",true);
        cropIntent.putExtra("return-data",true);
        startActivityForResult(cropIntent,1);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("message","resume");
        super.onResume();
       // this.recreate();
       // onCreate(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("message","restart");

        switchPush.setChecked(false);
        if(isLogin()==false){
            switchPush.setClickable(false);
        }
        else{
            switchPush.setClickable(true);
        }

        notificationStatus = sharedPreferences.getString("Status", null);
        if(notificationStatus!=null){
            if(notificationStatus.equals("on")){
                FirebaseMessaging.getInstance().subscribeToTopic("QA");
            }
            else{
                FirebaseMessaging.getInstance().unsubscribeFromTopic("QA");
            }
        }

        if(question!=null && question.size()>=60870) {
            questions = new ArrayList<>();
            ref.orderBy("postingDate").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                        QuestionFormat q = documentSnapshot.toObject(QuestionFormat.class);
                        q.setId(documentSnapshot.getId());
                        questions.add(q);
                    }
                    qts = new ArrayList<>();
                    for (QuestionFormat questionFormat : questions) {
                        List<String> qt_b = new ArrayList<>();
                        List<String> qt_t = new ArrayList<>();
                        for (int i : questionFormat.getIndexes()) {
                            qt_t.add(question.get(i).first);
                            qt_b.add(question.get(i).second);
                        }
                        questionFormat.setQuestion_body(qt_b);
                        questionFormat.setQuestion_title(qt_t);
                        qts.add(questionFormat);
                    }

                    setList(qts);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Log.d("message","failed");
                }
            });

        }

    }


    public static String getConnectivityStatusString(Context context) {
        String status = null;
        ConnectivityManager cm = (ConnectivityManager)           context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = "Wifi enabled";
                return status;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = "Mobile data enabled";
                return status;
            }
        } else {
            status = "No internet is available";
            return status;
        }
        return status;
    }
}