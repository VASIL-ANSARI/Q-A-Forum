package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link themeSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class themeSettings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public themeSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment themeSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static themeSettings newInstance(String param1, String param2) {
        themeSettings fragment = new themeSettings();
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

    GridView recyclerview;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_theme_settings, container, false);
        recyclerview=view.findViewById(R.id.grid_layout_theme);
        context=container.getContext();

        ArrayList<ThemeView> lists=new ArrayList<>();
        lists.add(new ThemeView(R.drawable.ic_1,Color.WHITE,Color.BLACK,Color.WHITE,Color.LTGRAY));
        lists.add(new ThemeView(R.drawable.ic_2,Color.BLACK,Color.WHITE,Color.BLACK,Color.GRAY));
        lists.add(new ThemeView(R.drawable.ic_3,Color.BLACK,Color.YELLOW,Color.WHITE,Color.BLUE));
        lists.add(new ThemeView(R.drawable.ic_4,Color.BLUE,Color.YELLOW,Color.BLACK,Color.rgb(147,204,234)));
        lists.add(new ThemeView(R.drawable.ic_5,Color.WHITE,Color.rgb(0,0,139),Color.BLACK,Color.rgb(0,183,235)));
        lists.add(new ThemeView(R.drawable.ic_6,Color.BLUE,Color.rgb(0,0,139),Color.BLACK,Color.WHITE));
        lists.add(new ThemeView(R.drawable.ic_7,Color.RED,Color.WHITE,Color.RED,Color.DKGRAY));
        lists.add(new ThemeView(R.drawable.ic_8,Color.WHITE,Color.RED,Color.WHITE,Color.GREEN));
        lists.add(new ThemeView(R.drawable.ic_9,Color.WHITE,Color.RED,Color.WHITE,Color.rgb(255,0,255)));

        ThemeViewAdapter themeViewAdapter=new ThemeViewAdapter(context,lists);
        recyclerview.setAdapter(themeViewAdapter);

        return view;
    }
}