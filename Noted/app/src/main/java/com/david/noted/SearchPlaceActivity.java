package com.david.noted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchPlaceActivity extends AppCompatActivity {
    SearchView searchView;
    static ArrayList<String> titles = new ArrayList<>();
    static ArrayList<String> locations = new ArrayList<>();
    static CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        titles.clear();
        locations.clear();
        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            Cursor c = noteDB.rawQuery("SELECT * FROM reminders",null);

            int idIndex = c.getColumnIndex("id");
            int titleIndex = c.getColumnIndex("title");
            int repeatByIndex = c.getColumnIndex("repeatBy");
            int locationIndex = c.getColumnIndex("location");

            c.moveToFirst();

            while(c != null){
                //Log.i("Resultnow",Integer.toString(c.getInt(idIndex)) + c.getString(titleIndex) + c.getString(noteIndex) +  c.getString(reminderTypeIndex) + c.getString(dateIndex) +  c.getString(timeIndex) +  c.getString(repeatByIndex) +c.getString(locationIndex) +c.getString(latitudeIndex)+c.getString(longitudeIndex)+Integer.toString(c.getInt(isTriggerIndex)));
                titles.add(c.getString(titleIndex));
                locations.add(c.getString(locationIndex));
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.placeListViewId);

        customAdapter = new CustomAdapter();
        if(titles != null){

            listView.setAdapter(customAdapter);
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



        //bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_decending:
                        Log.i("sta up there","a to z");
                        break;
                    case R.id.action_acending:
                        Log.i("sta up there","z to a");
                        break;
                }
                return true;
            }
        });

    }

    //search menu
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
                //arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //create custom listview
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return locations.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.search_place_custom_layout,null);
            TextView textViewPlaceName = (TextView) convertView.findViewById(R.id.textViewPlaceId);
            TextView textViewTitleName = (TextView) convertView.findViewById(R.id.textViewTitleNameId);

            textViewPlaceName.setText(locations.get(position));
            textViewTitleName.setText(titles.get(position));
            return convertView;
        }
    }
}
