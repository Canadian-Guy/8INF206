package com.dim8inf206.autthtest;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TagsActivity extends AppCompatActivity {

    private ArrayList<Tag> tags;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private TagListAdapter adapter;
    private ValueEventListener tagsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        tags = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");
        ListView mListView = findViewById(R.id.listViewTags);

        tagsListener = new ValueEventListener() {
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

        databaseReference.addValueEventListener(tagsListener);
        adapter = new TagListAdapter(this, R.layout.adapter_gestion_tags, tags);
        mListView.setAdapter(adapter);
    }

    @Override
    protected void onStop(){
        if(databaseReference != null && tagsListener != null)
            databaseReference.removeEventListener(tagsListener);
        super.onStop();
    }

    @Override
    protected void onPause(){
        if(databaseReference != null && tagsListener != null)
            databaseReference.removeEventListener(tagsListener);
        super.onPause();
    }

    public void deleteButtonClick(View view) {
        final View theView = view;
        new AlertDialog.Builder(this)
                .setTitle("Supression")
                .setMessage("Voulez-vous supprimer le tag?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LinearLayout parent = (LinearLayout)theView.getParent();
                        TextView textView = (TextView) parent.getChildAt(0);
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child(user.getUid() + "_Tags/" + textView.getText()).setValue(null);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


    }

    public void AddTagButtonClick(View view){
        EditText editText = findViewById(R.id.editTextAddTag);
        //Gros "if" pour s'assurer que le tag ne contient pas de caracteres interdits
        if(editText.getText().toString().matches("") || editText.getText().toString().contains("$") || editText.getText().toString().contains("/")
        || editText.getText().toString().contains(".") || editText.getText().toString().contains("#") || editText.getText().toString().contains("]")
        || editText.getText().toString().contains("[")) {
            Toast toast = Toast.makeText(this,"Invalid characters or empty field", Toast.LENGTH_LONG);
            toast.show();
        }
        else{
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(user.getUid() + "_Tags/" + editText.getText().toString()).setValue(editText.getText().toString());
        }
        editText.setText("");
    }
}
