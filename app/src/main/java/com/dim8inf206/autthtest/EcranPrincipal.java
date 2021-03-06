package com.dim8inf206.autthtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EcranPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecran_principal);

        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
    }

    @Override
    public void onBackPressed(){
        //Boite de dialogue pour éviter de quiter par accident.
        new AlertDialog.Builder(this)
                .setTitle("Êtes-vous sûr de vouloir quitter?")
                .setMessage("Êtes-vous sûr de vouloir quitter?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //Si on appuie sur l'icone de la toolbar, on va sur l'écran de gestion des tags
        if(id == R.id.action_favorite){
            Intent intent = new Intent(this, TagsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Methode pour aller à l'écran de recuperation
    public void Recuperer(View view){
        Intent intent = new Intent(this, RecupererActivity.class);
        startActivity(intent);
    }

    //Methode pour aller à l'écran d'archivage
    public void Archiver(View view){
        Intent intent = new Intent(this, ArchiverActivity.class);
        startActivity(intent);
    }
}
