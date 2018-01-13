package com.david.noted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchTimeActivity extends AppCompatActivity {

    static ArrayList<String> dateASC = new ArrayList<>();
    static ArrayList<String> titleASC = new ArrayList<>();
    static ArrayList<String> filteredDate = new ArrayList<>();
    static ArrayList<String> filteredTitle= new ArrayList<>();
    ArrayList<String> titles = MainActivity.titles;
    ArrayList<String> dates = MainActivity.dates;

    boolean date_is_today = false;
    boolean month_is_this_month = false;
    boolean is_show_all_date = true;

    CustomAdapter customAdapter = new CustomAdapter();;

    ListView dateListView;
    View convertView;
    String todaysDate;
    String thisMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_time);
        setTitle("Search By Date");
        todaysDate = getTodayDate();
        thisMonth = todaysDate.substring(0,7);

        runDatabase();
        filteredDate();
        addArrayListIntoListView();
        dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);

                convertView = getLayoutInflater().inflate(R.layout.search_time_custom_layout,null);
                TextView clickedTitle = (TextView) customAdapter.getView(position,view,parent).findViewById(R.id.textViewTitleNameId);
                TextView clickedDate = (TextView) customAdapter.getView(position,view,parent).findViewById(R.id.textViewTimeId);



                position = getDateListViewPosition(clickedTitle.getText().toString(), clickedDate.getText().toString() );

                intent.putExtra("noteId",position);
                startActivity(intent);
            }
        });


        //bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_time);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_today:
                        Log.i("bottomNava","today");
                        is_show_all_date = false;
                        date_is_today = true;
                        month_is_this_month = false;
                        filteredDate();
                        addArrayListIntoListView();

                        break;
                    case R.id.action_thisMonth:
                        Log.i("bottomNava","month");
                        is_show_all_date = false;
                        date_is_today = false;
                        month_is_this_month = true;
                        filteredDate();
                        addArrayListIntoListView();

                        break;
                    case R.id.action_showAll:
                        Log.i("bottomNava","show ALL");
                        is_show_all_date = true;
                        date_is_today = false;
                        month_is_this_month = false;
                        filteredDate();
                        addArrayListIntoListView();

                        break;
                }
                return true;
            }
        });

    }

    public int getDateListViewPosition(String title,String date){
        int position=0;
        for(int i = 0 ; i < titles.size() ; i++){
            if(title.equals(titles.get(i))) {
                if (date.equals(dates.get(i))) {

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
            return filteredDate.size();
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

            convertView = getLayoutInflater().inflate(R.layout.search_time_custom_layout,null);
            TextView textViewTimeName = (TextView) convertView.findViewById(R.id.textViewTimeId);
            TextView textViewTitleName = (TextView) convertView.findViewById(R.id.textViewTitleNameId);

            textViewTimeName.setText(filteredDate.get(position));
            textViewTitleName.setText(filteredTitle.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {

            return null;
        }


    }

    //get title and date  location
    public void runDatabase(){

        dateASC.clear();
        titleASC.clear();

        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders ORDER BY date ASC",null);

            int titleIndex = c.getColumnIndex("title");
            int dateIndex = c.getColumnIndex("date");


            c.moveToFirst();

            while(c != null){

                if(!(c.getString(dateIndex)).equals("")) {
                    dateASC.add(c.getString(dateIndex));
                    titleASC.add(c.getString(titleIndex));
                }
                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //get today date
    public String getTodayDate(){
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat dfToday = new SimpleDateFormat("yyyy-MM-dd");

        String formattedTodayDate = dfToday.format(cal.getTime());
        Log.i("dateNow",formattedTodayDate);

        return formattedTodayDate;

    }

    //add filtered arraylist
    public void filteredDate(){

        filteredDate.clear();
        filteredTitle.clear();
        if(date_is_today){
            for(int i = 0 ; i < dateASC.size() ; i++ ){
                if(todaysDate.equals(dateASC.get(i))){

                    filteredDate.add(dateASC.get(i));
                    filteredTitle.add(titleASC.get(i));

                }
            }
        }

        if(month_is_this_month){
            for(int i = 0 ; i < dateASC.size() ; i++ ){
                if(thisMonth.equals(dateASC.get(i).substring(0,7))){

                    filteredDate.add(dateASC.get(i));
                    filteredTitle.add(titleASC.get(i));

                }
            }
        }

        if(is_show_all_date){
            for(int i = 0 ; i < dateASC.size() ; i++ ){

                    filteredDate.add(dateASC.get(i));
                    filteredTitle.add(titleASC.get(i));
            }
        }

    }

    //add array list into listView
    public void addArrayListIntoListView(){
        dateListView = (ListView) this.findViewById(R.id.timeListViewId);

        if(dateASC != null){
            dateListView.setAdapter(customAdapter);
        }
    }

    @Override
    protected void onResume() {
        runDatabase();
        filteredDate();
        addArrayListIntoListView();
        super.onResume();
    }
}
