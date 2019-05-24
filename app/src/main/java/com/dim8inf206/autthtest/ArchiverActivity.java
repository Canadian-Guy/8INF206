package com.dim8inf206.autthtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;



public class ArchiverActivity extends AppCompatActivity {

    private ArrayList<Tag> tags;
    private TagSelectionAdapter adapter;
    private ImageView imageView;
    private FirebaseUser user;
    private Bitmap bitmap;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private String photoPath;
    static private int PICK_IMAGE_REQUEST = 2;
    static private int IMAGE_CAPTURE_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tags = new ArrayList<>();
        setContentView(R.layout.activity_archiver);
        //references
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseTagsReference = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");

        ListView mListView = findViewById(R.id.listViewTags);
        storageReference.child(user.getUid()+"/test.png"); // DEBUG - TESTING
        imageView = findViewById(R.id.imageViewPreview);

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tmpString;
                tags.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    tmpString = (String) ds.getValue(true);
                    tags.add(new Tag(tmpString));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseTagsReference.addListenerForSingleValueEvent(dataListener);
        adapter = new TagSelectionAdapter(this, R.layout.adapter_selection_tags, tags);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tags.get(i).switchSelection();
                adapter.notifyDataSetChanged();
            }
        });

        if(savedInstanceState != null) {
            tags = savedInstanceState.getParcelableArrayList("tags");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        //Sauvegarde de l'etat des tags
        savedInstanceState.putParcelableArrayList("tags", tags);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        //Set chaque checkbox a son ancienne valeure
        tags = savedInstanceState.getParcelableArrayList("tags");
        super.onRestoreInstanceState(savedInstanceState);
    }


    public void onButtonSelectClicked(View view){
        //Fermer le clavier, solution trouvee sur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        View possiblyTheEditText = this.getCurrentFocus();
        if (possiblyTheEditText != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(possiblyTheEditText.getWindowToken(), 0);
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
    }

    public void onButtonTakePictureClicked(View view){
        //Fermer le clavier
        View possiblyTheEditText = this.getCurrentFocus();
        if (possiblyTheEditText != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(possiblyTheEditText.getWindowToken(), 0);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = CreateImageFile();
            } catch (IOException e){
                e.printStackTrace();
            }
            if(photoFile != null){
                Uri uri = FileProvider.getUriForFile(this, "com.dim8inf206.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, IMAGE_CAPTURE_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0 , 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(rotatedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedImage = Bitmap.createBitmap(bitmap, 0 , 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(rotatedImage);
            //Supresseion de la photo crée pour sauver de l'espace sur l'appareil
            File file = new File(photoPath);
            file.delete();
        }
    }


    public void onButtonConfirmClicked(View view){
        //S'il n'y a pas d'image, on cancel!
        if(imageView.getDrawable() == null){
            Toast.makeText(getApplicationContext(), "Veuillez choisir une photo", Toast.LENGTH_SHORT).show();
            return;
        }
        //Fermer le clavier, solution trouvee sur https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        View possiblyTheEditText = this.getCurrentFocus();
        if (possiblyTheEditText != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(possiblyTheEditText.getWindowToken(), 0);
        }
        //On indique a l'utilisateur qu'il y a un chargement
        findViewById(R.id.buttonConfirm).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        //Code pris dans la doc pour imageView -> Storage
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageReference = storage.getReference().child(user.getUid() + "/" + UUID.randomUUID() + ".jpg" );

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.v("DIM", exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        EditText tmpEditText = (EditText) findViewById(R.id.editTextDescription);
                        String tmpDescription = tmpEditText.getText().toString();
                        AjouterImageRealtimeDatabase(tmpDescription, uri.toString());
                    }
                });

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Toast toast = Toast.makeText(getApplicationContext(), "Picture archived!", Toast.LENGTH_SHORT);
                toast.show();
                //Afficher le bouton de nouveau, l'archivage est fini
                findViewById(R.id.buttonConfirm).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Log.v("DIM", "Archivage completé!!");
            }
        });
    }

    private File CreateImageFile() throws IOException{
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("tmpImage", ".jpg", storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    private void AjouterImageRealtimeDatabase(String description, String link){
        //Creation du time stamp qui servira de nom a la photo
        Date date = new Date();
        Long timestamp = date.getTime();
        //Creation d'une reference a la base de donne
        DatabaseReference databasePhotosReference = FirebaseDatabase.getInstance().getReference();
        //Recuperation des tags qui sont cochés
        ArrayList<String> tmpTags = new ArrayList<>();
        for(Tag tag:tags){
            if(tag.isSelected)
                tmpTags.add(tag.getTagName());
        }
        //Ajout des champs a la base de donne
        databasePhotosReference.child(user.getUid() + "_Photos").child(timestamp.toString()).child("Description").setValue(description);
        databasePhotosReference.child(user.getUid() + "_Photos").child(timestamp.toString()).child("Link").setValue(link);
        for (String tag:tmpTags)
            databasePhotosReference.child(user.getUid() + "_Photos").child(timestamp.toString()).child("Tags").child(tag).setValue(true);

        PreparerPourProchainArchivage();

    }

    private void PreparerPourProchainArchivage(){
        //Reset tout pour etre pret a archiver de nouveau
        EditText tmpEditText = findViewById(R.id.editTextDescription);
        ImageView tmpImageView = findViewById(R.id.imageViewPreview);
        tmpEditText.setText("");
        tmpImageView.setImageDrawable(null);
        for(Tag tag:tags){
            tag.isSelected = false;
        }
        adapter.notifyDataSetChanged();
    }

}

