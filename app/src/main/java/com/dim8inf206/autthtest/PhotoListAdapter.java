package com.dim8inf206.autthtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        final String link = getItem(position).getLink();
        final String timestamp = getItem(position).getPhotoTimestamp();
        String description = getItem(position).getDescription();

        convertView = inflater.inflate(mResource, parent, false);

        final TextView tvDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.imageViewThumbnail);
        ImageButton buttonDelete = (ImageButton) convertView.findViewById(R.id.buttonDelete);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(mContext)
                        .setTitle("Supression")
                        .setMessage("Voulez-vous supprimer la photo?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeletePicture(link, timestamp);
                                Log.v("DIM", "DELETED ITEM: " + i);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        tvDescription.setText(description);
        Glide.with(mContext).load(link).into(ivThumbnail);
        return convertView;
    }

    private void DeletePicture(String link, String timestamp){
        StorageReference photoStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(link);
        photoStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Photo suprimée", Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference photoDataRef = FirebaseDatabase.getInstance().getReference(user.getUid() +"_Photos/" + timestamp);
        photoDataRef.setValue(null);
    }

}
