package com.dim8inf206.autthtest;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecupererActivity extends AppCompatActivity {

    final long MAX_SIZE = 5 * 1024 * 1024;

    private ArrayList<Photo> photos = new ArrayList<>();
    private ArrayList<Tag> tags = new ArrayList<>();
    private DatabaseReference databaseReferencePhotos;
    private DatabaseReference databaseReferenceTags;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseUser user;
    private ListView mListViewTags;
    private ListView mListViewPhotos;
    private TagSelectionAdapter tagSelectionAdapter;
    private PhotoListAdapter photoListAdapter;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperer);

        imageView = findViewById(R.id.imageViewSelectedPhoto);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mListViewTags = findViewById(R.id.listViewTags);
        mListViewPhotos = findViewById(R.id.listViewPhotos);
        databaseReferencePhotos = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Photos");
        databaseReferenceTags = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");
        tagSelectionAdapter = new TagSelectionAdapter(this, R.layout.adapter_selection_tags, tags);
        mListViewTags.setAdapter(tagSelectionAdapter);
        photoListAdapter = new PhotoListAdapter(this, R.layout.adapter_photos, photos);
        mListViewPhotos.setAdapter(photoListAdapter);

        FillTagsList();

        FillImageList();

        // Ajouter une icone de chargement https://stackoverflow.com/questions/26054420/set-visibility-of-progress-bar-gone-on-completion-of-image-loading-using-glide-l
        mListViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Photo tmpPhoto = photos.get(i);
                Glide.with(getApplicationContext()).load(tmpPhoto.getLink()).transform(new Rotate(90)).into(imageView);
                mListViewPhotos.setVisibility(View.GONE);
                mListViewTags.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        });

        Log.v("DIM", "PHOTOS: " + photos);
    }

    private void FillImageList(){
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //Log.v("DIM", "RECUPERER ACTIVITY: " + ds.getKey() + ": " + ds.getValue());
                    Log.v("DIM", "Here's a link: " + ds.child("Link").getValue().toString());
                    Log.v("DIM", "Here's a description: " + ds.child("Description").getValue().toString());
                    photos.add(new Photo(ds.child("Description").getValue().toString(), ds.child("Link").getValue().toString()));
                }
                photoListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReferencePhotos.addListenerForSingleValueEvent(dataListener);
    }

    private void FillTagsList(){
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tags.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    tags.add(new Tag((String) ds.getValue()));
                }
                tagSelectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReferenceTags.addListenerForSingleValueEvent(dataListener);

        mListViewTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tags.get(i).switchSelection();
                tagSelectionAdapter.notifyDataSetChanged();
            }
        });
    }
}
