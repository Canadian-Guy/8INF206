package com.dim8inf206.autthtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

public class TagSelectionAdapter extends ArrayAdapter<Tag> {

    private Context mContext;
    int mResource;

    public TagSelectionAdapter(Context context, int resource, ArrayList<Tag> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String tagName = getItem(position).getTagName();
        Tag tag = new Tag(tagName);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxTagSelection);
        checkBox.setText(tagName);

        return convertView;
    }
}
