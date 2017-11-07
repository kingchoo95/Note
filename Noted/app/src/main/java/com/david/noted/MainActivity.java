package com.david.noted;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    static ArrayList<String> titles = new ArrayList<>();
    //static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    public int selectId=0;

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

        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            noteDB.execSQL("CREATE TABLE IF NOT EXISTS reminders (id INTEGER, title VARCHAR, note VARCHAR, date VARCHAR, location VARCHAR)");


            Cursor c = noteDB.rawQuery("SELECT * FROM reminders",null);


            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int noteIndex = c.getColumnIndex("note");
            int dateIndex = c.getColumnIndex("date");
            int locationIndex = c.getColumnIndex("location");

            c.moveToFirst();

            while(c != null){
                Log.i("Result",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) + c.getString(dateIndex) + c.getString(locationIndex));
                titles.add(c.getString(titleIndex));
                c.moveToNext();
            }

        }catch(Exception e){
        e.printStackTrace();
    }



        ListView listView = (ListView) findViewById(R.id.notesListViewId);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.addnote_menu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //create arrayadapter and set it to list view

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titles);
        if(titles != null){
            listView.setAdapter(arrayAdapter);
        }




        //SharedPreferences sharedPreferencesNotes = getApplicationContext().getSharedPreferences("com.david.noted", Context.MODE_PRIVATE);
        //SharedPreferences sharedPreferencesTitles = getApplicationContext().getSharedPreferences("com.david.noted", Context.MODE_PRIVATE);

        //HashSet<String> setNotes = (HashSet<String>) sharedPreferencesNotes.getStringSet("notes",null);
        //HashSet<String> setTitles =  (HashSet<String>) sharedPreferencesTitles.getStringSet("titles",null);


       // if(setTitles == null){
        //    titles.add("Do more exercise");
        //    notes.add("do push up 200 times");
        //}else{
        //    notes = new ArrayList(setNotes);
       //     titles = new ArrayList(setTitles);
       // }




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);
                //tell which row the user tap
                intent.putExtra("noteId",position);
                Log.i("noteiD" , Integer.toString(position));

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
                                //SharedPreferences sharedPreferencesNotes = getApplicationContext().getSharedPreferences("com.david.noted", Context.MODE_PRIVATE);
                               //SharedPreferences sharedPreferencesTitles = getApplicationContext().getSharedPreferences("com.david.noted", Context.MODE_PRIVATE);

                                //HashSet<String> setNotes = new HashSet<String>(MainActivity.notes);
                                //HashSet<String> setTitles = new HashSet<String>(MainActivity.titles);

                                //sharedPreferencesNotes.edit().putStringSet("notes",setNotes).apply();
                                //sharedPreferencesTitles.edit().putStringSet("titles",setTitles).apply();
                                //while()
                                //noteDB.execSQL("UPDATE reminders SET id = , id = ");

                                //reassign id when delete
                                int numRows = (int)DatabaseUtils.queryNumEntries(noteDB, "reminders");
                                Log.i("o0o", Integer.toString(numRows));
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

        switch (item.getItemId()){

            case R.id.searchByTitleId : {
                Log.i("search button", "Title clicked");
                return true;
            }

            case R.id.searchByTimeId : {
                Log.i("search button", "Time clicked");
                return true;
            }

            case  R.id.searchByLocationId : {
                Log.i("search button", "location clicked");
                return true;
            }

            default : {
                return false;
            }

        }

    }
    //search menu on header end

}
