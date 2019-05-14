package com.dim8inf206.autthtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EcranPrincipal extends AppCompatActivity {

    private Toolbar mTopToolbar;
    FirebaseUser user;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecran_principal);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Add this to the archiving activity
        databaseReference.child(user.getUid() + "_" + "Photos").child("photo1").child("Link").setValue("Dummy link");
        databaseReference.child(user.getUid() + "_" + "Photos").child("photo1").child("Description").setValue("Dummy description");
        databaseReference.child(user.getUid() + "_" + "Photos").child("photo1").child("Tags").child("tag1").setValue(true);


        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Log.v("DIM",ds.getKey() + " " + ds.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(dataListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_favorite){
            Intent intent = new Intent(this, TagsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Recuperer(View view){
        Intent intent = new Intent(this, RecupererActivity.class);
        startActivity(intent);
    }

    public void Archiver(View view){
        Intent intent = new Intent(this, ArchiverActivity.class);
        startActivity(intent);
    }
}
