package com.david.testtickbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> simpleArray = new ArrayList<>();
    ListView listView;
    static CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewId);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(getApplicationContext(),NoteEditorActivity.class);

                /*
                convertView = getLayoutInflater().inflate(R.layout.search_place_custom_layout,null);
                TextView clickedTitle = (TextView) customAdapter.getView(position,view,parent).findViewById(R.id.textViewTitleNameId);
                TextView clickedLocation = (TextView) customAdapter.getView(position,view,parent).findViewById(R.id.textViewPlaceId);
                //Log.i("adasdasdasd",clickedTitle.getText().toString());
                Log.i("awdwa1",Integer.toString(position));
                position = getLocationPosition(clickedTitle.getText().toString(), clickedLocation.getText().toString() );

                intent.putExtra("noteId",position);
                startActivity(intent);
                */
            }
        });

        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    //create custom listview
    class CustomAdapter extends BaseAdapter implements Filterable {

        @Override
        public int getCount() {
            return simpleArray.size();
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

            convertView = getLayoutInflater().inflate(R.layout.custom,null);
            //TextView textViewPlaceName = (TextView) convertView.findViewById(R.id.textViewPlaceId);
            EditText textViewTitleName = (EditText) convertView.findViewById(R.id.editTextId);

           // textViewPlaceName.setText(locationsASC.get(position));
            textViewTitleName.setText(simpleArray.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {

            return null;
        }


    }

    public void addNewList(View view){

        simpleArray.add(" ");
        MainActivity.customAdapter.notifyDataSetChanged();
    }
}
