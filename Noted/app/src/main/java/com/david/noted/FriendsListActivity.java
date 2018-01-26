package com.david.noted;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsListActivity extends AppCompatActivity {
    LocationManager locationManager ;
    LocationListener locationListener;

    ArrayList<String> helperDatabaseArrayList = new ArrayList<String>();
    ArrayList<String> helperLocationArrayList = new ArrayList<String>();
    ArrayList<String> helperUsernameArrayList = new ArrayList<String>();
    ArrayList<String> getUsernameAfterFilter = new ArrayList<String>();
    ArrayList<String> helperUsernameAndLocationArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    ListView friendsListView;
    float getNowLatitude;
    float getNowLongitude;
    Boolean isRemoveLocation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        setTitle("Helpers Location");

        friendsListView = (ListView) findViewById(R.id.listViewFriendsListId);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, helperUsernameAndLocationArrayList);

        addLocationToHelper();


        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("username", getUsernameAfterFilter.get(position));
                startActivity(intent);
            }
        });

    }

    public void filterListView(){



        for(int x = 0 ; x < helperUsernameArrayList.size(); x++){

            for(String usernameInDB : helperDatabaseArrayList ){
                if( usernameInDB.equals(helperUsernameArrayList.get(x))){

                    helperUsernameAndLocationArrayList.add(helperUsernameArrayList.get(x) +" - " + helperLocationArrayList.get(x));
                    getUsernameAfterFilter.add(helperUsernameArrayList.get(x));
                    friendsListView.setAdapter(arrayAdapter);
                }

            }

        }

        if(helperDatabaseArrayList.isEmpty()){

            Toast.makeText(this, "You dont have any helper!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_helper,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.addHelperId) {
            Intent intent = new Intent(FriendsListActivity.this, AddFriendsActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.logOutId) {

            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(FriendsListActivity.this, MainActivity.class);
            startActivity(intent);

            Toast.makeText(this, "Good Bye", Toast.LENGTH_SHORT).show();

        }
        return true;
    }

    public void runFriendListDatabase() {
        helperDatabaseArrayList.clear();


        try {

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            noteDB.execSQL("CREATE TABLE IF NOT EXISTS helpersName (helpersUsername VARCHAR)");

            Cursor c = noteDB.rawQuery("SELECT * FROM helpersName",null);
            int helperIndex = c.getColumnIndex("helpersUsername");
            c.moveToFirst();

            while(c != null){

                helperDatabaseArrayList.add(c.getString(helperIndex));

                c.moveToNext();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        filterListView();
    }



    public void helperLocation(){



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {

                getNowLatitude =(float) location.getLatitude();
                getNowLongitude=(float)location.getLongitude();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> listAddress = geocoder.getFromLocation(getNowLatitude,getNowLongitude,1);
                String locationName = "";
            if(listAddress != null && listAddress.size() >0){

                if(listAddress.get(0).getLocality() != null ){
                    locationName += listAddress.get(0).getLocality() + ", ";
                }

                if(listAddress.get(0).getCountryName() != null ){
                    locationName += listAddress.get(0).getCountryName() + " ";
                }

                ParseObject userCurrentLocation = new ParseObject("UserCurrentLocation");

                userCurrentLocation.put("username", ParseUser.getCurrentUser().getUsername());
                userCurrentLocation.put("Location", locationName);
                userCurrentLocation.saveInBackground();
                Toast.makeText(this, "Your current location at "+locationName, Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public void removeHelperLocation(){


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("UserCurrentLocation");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object : objects){
                            object.deleteInBackground();
                        }
                    }

                }

            }
        });


    }

    public void updateLocation(View view){
        Button updateUserLocationButton = (Button) findViewById(R.id.updateUserLocationId);
        if(isRemoveLocation){

            removeHelperLocation();
            updateUserLocationButton.setText("Update Location");
            Log.i("currentState",Boolean.toString(isRemoveLocation));
            isRemoveLocation = false;

        }else{

            helperLocation();
            updateUserLocationButton.setText("Remove Location");
            Log.i("currentState",Boolean.toString(isRemoveLocation));
            isRemoveLocation = true;

        }

    }

        public void addLocationToHelper(){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("UserCurrentLocation");
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());



            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects, ParseException e) {


                    helperLocationArrayList.clear();
                    helperUsernameArrayList.clear();
                    helperUsernameAndLocationArrayList.clear();
                    getUsernameAfterFilter.clear();
                    if (e == null) {
                        if (objects.size() > 0) {

                            for (ParseObject object : objects) {
                                helperUsernameArrayList.add(object.getString("username"));
                                helperLocationArrayList.add(object.getString("Location"));

                            }
                        }

                    }
                    runFriendListDatabase();
                }
            });

    }




    @Override
    protected void onResume() {
        super.onResume();
        addLocationToHelper();
    }


}
