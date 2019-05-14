package com.dim8inf206.autthtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.util.ArrayList;

/*
* TODO:
* Archiver un objet photo dans la realtime database
* quand une photo est mise sur storage.
*
* Afficher les tags dans une listView et permetre
* de les selectionner pour les appliquer a la photo
* */

public class ArchiverActivity extends AppCompatActivity {

    private ArrayList<Tag> tags = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ListView mListView;
    private TagSelectionAdapter adapter;
    private ImageView imageView;
    private FirebaseUser user;
    private Bitmap bitmap;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    static private int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_archiver);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");
        mListView = findViewById(R.id.listViewTags);

        storageReference.child(user.getUid()+"/test.png");
        Log.v("DIM","User: " + user.getUid());
        imageView = findViewById(R.id.imageViewPreview);

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tmpString;
                tags.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    tmpString = (String) ds.getValue(true);
                    tags.add(new Tag(tmpString));
                    Log.v("DIM", "TAGS ACTIVITY: " + ds.getKey() + " " + ds.getValue()+" and here is the current list" + tags.toString());
                }
                refreshListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(dataListener);
        adapter = new TagSelectionAdapter(this, R.layout.adapter_selection_tags, tags);
        mListView.setAdapter(adapter);

    }

    public void onButtonSelectClicked(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onButtonConfirmClicked(View view) throws FileNotFoundException {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageReference = storage.getReference().child(user.getUid()+"test.jpg");
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast toast = Toast.makeText(getApplicationContext(), "Picture archived!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void refreshListView(){
        adapter.notifyDataSetChanged();
    }

    //private void AjouterImageRealtimeDatabase(Photo photo){
    // Creation d'un objet photo
    // Ajout de l'objet dans la realtime database
    // }
}
