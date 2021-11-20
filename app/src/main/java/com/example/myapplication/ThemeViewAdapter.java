package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.rpc.context.AttributeContext;

import java.util.List;

public class ThemeViewAdapter   extends ArrayAdapter<ThemeView> {

    private List<ThemeView> lists;
    private Context context;
    SharedPreferences preferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    public ThemeViewAdapter(@NonNull Context context, @NonNull List<ThemeView> objects) {
        super(context, 0, objects);
        this.context = context;
        this.lists=lists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.theme_item, parent, false);
        }
        ThemeView model = getItem(position);
        ImageButton img = listitemView.findViewById(R.id.imgBtnTheme);
        TextView title = listitemView.findViewById(R.id.txtTextTheme);

        preferences= context.getSharedPreferences(MyPREFERENCES,context.MODE_PRIVATE);
        int imgPath=preferences.getInt("theme",R.drawable.ic_3);
        if(imgPath==model.getImagePath()){
            model.setSelected(true);
        }
        else{
            model.setSelected(false);
        }

        if(model.isSelected()==true){
            title.setBackgroundColor(Color.GREEN);
            title.setText("SELECTED");
        }
        else{
            title.setBackgroundColor(Color.RED);
            title.setText("UNSELECTED");
        }

        img.setImageResource(model.getImagePath());
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=preferences.edit();
                editor.putInt("theme",model.getImagePath());
                editor.putInt("theme_text",model.getTextColor());
                editor.putInt("theme_bg",model.getBackgroundColor());
                editor.putInt("theme_tag_text",model.getTagTextColor());
                editor.putInt("theme_tag_bg",model.getTagBgColor());
                editor.commit();
                notifyDataSetChanged();
            }
        });

        return listitemView;
    }
}
