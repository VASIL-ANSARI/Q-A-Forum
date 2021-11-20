package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    public void sendMessage(String message,int index) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String endpoint="https://fcm.googleapis.com/fcm/send";
                try {
                    URL url=new URL(endpoint);
                    HttpsURLConnection httpsURLConnection= (HttpsURLConnection) url.openConnection();
                    httpsURLConnection.setReadTimeout(10000);
                    httpsURLConnection.setConnectTimeout(15000);
                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(true);

                    httpsURLConnection.setRequestProperty("authorization","key=AAAAoHUxj20:APA91bHjKQtc2vQCsAFD1zHwdjNkdblpRi8gsAUFhMWv-DVTmw59Mn5OQwaFZX2NQ0x8Monl-I5_-IyhBX-LuuyybFmnC44Oz6yqUo0m2VogjH5CQKKzU7epVxTwCW9oZlRskoXO1scJ");
                    httpsURLConnection.setRequestProperty("Content-Type","application/json");

                    JSONObject body=new JSONObject();
                    JSONObject data=new JSONObject();
                    data.put("title","User just posted a new question");
                    data.put("content","User with email id : "+message+" just posted a question. Tap to see it");
                    data.put("index",index);
                    body.put("data",data);

                    body.put("to","/topics/QA");

                    OutputStream outputStream =new BufferedOutputStream(httpsURLConnection.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    writer.write(body.toString());
                    writer.flush();
                    writer.close();
                    outputStream.close();
                    int responseCode=httpsURLConnection.getResponseCode();
                    String responseMessage=httpsURLConnection.getResponseMessage();
                    Log.d("message",responseCode+responseMessage);
                    String result;
                    InputStream inputStream=null;
                    if(responseCode>=400 && responseCode<=499){
                        inputStream= httpsURLConnection.getErrorStream();
                    }
                    else{
                        inputStream=httpsURLConnection.getInputStream();
                    }
                    if(responseCode==200){
                        Log.d("message","notification sent");
                    }
                    else{
                        Log.d("message","error sending notification");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage p0) {
        super.onMessageReceived(p0);
        Log.d("message",p0.getData().toString());

        String title=p0.getData().get("title");
        String content=p0.getData().get("content");
        String index=p0.getData().get("index");
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("NotificationMessage",index);
        intent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        intent.setAction(Intent. ACTION_MAIN ) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        checkNotificationChannel("1");

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),"1")
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSound(defaultSound);

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification.build());

    }

    private void checkNotificationChannel(String Id){
        NotificationChannel notificationChannel=new NotificationChannel(Id,getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.setDescription("CHANNEL_DESCRIPTION");
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
