package com.david.noted;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> locations = new ArrayList<>();
    static ArrayList<String> dates = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    public int selectId = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            if(item.getItemId()== R.id.addNewNoteId) {

                //Log.i("Note id Button", );
                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);

                intent.putExtra("noteId",-1);
                startActivity(intent);
                return true;

            }else{
                return false;

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titles.clear();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //aSK FOR PERMISSION
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

       /*
        //get user location permission
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        */
        Intent intent = new Intent(this, CheckConditionService.class);
        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            noteDB.execSQL("CREATE TABLE IF NOT EXISTS reminders (id INTEGER, title VARCHAR, note VARCHAR, reminderType VARCHAR, date VARCHAR, time VARCHAR, repeatBy VARCHAR, location VARCHAR, latitude REAL, longitude REAL, isTrigger INTEGER)");


            Cursor c = noteDB.rawQuery("SELECT * FROM reminders",null);


            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int noteIndex = c.getColumnIndex("note");
            int reminderTypeIndex = c.getColumnIndex("reminderType");
            int dateIndex = c.getColumnIndex("date");
            int timeIndex = c.getColumnIndex("time");
            int repeatByIndex = c.getColumnIndex("repeatBy");
            int locationIndex = c.getColumnIndex("location");
            int latitudeIndex = c.getColumnIndex("latitude");
            int longitudeIndex = c.getColumnIndex("longitude");
            int isTriggerIndex = c.getColumnIndex("isTrigger");

            c.moveToFirst();

            while(c != null){
                Log.i("Resultnow",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) +  c.getString(reminderTypeIndex) + c.getString(dateIndex) +  c.getString(timeIndex) +  c.getString(repeatByIndex) +c.getString(locationIndex) +c.getString(latitudeIndex)+c.getString(longitudeIndex)+Integer.toString(c.getInt(isTriggerIndex)));
                titles.add(c.getString(titleIndex));
                locations.add(c.getString(locationIndex));
                dates.add(c.getString(dateIndex));
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        startService(intent);


        ListView listView = (ListView) findViewById(R.id.notesListViewId);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.addnote_menu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //create arrayadapter and set it to list view
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titles);
        if(titles != null){
            listView.setAdapter(arrayAdapter);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);
                //tell which row the user tap
                intent.putExtra("noteId",position);
                //Log.i("noteiD" , Integer.toString(position));

                startActivity(intent);
            }
        });

        //long click to delete note
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final  int position, long id) {
                final int itemToDelete = position;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                titles.remove(itemToDelete);
                                MainActivity.arrayAdapter.notifyDataSetChanged();

                                SQLiteDatabase noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
                                noteDB.execSQL("DELETE FROM reminders WHERE id = " + Integer.toString(itemToDelete+1)+ "");

                                int numRows = (int)DatabaseUtils.queryNumEntries(noteDB, "reminders");

                                selectId = itemToDelete+1 ;
                                while(selectId <= numRows){

                                    noteDB.execSQL("UPDATE reminders SET id = "+ Integer.toString(selectId) +" WHERE id = "+ Integer.toString(selectId+1)+"");
                                    selectId = selectId+1;

                                }
                            }
                        }).setNegativeButton("No",null).show();
                return true;
            }
        });


    }

    //search menu on header start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.search_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.i("search",Integer.toString(item.getItemId()));
        if(item.getItemId() == R.id.searchReminderId){
            Intent searchReminderItent = new Intent(this, SearchReminderActivity.class);
            this.startActivity(searchReminderItent);
        }

        switch (item.getItemId()){

            case R.id.addFriendsId : {
                Intent intent = new Intent(this, LogInActivity.class);
                startActivity(intent);
                return true;
            }



            default : {
                return false;
            }

        }

    }
    //search menu on header end



}
