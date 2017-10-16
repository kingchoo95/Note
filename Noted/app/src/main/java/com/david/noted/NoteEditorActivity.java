package com.david.noted;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NoteEditorActivity extends AppCompatActivity {

    //define spinner
    Spinner sp;

    //make string array for spinner
    String names[]={"red","blue","purple"};

    //defines array adapter of string type
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);


        //set adapter to spinner
         sp = (Spinner)findViewById(R.id.spinnerRepeatId);
         adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names);

        sp.setAdapter(adapter);
        sp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:{
                        Log.i("color","red");
                    }
                    case 1:{
                        Log.i("color","blue");
                    }
                    default:{
                        Log.i("color","purple");
                    }

                }
            }
        });

    }
    // menu bar for add reminder start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.addreminder_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.addReminderId ){
            //open add_reminder_dialog activity
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_reminder_dialog);
            dialog.show();
            return true;
        }else{
            return false;
        }
    }
    // menu bar for add reminder end





}
