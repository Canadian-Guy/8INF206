package com.dim8inf206.autthtest;


import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Objects;

public class RecupererActivity extends AppCompatActivity {

    private ArrayList<String> selectedTags;
    private ArrayList<Photo> photos;
    private ArrayList<Tag> tags;
    private DatabaseReference databaseReferencePhotos;
    private DatabaseReference databaseReferenceTags;
    private ListView mListViewTags;
    private ListView mListViewPhotos;
    private TagSelectionAdapter tagSelectionAdapter;
    private PhotoListAdapter photoListAdapter;
    private ImageView imageView;
    private View loadingPanel;
    private ValueEventListener tagsListener;
    private ValueEventListener photosListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_recuperer);

        selectedTags = new ArrayList<>();
        tags = new ArrayList<>();
        photos = new ArrayList<>();

        if(savedInstanceState != null){
            tags = savedInstanceState.getParcelableArrayList("tags");
            selectedTags = savedInstanceState.getStringArrayList("selectedTags");
        }

        loadingPanel = findViewById(R.id.loadingPanel);
        imageView = findViewById(R.id.imageViewSelectedPhoto);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mListViewTags = findViewById(R.id.listViewTags);
        mListViewPhotos = findViewById(R.id.listViewPhotos);
        //Creation des références sur la base de donnée et sur le Storage.
        databaseReferencePhotos = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(user).getUid() + "_Photos");
        databaseReferenceTags = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");
        //Creation et application des adaptateurs
        tagSelectionAdapter = new TagSelectionAdapter(this, R.layout.adapter_selection_tags, tags);
        mListViewTags.setAdapter(tagSelectionAdapter);
        photoListAdapter = new PhotoListAdapter(this, R.layout.adapter_photos, photos);
        mListViewPhotos.setAdapter(photoListAdapter);

        if(savedInstanceState == null)  //Puisqu'on garde les tags en mémoire
            FillTagsList();

        FillImageList();
        SetTagsListListener();
        SetImageClickListener();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed(){
        //Si une image est ouverte, on la ferme au lieu de quitter.
        if(imageView.getVisibility() != View.GONE){
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.GONE);
            mListViewPhotos.setVisibility(View.VISIBLE);
            mListViewTags.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility(View.GONE);
        }
        else{
            //Si aucune image n'est ouverte, l'utilisateur veut quitter.
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause(){
        //Retrait des listeners quand on quite cet écran
        if(tagsListener != null && databaseReferenceTags != null) {
            databaseReferenceTags.removeEventListener(tagsListener);
            Log.v("DIM", "Removed listener on tags reference");
        }
        if(photosListener != null && databaseReferencePhotos != null) {
            databaseReferencePhotos.removeEventListener(photosListener);
            Log.v("DIM", "Removed listener on photos reference");
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        //On sauvegarde les tags ainsi que les tags selectionnés
        savedInstanceState.putParcelableArrayList("tags", tags);
        savedInstanceState.putStringArrayList("selectedTags", selectedTags);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        tags = savedInstanceState.getParcelableArrayList("tags");
        selectedTags = savedInstanceState.getStringArrayList("selectedTags");
        super.onRestoreInstanceState(savedInstanceState);
    }

    //Quand on clique sur une image dans la liste, cache les deux listView et on affiche une
    //imageView contenant l'image selectionnée à la place. Si le telechargement ne se fait pas
    //en une fraction de seconde, un indicateur de chargement est affiché.
    private void SetImageClickListener(){
        mListViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Photo tmpPhoto = photos.get(i);
                mListViewPhotos.setVisibility(View.GONE);
                mListViewTags.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                loadingPanel.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(tmpPhoto.getLink())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loadingPanel.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loadingPanel.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imageView);
            }
        });
    }

    //Quand un tag est cliqué, on switch son isSelected et on ajoute son nom dans la liste
    //des tags selectionnés, qui sert de filtre lors de la récupération des photos.
    private void SetTagsListListener() {
        mListViewTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Pour éviter qu'il n'y ai plus qu'un listener pour les photos, puisqu'on appel FillImageList().
                if(photosListener != null && databaseReferencePhotos != null) {
                    databaseReferencePhotos.removeEventListener(photosListener);
                    Log.v("DIM", "Removed listener on photos reference");
                }
                tags.get(i).switchSelection();
                if(tags.get(i).isSelected){
                    selectedTags.add(tags.get(i).getTagName());
                }
                else{
                    selectedTags.remove(tags.get(i).getTagName());
                }
                tagSelectionAdapter.notifyDataSetChanged();
                //Pour rafraichir la liste de photos avec le nouveau filtre.
                FillImageList();
            }
        });
    }

    private void FillImageList(){
        photosListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Quand on récupere des photos dans la base de données, on purge l'ancienne liste de photos.
                photos.clear();
                //S'il y a un filste, on ne récupere que certaines photos.
                if(!selectedTags.isEmpty()) {
                    //Pour chaque photo, on compare la photo avec chaque tag pour savoir si on a la récupere ou non.
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        boolean mustShow = true;
                        for(String s : selectedTags) {
                            if (!ds.hasChild("Tags/" + s)) {
                                mustShow = false;
                            }
                        }
                        if(mustShow){
                            photos.add(new Photo(ds.getKey(), ds.child("Description").getValue().toString(),
                                     ds.child("Link").getValue().toString()));
                        }
                    }
                }
                //S'il n'y a pas de filtre, on affiche toutes les photos.
                else{
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                            photos.add(new Photo(ds.getKey(), ds.child("Description").getValue().toString(),
                                    ds.child("Link").getValue().toString()));
                    }
                }
                photoListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReferencePhotos.addValueEventListener(photosListener);
    }

    //On récuperer les tags de l'utilisateur pour pouvoir les afficher et les utiliser.
    private void FillTagsList(){
        tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Si la liste de tags contenait déjà quelquechose, on purge, car on s'enva chercher tous les tags sur la base de données.
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
        databaseReferenceTags.addListenerForSingleValueEvent(tagsListener);
    }
}
