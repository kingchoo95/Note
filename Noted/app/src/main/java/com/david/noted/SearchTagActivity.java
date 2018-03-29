package com.david.noted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchTagActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter arrayAdapter;

    static ArrayList<String> tagsArraylist = new ArrayList<>();
    Set<String> filteredArraylist = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tag);
        setTitle("Search By Tag");
        searchAndAddAllTags();
        listView = (ListView) findViewById(R.id.listViewTagGroupId);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,filteredArraylist.toArray());

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getApplicationContext(),SearchTagNoteTitleActivity.class);

                intent.putExtra("tag",listView.getItemAtPosition(position).toString());


                startActivity(intent);

            }
        });

    }


    public void searchAndAddAllTags(){
        tagsArraylist.clear();
        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders",null);
            int tagIndex = c.getColumnIndex("tag");
            c.moveToFirst();

            while(c != null){
                tagsArraylist.add(c.getString(tagIndex));
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        //prevent duplicated tag
        filteredArraylist.clear();
        filteredArraylist.addAll(tagsArraylist);
    }

    @Override
    protected void onResume() {
        searchAndAddAllTags();
        listView.setAdapter(arrayAdapter);
        super.onResume();

    }
}
