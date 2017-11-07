package com.david.noted;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {

    //make string array for spinner
    String dates[]={"Today","Tomorrow","Pick a date"};
    String times[]={"Select a Time"};
    String repeats[]={"Does not repeat","Daily","Weekly","Monthly","Yearly"};
    int noteId;
    Intent intent;
    SQLiteDatabase noteDB;
    //defines array adapter of string type
    ArrayAdapter dateAdapter,timeAdapter,repeatAdapter;

    EditText editTextTitle,editTextNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
        dateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dates);
        timeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times);
        repeatAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repeats);

        editTextTitle = (EditText) findViewById(R.id.editTextTitleId);
        editTextNotes = (EditText) findViewById(R.id.editTextAddNoteHereId);

        intent = getIntent();

        noteId = intent.getIntExtra("noteId",noteId);

        if(noteId != -1 ){



            Cursor c = noteDB.rawQuery("SELECT * FROM reminders WHERE id = " + Integer.toString(noteId+1)+ "", null);

            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int noteIndex = c.getColumnIndex("note");
            int dateIndex = c.getColumnIndex("date");
            int locationIndex = c.getColumnIndex("location");

            //Log.i("test",Integer.toString(locationIndex));
            //Log.i("test",Integer.toString(titleIndex));
            //Log.i("test",Integer.toString(idIndex));

            c.moveToFirst();

            Log.i("Result here",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) + c.getString(dateIndex) + c.getString(locationIndex));

            editTextTitle.setText(c.getString(titleIndex));
            //editTextNotes.setText(c.getString(noteIndex));




        }else{

            Log.i("this is else","else");
            noteId = MainActivity.titles.size() - 1;
            //MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //MainActivity.titles.set(noteId,String.valueOf(s));
                //MainActivity.arrayAdapter.notifyDataSetChanged();


                //SharedPreferences sharedPreferencesTitles = getApplicationContext().getSharedPreferences("com.david.noted", Context.MODE_PRIVATE);

               // HashSet<String> setTitles = new HashSet<String>(MainActivity.titles);

                //sharedPreferencesTitles.edit().putStringSet("titles",setTitles).apply();

            }

            @Override
            public void afterTextChanged(Editable s) {

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

            //define spinner
            final Spinner repeatSp = (Spinner)dialog.findViewById(R.id.spinnerRepeatId);
            final Spinner dateSp = (Spinner)dialog.findViewById(R.id.spinnerDateId);
            final Spinner timeSp = (Spinner)dialog.findViewById(R.id.spinnerTimeId);

            //set adapter to spinner
            repeatSp.setAdapter(repeatAdapter);
            dateSp.setAdapter(dateAdapter);
            timeSp.setAdapter(timeAdapter);

            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            //auto complete
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    Log.i("tag", "Place: " + place.getName());
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("tag", "An error occurred: " + status);
                }
            });

            dialog.show();
            return true;

        }else{

            return false;

        }
    }
    // menu bar for add reminder end

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        noteId = intent.getIntExtra("noteId",noteId);
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {


            if(noteId ==-1){
                SQLiteDatabase noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
                int numRows = (int) DatabaseUtils.queryNumEntries(noteDB, "reminders");
                noteDB.execSQL("INSERT INTO reminders (id, title, note, date, location) VALUES ( " + Integer.toString(numRows+1) + ",'" + editTextTitle.getText().toString() + "' ,'" + editTextNotes.getText().toString() + "' ,'dog','dog')");

                MainActivity.titles.add(editTextTitle.getText().toString());
                MainActivity.arrayAdapter.notifyDataSetChanged();
                onBackPressed();
                return true;
            }else{
                noteDB.execSQL("UPDATE reminders SET  title= '"+ editTextTitle.getText().toString() +"' WHERE id = "+ Integer.toString(noteId+1)+"");
                MainActivity.titles.set(noteId,editTextTitle.getText().toString());
                MainActivity.arrayAdapter.notifyDataSetChanged();
            }


        }
        return super.onKeyDown(keyCode, event);
    }



}