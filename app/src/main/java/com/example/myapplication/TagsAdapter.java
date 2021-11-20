package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TagsAdapter extends ArrayAdapter<Tag> {

    public TagsAdapter(@NonNull Context context, ArrayList<Tag> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.show_tags, parent, false);
        }
        Tag courseModel = getItem(position);
        TextView description = listitemView.findViewById(R.id.idTVCourse);
        TextView title = listitemView.findViewById(R.id.idIVcourse);

        title.setText(courseModel.getName());
        description.setText(courseModel.getDescription());

        return listitemView;
    }

}
