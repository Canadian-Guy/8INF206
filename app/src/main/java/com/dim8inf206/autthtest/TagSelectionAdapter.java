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
    private int mResource;
    private LayoutInflater inflater;

    public TagSelectionAdapter(Context context, int resource, ArrayList<Tag> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_selection_tags, null);
            holder = new ViewHolder();
            holder.tagCheckBox = (CheckBox) convertView.findViewById(R.id.checkBoxTagSelection);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Tag tag = getItem(position);
        holder.tagCheckBox.setText(tag.getTagName());
        holder.tagCheckBox.setChecked(tag.isSelected);
        return convertView;
    }

    //Essaie d'utilisation du paton ViewHolder
    static private class ViewHolder{
        private CheckBox tagCheckBox;
    }
}
