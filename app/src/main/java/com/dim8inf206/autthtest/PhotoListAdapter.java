package com.dim8inf206.autthtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoListAdapter extends ArrayAdapter<Photo> {
    private Context mContext;
    private int mResource;
    private LayoutInflater inflater;

    public PhotoListAdapter(Context context, int resource, ArrayList<Photo> objects){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String link = getItem(position).getLink();
        String description = getItem(position).getDescription();

        convertView = inflater.inflate(mResource, parent, false);

        TextView tvDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);

        tvDescription.setText(description);
        Glide.with(mContext).load(link).into(ivThumbnail);
        return convertView;
    }

}
