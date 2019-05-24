package com.dim8inf206.autthtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class TagListAdapter extends ArrayAdapter<Tag> {

    private Context mContext;
    int mResource;

    public TagListAdapter(Context context, int resource, ArrayList<Tag> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String tagName = getItem(position).getTagName();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTagName = (TextView) convertView.findViewById(R.id.textViewTagName);

        tvTagName.setText(tagName);
        return convertView;
    }

}
