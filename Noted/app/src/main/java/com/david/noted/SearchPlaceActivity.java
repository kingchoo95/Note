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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Filter;

import java.util.ArrayList;



public class SearchPlaceActivity extends AppCompatActivity {
    SearchView searchView;
    static ArrayList<String> titlesASC = new ArrayList<>();
    static ArrayList<String> locationsASC = new ArrayList<>();

    ArrayList<String> titles = MainActivity.titles;
    ArrayList<String> locations = MainActivity.locations;

    static CustomAdapter customAdapter;
    ListView listView;
    View convertView;
    Boolean ASC = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        runDatabase();
        //bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_decending:
                        Log.i("sta up there","a to z");
                        ASC = false;
                        runDatabase();


                        break;
                    case R.id.action_acending:
                        Log.i("sta up there","z to a");
                        ASC = true;
                        runDatabase();

                        break;
                }
                return true;
            }
        });

    }

    public void runDatabase(){

        titlesASC.clear();
        locationsASC.clear();

        String newQuery;

        if(ASC == true){
            newQuery ="SELECT * FROM reminders ORDER BY location ASC";
        }else{
            newQuery ="SELECT * FROM reminders ORDER BY location DESC";
        }

        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            Cursor c = noteDB.rawQuery(newQuery,null);


            int titleIndex = c.getColumnIndex("title");
            int locationIndex = c.getColumnIndex("location");

            c.moveToFirst();

            while(c != null){

                if(!c.getString(locationIndex).equals("")) {
                    titlesASC.add(c.getString(titleIndex));
                    locationsASC.add(c.getString(locationIndex));
                }

                c.moveToNext();
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.placeListViewId);

        customAdapter = new CustomAdapter();
        if(titlesASC != null){

            listView.setAdapter(customAdapter);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);
                //String clickedLocationString = listView.getItemAtPosition(position).toString();
                convertView = getLayoutInflater().inflate(R.layout.search_place_custom_layout,null);
                TextView clickedTitle = (TextView) customAdapter.getView(position,view,parent).findViewById(R.id.textViewTitleNameId);
                TextView clickedLocation = (TextView) customAdapter.getView(position,view,parent).findViewById(R.id.textViewPlaceId);
                //Log.i("adasdasdasd",clickedTitle.getText().toString());
                Log.i("awdwa1",Integer.toString(position));
                position = getLocationPosition(clickedTitle.getText().toString(), clickedLocation.getText().toString() );

                intent.putExtra("noteId",position);
                startActivity(intent);
            }
        });
    }
    //get location id in database
    public int getLocationPosition(String title,String location){
        int position = 0;


        for(int i = 0 ; i < locations.size() ; i++ ){

            if(locations.get(i).equals(location)){
                if(titles.get(i).equals(title)){
                    position = i;
                    break;
                }
            }

        }

        return position;
    }
    //create custom listview
    class CustomAdapter extends BaseAdapter implements Filterable{

        @Override
        public int getCount() {
            return locationsASC.size();
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

            textViewPlaceName.setText(locationsASC.get(position));
            textViewTitleName.setText(titlesASC.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {

            return null;
        }


    }
    @Override
    public void onResume(){

        runDatabase();
        super.onResume();
    }

}
