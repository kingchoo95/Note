package com.david.noted;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;




public class NoteEditorActivity extends AppCompatActivity {

    //variable for dialog reminder
    Dialog dialog;
    String dialogReminderType = null;
    String dialogDate = null;
    String dialogTime = null;
    String dialogRepeatBy = null;
    String dialogLocation = null;
    String locationString = null;
    String getReminderDate= null;
    String getReminderTime = null;
    String getDialogReminderTypeString = null;
    //make string array for spinner
    String repeats[]={"Does not repeat","Daily","Weekly","Monthly","Yearly"};
    //date and time picker for dialog
    private DatePickerDialog.OnDateSetListener dDateSetListener;
    private TimePickerDialog.OnTimeSetListener tTimeSetListener;

    int noteId;
    Intent intent;
    SQLiteDatabase noteDB;
    //defines array adapter of string type
    ArrayAdapter repeatAdapter;

    EditText editTextTitle,editTextNotes;
    //Declear for date dialog



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
        repeatAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repeats);

        editTextTitle = (EditText) findViewById(R.id.editTextTitleId);
        editTextNotes = (EditText) findViewById(R.id.editTextAddNoteHereId);

        intent = getIntent();
        dialog = new Dialog(NoteEditorActivity.this);
        dialog.setContentView(R.layout.add_reminder_dialog);
        noteId = intent.getIntExtra("noteId",noteId);

        if(noteId != -1 ){

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders WHERE id = " + Integer.toString(noteId+1)+ "", null);

            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int noteIndex = c.getColumnIndex("note");
            int reminderTypeIndex = c.getColumnIndex("reminderType");
            int dateIndex = c.getColumnIndex("date");
            int timeIndex = c.getColumnIndex("time");
            int repeatByIndex = c.getColumnIndex("repeatBy");
            int locationIndex = c.getColumnIndex("location");

            c.moveToFirst();

           Log.i("Result here",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) + c.getString(reminderTypeIndex) + c.getString(dateIndex) + c.getString(repeatByIndex) + c.getString(locationIndex));

            editTextTitle.setText(c.getString(titleIndex));
            editTextNotes.setText(c.getString(noteIndex));
            dialogLocation = locationString;
            dialogReminderType = c.getString(reminderTypeIndex);
            dialogDate = c.getString(dateIndex);
            dialogTime = c.getString(timeIndex);
            dialogRepeatBy = c.getString(repeatByIndex);
            dialogLocation =  c.getString(locationIndex);


        }else{


            noteId = MainActivity.titles.size() - 1;
            //MainActivity.arrayAdapter.notifyDataSetChanged();
        }

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


            final Spinner repeatSp = (Spinner)dialog.findViewById(R.id.spinnerRepeatId);
            //final Spinner dateSp = (Spinner)dialog.findViewById(R.id.spinnerDateId);
            //final Spinner timeSp = (Spinner)dialog.findViewById(R.id.spinnerTimeId);
            final RadioGroup radioGroupReminderType = (RadioGroup) dialog.findViewById(R.id.radioReminderType);
            RadioButton radioButtonTime = (RadioButton) dialog.findViewById(R.id.radioTimeId);
            RadioButton radioButtonPlace = (RadioButton) dialog.findViewById(R.id.radioPlaceId);
            RadioButton radioButtonTimeAndPlace = (RadioButton) dialog.findViewById(R.id.radioTimeAndPlaceId);
            //set adapter to spinner
            repeatSp.setAdapter(repeatAdapter);


            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

            //auto complete
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    locationString = place.getName().toString();


                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("tag", "An error occurred: " + status);
                }
            });
            //get and assign spinner value

            //radioButtonPlace.setChecked(true);
            dialog.show();
            //find textView id for dialog
            TextView saveTextBtn = (TextView) dialog.findViewById(R.id.textViewSaveId);
            TextView cancelTextBtn = (TextView) dialog.findViewById(R.id.textViewCancelId);
            final TextView textViewDateId = (TextView) dialog.findViewById(R.id.textViewDateId);
            final TextView textViewTimeId = (TextView) dialog.findViewById(R.id.textViewTimeId);

            textViewDateId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dateDialog = new DatePickerDialog(NoteEditorActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, dDateSetListener,year, month,day);
                    dateDialog.show();
                }
            });

            dDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    getReminderDate = Integer.toString(dayOfMonth)+"/"+Integer.toString(month)+"/"+Integer.toString(year);
                    textViewDateId.setText(getReminderDate);
                }
            };

            textViewTimeId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int min = cal.get(Calendar.MINUTE);

                    TimePickerDialog timeDialog = new TimePickerDialog(NoteEditorActivity.this, tTimeSetListener, hour, min, true);
                    timeDialog.show();
                }
            });

            tTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
                @Override
                public void onTimeSet(android.widget.TimePicker view,
                                      int hourOfDay, int minute) {
                    getReminderTime = Integer.toString(hourOfDay)+":"+Integer.toString(minute);
                    textViewTimeId.setText(getReminderTime);
                }
            };
            cancelTextBtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLocation =null;
                    dialogRepeatBy = null;
                    dialogDate =null;
                    dialogTime = null;
                    dialogReminderType =null;
                    dialog.dismiss();

                }
            });

            saveTextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogLocation = locationString;
                    //get and assign spinner value
                    dialogRepeatBy = repeatSp.getSelectedItem().toString();
                    dialogDate = getReminderDate;
                    dialogTime = getReminderTime;

                    //get and assign radio button value
                    int selectedId = radioGroupReminderType.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
                    dialogReminderType = radioButton.getText().toString();
                    dialog.dismiss();

                }

            });

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
                noteDB.execSQL("INSERT INTO reminders (id, title, note, reminderType, date, time, repeatBy, location) VALUES ( " + Integer.toString(numRows+1) + ",'" + editTextTitle.getText().toString() + "' ,'" + editTextNotes.getText().toString() + "' ,'"+ dialogReminderType +"',  '"+ dialogDate +"', '"+  dialogTime +"','"+ dialogRepeatBy +"', '"+ dialogLocation +"')");

                MainActivity.titles.add(editTextTitle.getText().toString());
                MainActivity.arrayAdapter.notifyDataSetChanged();
                onBackPressed();
                return true;
            }else{
                noteDB.execSQL("UPDATE reminders SET  title= '"+ editTextTitle.getText().toString() +"',note = '"+ editTextNotes.getText().toString() +"',reminderType = '" + dialogReminderType + "',date = '" + dialogDate + "',time = '"+ dialogTime +"', repeatBy = '" + dialogRepeatBy + "', location = '"+ dialogLocation +"'  WHERE id = "+ Integer.toString(noteId+1)+"");
                MainActivity.titles.set(noteId,editTextTitle.getText().toString());
                MainActivity.arrayAdapter.notifyDataSetChanged();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}