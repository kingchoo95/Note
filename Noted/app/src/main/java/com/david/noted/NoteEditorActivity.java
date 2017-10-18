package com.david.noted;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class NoteEditorActivity extends AppCompatActivity {

    //make string array for spinner
    String dates[]={"Today","Tomorrow","Pick a date"};
    String times[]={"Select a Time"};
    String repeats[]={"Does not repeat","Daily","Weekly","Monthly","Yearly"};
    int noteId;

    //defines array adapter of string type
    ArrayAdapter dateAdapter,timeAdapter,repeatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        dateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dates);
        timeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times);
        repeatAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repeats);

        EditText editTextTitle = (EditText) findViewById(R.id.editTextTitleId);
        EditText editTextNotes = (EditText) findViewById(R.id.editTextAddNoteHereId);

        Intent intent = getIntent();

        noteId = intent.getIntExtra("noteId",-1);

        if(noteId != -1 ){

            editTextTitle.setText(MainActivity.titles.get(noteId));
            //editTextNotes.setText(MainActivity.notes.get(noteId));
        }else{
            MainActivity.titles.add("");
           // MainActivity.notes.add("");
            noteId = MainActivity.titles.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                MainActivity.titles.set(noteId,String.valueOf(s));
                MainActivity.arrayAdapter.notifyDataSetChanged();
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





}