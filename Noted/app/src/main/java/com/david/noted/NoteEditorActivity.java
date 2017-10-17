package com.david.noted;

import android.app.Dialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NoteEditorActivity extends AppCompatActivity {

    //make string array for spinner
    String dates[]={"Today","Tomorrow","Pick a date"};
    String times[]={"Select a Time"};
    String repeats[]={"Does not repeat","Daily","Weekly","Monthly","Yearly"};

    //defines array adapter of string type
    ArrayAdapter dateAdapter,timeAdapter,repeatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        dateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dates);
        timeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times);
        repeatAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repeats);

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

            //define spinner
            final Spinner repeatSp = (Spinner)dialog.findViewById(R.id.spinnerRepeatId);
            final Spinner dateSp = (Spinner)dialog.findViewById(R.id.spinnerDateId);
            final Spinner timeSp = (Spinner)dialog.findViewById(R.id.spinnerTimeId);

            //set adapter to spinner
            repeatSp.setAdapter(repeatAdapter);
            dateSp.setAdapter(dateAdapter);
            timeSp.setAdapter(timeAdapter);

            dialog.show();
            return true;

        }else{

            return false;

        }
    }
    // menu bar for add reminder end





}