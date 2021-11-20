package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.speechly.client.slu.StreamConfig;
import com.speechly.client.speech.Client;
import com.speechly.ui.SpeechlyButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralSettings extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    TextView email;
    SwitchCompat switchPush;
    CardView cardView;
    static final int RC_SIGN_IN = 100;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();



    private SharedPreferences sharedPreferences;

    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;
    AppCompatButton signin,privacy,feedback,help_btn,profile_btn;

    FirebaseAuth auth;

    User user;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneralSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralSettings newInstance(String param1, String param2) {
        GeneralSettings fragment = new GeneralSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Context context;

    static Client speechlyClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_general_settings, container, false);

        auth=FirebaseAuth.getInstance();
        SpeechlyButton speechlyBtn;

        context=getContext();

        email=view.findViewById(R.id.email_signin_id);
        switchPush=view.findViewById(R.id.switch_push_notifications);
        profile_btn=view.findViewById(R.id.btn_profile_id);
        cardView=view.findViewById(R.id.profile_id_user);
        signin=view.findViewById(R.id.btn_signin_signout);
        privacy=view.findViewById(R.id.btn_privacy_id);
        feedback=view.findViewById(R.id.btn_feedback_id);
        help_btn=view.findViewById(R.id.btn_help_id);
        speechlyBtn=view.findViewById(R.id.speechly_btn_settings);

        TextView txt=view.findViewById(R.id.textSpeechlyId);

        SpeechlyClass speechlyClass=new SpeechlyClass(speechlyBtn,speechlyClient,txt);

        speechlyBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    try {
                        speechlyClient.stopContext();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txt.setVisibility(View.GONE);
                            }
                        },500);
                    } finally {
                        List<String> res=speechlyClass.getList();
                        if(res!=null) {
                            switch(res.get(0)){
                                case "rating":
                                    if(isLogin()==false){
                                        Toast.makeText(context,"You need to login to give feedback",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (res.get(1) != null) {
                                            int num = getNumber(res.get(1));
                                            if (num == -1) {
                                                Toast.makeText(context, "Not a valid rating. Please go to feedback section for more details...", Toast.LENGTH_SHORT).show();
                                            } else if (num > 0 && num <= 3) {
                                                Toast.makeText(context, "You need to provide suggestions in feedback section for such rating.", Toast.LENGTH_SHORT).show();
                                            } else if (num > 3 && num <= 5) {
                                                Toast.makeText(context, "You give " + res.get(1) + " rating to this app . Thank for your feedback", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(context, "Not a valid rating. Please go to feedback section for more details...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;
                                case "logout":
                                    if(isLogin()==false){
                                        Toast.makeText(context,"You are already logged out",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        SignOut();
                                    }
                                    break;
                                case "login":
                                    if(isLogin()==true){
                                        Toast.makeText(context,"You are already logged in.Please log out and again log in then",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        if (res.get(1) != null) {
                                            if (res.get(1).toLowerCase().equals("github")) {
                                                loginGithub();
                                            } else if (res.get(1).toLowerCase().equals("google")) {
                                                loginGoogle();
                                            }
                                        }
                                    }
                                    break;
                                case "notification":
                                    if(res.get(1)!=null){
                                        if(res.get(1).toLowerCase().equals("on")){

                                            switchPush.setChecked(true);
                                            SharedPreferences.Editor editor =sharedPreferences.edit();
                                            editor.putString("Status","on");
                                            editor.commit();

                                        }
                                        else if(res.get(1).toLowerCase().equals("off")){
                                            switchPush.setChecked(false);
                                            SharedPreferences.Editor editor =sharedPreferences.edit();
                                            editor.putString("Status","off");
                                            editor.commit();

                                        }
                                    }
                                    break;
                            }
                        }
                    }
                }
                else if(event.getAction()==MotionEvent.ACTION_DOWN){
                    txt.setVisibility(View.VISIBLE);
                    txt.setText("I am listening. Try to speak something");
                    speechlyClient.startContext();
                }
                return true;
            }
        });

        sharedPreferences=context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        user=null;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if(isLogin()){
            String e=sharedPreferences.getString("email",null);
            cardView.setVisibility(View.VISIBLE);
            email.setText(e);
            signin.setText("SIGN OUT");
            getCurrentUser(e);
        }
        else{
            cardView.setVisibility(View.GONE);
            email.setText("You are not signed in...");
            signin.setText("SIGN IN");
        }

        String status=sharedPreferences.getString("Status",null);
        if(status==null){
            switchPush.setChecked(false);
        }
        else{
            if(status.equals("on")){

                switchPush.setChecked(true);
//                FirebaseMessaging.getInstance().subscribeToTopic("QA").addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        switchPush.setChecked(true);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SettingsActivity.this,"Some error occurred. Try after some time",Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
            else{

                switchPush.setChecked(false);

//                FirebaseMessaging.getInstance().unsubscribeFromTopic("QA").addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        switchPush.setChecked(false);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SettingsActivity.this,"Some error occurred. Try after some time",Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        }

        switchPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(b){
                    editor.putString("Status","on");
//                    FirebaseMessaging.getInstance().subscribeToTopic("QA").addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//
//                            editor.commit();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(SettingsActivity.this,"Some error occurred. Try after some time",Toast.LENGTH_SHORT).show();
//                        }
//                    });

                }
                else{
                    editor.putString("Status","off");

//                    FirebaseMessaging.getInstance().unsubscribeFromTopic("QA").addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//
//                            editor.commit();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(SettingsActivity.this,"Some error occurred. Try after some time",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
                editor.commit();

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signin.getText().toString().equals("SIGN IN")){
                    SignIn();

                }
                else{
                    SignOut();

                }
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context,PrivacyActivity.class);
                intent.putExtra("Intent Web","privacy");
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user != null) {
                    Intent intent =new Intent(context,FeedbackActivity.class);
                    intent.putExtra("User Id",user.getId());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(context,"You need to log in to give feedback",Toast.LENGTH_SHORT).show();
                }

            }
        });

        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context,PrivacyActivity.class);
                intent.putExtra("Intent Web","help");
                startActivity(intent);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ProfileActivity.class);
                intent.putExtra("User Id",user.getId());
                startActivity(intent);
            }
        });
        return view;
    }

    private int getNumber(String s) {
        switch(s.toLowerCase()){
            case "one":
                return 1;
            case "two":
                return 2;
            case "three":
                return 3;
            case "four":
                return 4;
            case "five":
                return 5;
        }
        return -1;
    }

    private void getCurrentUser(String e) {
        db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                    User uuser=snapshot.toObject(User.class);
                    if(uuser.getEmail().equals(e)){
                        user=new User(uuser.getName(),uuser.getEmail(),uuser.getLast_login_time(),uuser.getLast_login_date(),uuser.getStat());
                        user.setId(uuser.getId());
                        user.setPlatform(uuser.getPlatform());
                        break;
                    }
                }
            }
        });
    }

    private void SignOut() {
        //Log.d("message",user.getPlatform());
        if(user!=null && user.getPlatform()!=null && user.getPlatform().equals("Git")){
            String email=sharedPreferences.getString("email",null);
            if(email!=null){
                Set<String> set=sharedPreferences.getStringSet(email,null);

                if(set!=null){
                    SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                    Date date = new Date();

                    cardView.setVisibility(View.GONE);
                    this.email.setText("You are not signed in...");
                    signin.setText("SIGN IN");
                    db.collection("User").document(user.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User curr=documentSnapshot.toObject(User.class);
                            curr.setStat(com.example.myapplication.status.offline);
                            curr.setLast_login_date(format.format(date));
                            curr.setLast_login_time(format.format(date));
                            db.collection("User").document(curr.getId()).set(curr);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("islogin"+" "+email,false);
                            editor.commit();
                            user=null;

                        }
                    });

                }
            }
        }
        else {
            logoutGoogle();
        }
    }

    private void SignIn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.login_layout, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        ImageButton gmail=dialogView.findViewById(R.id.img_gmail);
        ImageButton github=dialogView.findViewById(R.id.img_github);

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginGoogle();
                alertDialog.dismiss();
            }
        });

        github.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loginGithub();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void loginGithub() {
        Task<AuthResult> pending=auth.getPendingAuthResult();

        if(pending!=null){
            pending.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(context,"User already signed in...", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            ArrayList<String> scopes = new ArrayList<String>() {
                {
                    add("user:email");
                }
            };
            OAuthProvider.Builder p=OAuthProvider.newBuilder("github.com");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Log in using github:");
            builder.setIcon(R.drawable.github_icon);
            final EditText edit_text = new EditText(context);
            edit_text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            edit_text.setTextColor(Color.BLACK);
            edit_text.setHint("Enter your email here...");
            builder.setView(edit_text);
            builder.setPositiveButton("LOG IN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    p.addCustomParameter("login",edit_text.getText().toString());
                    p.setScopes(scopes);
                    auth.startActivityForSignInWithProvider((Activity) context,p.build())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser firebaseUser=auth.getCurrentUser();
                                    Map<String,Object> mp=authResult.getAdditionalUserInfo().getProfile();
//                                    Log.d("message", (String) mp.get("name"));
//                                    Log.d("message", (String) mp.get("login"));
                                    String name="";
                                    if(mp.get("name")==null){
                                        name=(String)mp.get("login");
                                    }
                                    else{
                                        name=(String)mp.get("name");
                                    }

                                    User u=new User(name,firebaseUser.getEmail(),"","",status.online);
                                    u.setPlatform("Git");


                                    //checking user
                                    db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            boolean ff=false;
                                            for (QueryDocumentSnapshot snapshot :queryDocumentSnapshots){
                                                if(snapshot.toObject(User.class).getStat().equals(status.online) && snapshot.toObject(User.class).getEmail().equals(u.getEmail())){
                                                    ff=true;
                                                    Toast.makeText(context,
                                                            "This account is already logged in on another device.Go to your account settings for more details",
                                                            Toast.LENGTH_LONG).show();
                                                    break;
                                                }
                                            }
                                            if(ff==false){
                                                //Log.d("message",account.getEmail()+" "+account.getDisplayName()+" "+account.getPhotoUrl());
                                                Toast.makeText(context, "You have successfully logged in", Toast.LENGTH_SHORT).show();

                                                //User uuser = new User(u.getName(), u.getEmail(), "", "", status.online);
                                                db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        boolean f = false;
                                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                            User user1 = snapshot.toObject(User.class);
                                                            if (user1.getEmail().equals(u.getEmail())) {
                                                                f = true;
                                                                user1.setStat(status.online);
                                                                user1.setLast_login_time("");
                                                                user1.setLast_login_date("");
                                                                //user.setId(sharedPreferences.getStringSet(user.getEmail(),null).stream().collect(Collectors.toList()).get(0));
                                                                db.collection("User").document(user1.getId()).set(user1);
                                                                Set<String> set = new HashSet<String>();
                                                                set.add(user1.getId());
                                                                set.add(user1.getName());
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putString("email", u.getEmail());
                                                                editor.putBoolean("islogin" + " " + u.getEmail(), true);
                                                                editor.putStringSet(u.getEmail(), set);
                                                                editor.commit();
                                                                //String e=sharedPreferences.getString("email",null);
                                                                cardView.setVisibility(View.VISIBLE);
                                                                email.setText(user1.getEmail());
                                                                signin.setText("SIGN OUT");
                                                                getCurrentUser(user1.getEmail());
                                                                break;
                                                            }
                                                        }
                                                        if (f == false) {
                                                            db.collection("User").add(u).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    u.setId(documentReference.getId());
                                                                    db.collection("User").document(u.getId()).set(u);
                                                                    //user_list.add(user);
                                                                    Set<String> set = new HashSet<String>();
                                                                    set.add(u.getId());
                                                                    set.add(u.getName());
                                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                    editor.putString("email", u.getEmail());
                                                                    editor.putBoolean("islogin" + " " + u.getEmail(), true);
                                                                    editor.putStringSet(u.getEmail(), set);
                                                                    editor.commit();

                                                                    cardView.setVisibility(View.VISIBLE);
                                                                    email.setText(u.getEmail());
                                                                    signin.setText("SIGN OUT");
                                                                    getCurrentUser(u.getEmail());
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });




//                                    db.collection("User").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            user.setId(documentReference.getId());
//                                            db.collection("User").document(user.getId()).set(user);
//                                            //user_list.add(user);
//                                            Set<String> set = new HashSet<String>();
//                                            set.add(user.getId());
//                                            set.add(user.getName());
//                                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                                            editor.putString("email", user.getEmail());
//                                            editor.putBoolean("islogin" + " " + user.getEmail(), true);
//                                            editor.putStringSet(user.getEmail(), set);
//                                            editor.commit();
//                                            String e=sharedPreferences.getString("email",null);
//                                            cardView.setVisibility(View.VISIBLE);
//                                            email.setText(e);
//                                            signin.setText("SIGN OUT");
//                                            getCurrentUser(e);
//                                        }
//                                    });



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });
            builder.setNegativeButton("CANCEL",null);
            builder.show();
        }
    }

    boolean isLogin(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String email=sharedPreferences.getString("email",null);
        if(email==null)
            return false;
        Boolean login=sharedPreferences.getBoolean("islogin"+" "+email,false);
        return login;

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    void loginGoogle(){

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    void logoutGoogle(){

        cardView.setVisibility(View.GONE);
        email.setText("You are not signed in...");
        signin.setText("SIGN IN");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()){
                            String email=sharedPreferences.getString("email",null);
                            if(email!=null){
                                Set<String> set=sharedPreferences.getStringSet(email,null);

                                if(set!=null){
                                    SimpleDateFormat format=new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                                    Date date = new Date();
//                                    User user=new User((String) set.toArray()[1],email,format.format(date),format.format(date.getDate()), com.example.myapplication.status.offline);
//                                    user.setId((String)set.toArray()[0]);
                                    db.collection("User").document((String) set.toArray()[0]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User curr=documentSnapshot.toObject(User.class);
                                            curr.setStat(com.example.myapplication.status.offline);
                                            curr.setLast_login_date(format.format(date));
                                            curr.setLast_login_time(format.format(date));
                                            db.collection("User").document(curr.getId()).set(curr);
                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                            editor.putBoolean("islogin"+" "+email,false);
                                            editor.commit();

                                        }
                                    });

                                }
                            }
                        }
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account != null) {

                db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean ff=false;
                        for (QueryDocumentSnapshot snapshot :queryDocumentSnapshots){
                            if(snapshot.toObject(User.class).getStat().equals(status.online) && snapshot.toObject(User.class).getEmail().equals(account.getEmail())){
                                ff=true;
                                Toast.makeText(context,
                                        "This account is already logged in on another device.Go to your account settings for more details",
                                        Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                        if(ff==false){
                            //Log.d("message",account.getEmail()+" "+account.getDisplayName()+" "+account.getPhotoUrl());
                            Toast.makeText(context, "You have successfully logged in", Toast.LENGTH_SHORT).show();

                            User user = new User(account.getDisplayName(), account.getEmail(), "", "", status.online);
                            db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    boolean f = false;
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        User user1 = snapshot.toObject(User.class);
                                        if (user1.getEmail().equals(user.getEmail())) {
                                            f = true;
                                            user1.setStat(status.online);
                                            user1.setLast_login_time("");
                                            user1.setLast_login_date("");
                                            //user.setId(sharedPreferences.getStringSet(user.getEmail(),null).stream().collect(Collectors.toList()).get(0));
                                            db.collection("User").document(user1.getId()).set(user1);
                                            Set<String> set = new HashSet<String>();
                                            set.add(user1.getId());
                                            set.add(user1.getName());
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email", account.getEmail());
                                            editor.putBoolean("islogin" + " " + account.getEmail(), true);
                                            editor.putStringSet(account.getEmail(), set);
                                            editor.commit();
                                            //String e=sharedPreferences.getString("email",null);
                                            cardView.setVisibility(View.VISIBLE);
                                            email.setText(user1.getEmail());
                                            signin.setText("SIGN OUT");
                                            getCurrentUser(user1.getEmail());
                                            break;
                                        }
                                    }
                                    if (f == false) {
                                        db.collection("User").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                user.setId(documentReference.getId());
                                                db.collection("User").document(user.getId()).set(user);
                                                //user_list.add(user);
                                                Set<String> set = new HashSet<String>();
                                                set.add(user.getId());
                                                set.add(user.getName());
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("email", account.getEmail());
                                                editor.putBoolean("islogin" + " " + account.getEmail(), true);
                                                editor.putStringSet(account.getEmail(), set);
                                                editor.commit();
                                                String e=sharedPreferences.getString("email",null);
                                                cardView.setVisibility(View.VISIBLE);
                                                email.setText(e);
                                                signin.setText("SIGN OUT");
                                                getCurrentUser(e);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });

            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w("message", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.d("message","connection Failed");
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) context);
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}