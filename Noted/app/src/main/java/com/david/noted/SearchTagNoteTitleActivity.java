package com.david.noted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchTagNoteTitleActivity extends AppCompatActivity {
    String tag, noteTitle;
    static ArrayList<String> titles = new ArrayList<>();
    ListView listViewTitle;
    ArrayAdapter arrayAdapter;
    int noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag_note_title);
        tag = getIntent().getStringExtra("tag");

        setTitle("Search Tag : " + tag);
        searchAndAddAllTags();
        listViewTitle = (ListView) findViewById(R.id.listViewTitlesId);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titles);
        listViewTitle.setAdapter(arrayAdapter);

        listViewTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               noteTitle = listViewTitle.getItemAtPosition(position).toString();
                searchNoteIdUsingTagAndTitle();
            }
        });


    }

    public void searchAndAddAllTags(){
        titles.clear();

        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders WHERE tag ='"+ tag +"'",null);
            int titleIndex = c.getColumnIndex("title");
            c.moveToFirst();

            while(c != null){

                titles.add(c.getString(titleIndex));
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void searchNoteIdUsingTagAndTitle(){
        Log.i("sadasd","asdasdasd");
        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders WHERE tag ='"+ tag +"' AND title ='"+ noteTitle +"'",null);
            int idIndex = c.getColumnIndex("id");
            c.moveToFirst();
            noteId = c.getInt(idIndex);


            Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);

            //tell which row the user tap
            intent.putExtra("noteId",noteId-1);
            startActivity(intent);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
