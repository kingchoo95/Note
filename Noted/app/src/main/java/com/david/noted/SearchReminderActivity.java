package com.david.noted;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;



public class SearchReminderActivity extends AppCompatActivity {
    int countFilterPosition=0;
    SearchView searchView;
    ArrayList<String> titles = MainActivity.titles;
    ArrayList<String> titlesFilter = MainActivity.titles;
    Boolean isFilter = false;
    static ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_reminder);

        ListView listView = (ListView) findViewById(R.id.notesListViewId);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,titlesFilter);
        if(titlesFilter != null){
            listView.setAdapter(arrayAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);
                //tell which row the user tap

                String clickFilterString = parent.getAdapter().getItem(position).toString();
                countFilterPosition = position;
                if(isFilter){

                   checkAfterFilterPosition(clickFilterString,position);

                }
                intent.putExtra("noteId", countFilterPosition);
                startActivity(intent);
                searchView.setQuery("", false);
                searchView.clearFocus();

            }
        });
    }

    public void checkAfterFilterPosition(String clickFilterString, int position){


            int arraySize = titles.size();


            for(int i = 0 ; i < arraySize ; i++){

                if ((clickFilterString.equals(titles.get(i)))) {
                    if(position==0) {

                        countFilterPosition=i;

                    }else{
                        checkAfterFilterPosition(clickFilterString,position-1);
                    }
                }
            }
        Log.i("asdasd",Integer.toString(countFilterPosition));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.advance_search_menu,menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                isFilter = true;
                return false;
            }
        });

      return super.onCreateOptionsMenu(menu);
    }
}