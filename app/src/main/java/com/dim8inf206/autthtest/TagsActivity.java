package com.dim8inf206.autthtest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    private ArrayList<Tag> tags = new ArrayList<>();
    FirebaseUser user;
    private DatabaseReference databaseReference;
    private ListView mListView;
    private TagListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid() + "_Tags");
        mListView = findViewById(R.id.listView);

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

        tags.add(new Tag("This tag should't be the only one visible"));

        databaseReference.addValueEventListener(dataListener);
        adapter = new TagListAdapter(this, R.layout.adapter_gestion_tags, tags);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("DIM", "ITEM CLICKED IS : " + adapterView.getItemAtPosition(i) + "AT POSITION: " + i);
            }
        });
    }

    private void refreshListView(){
        adapter.notifyDataSetChanged();
    }

    public void deleteButtonClick(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        TextView textView = (TextView) parent.getChildAt(0);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(user.getUid() + "_Tags/" + textView.getText()).setValue(null);
    }

    public void AddTagButtonClick(View view){
        EditText editText = findViewById(R.id.editTextAddTag);
        //Big if to make sure that the entered text does not make the the app crash

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
