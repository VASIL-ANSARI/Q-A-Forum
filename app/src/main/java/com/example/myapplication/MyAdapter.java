package com.example.myapplication;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.speechly.client.slu.StreamConfig;
import com.speechly.client.speech.Client;

import java.util.UUID;

public class MyAdapter extends FragmentPagerAdapter
{

    Context mContext;
    int totalCount;

    public MyAdapter(Context context, FragmentManager fm,int totalCount){
        super(fm);
        mContext=context;
        this.totalCount=totalCount;
        GeneralSettings.speechlyClient=Client.Companion.fromActivity((AppCompatActivity) mContext, UUID.fromString("7183f0cf-c274-44c4-9b2d-c07fe2f1da8c"), StreamConfig.LanguageCode.EN_US,"api.speechly.com",true);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                //Client speechlyClient= Client.Companion.fromActivity((AppCompatActivity)mContext, UUID.fromString("7183f0cf-c274-44c4-9b2d-c07fe2f1da8c"), StreamConfig.LanguageCode.EN_US,"api.speechly.com",true);

                GeneralSettings gs=new GeneralSettings();
                return gs;
            case 1:
                themeSettings ts=new themeSettings();
                return ts;
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return totalCount;
    }
}
