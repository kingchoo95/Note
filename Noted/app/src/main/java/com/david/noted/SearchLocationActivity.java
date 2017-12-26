package com.david.noted;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SearchLocationActivity extends AppCompatActivity {

    int noteId;
    Button userLocationButton;
    Button othersButton;
    Button atmsButton;
    Button gasStationsButton;
    Button pharmaciesButton;
    Button cafesButton;
    Button groceriesButton;
    Button restaurantsButton;
    String locationFound;
    String setText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        Intent intentGetNoteId = getIntent();
        noteId = intentGetNoteId.getIntExtra("noteId",noteId);


        userLocationButton = (Button) findViewById(R.id.userLocationId);
        othersButton = (Button) findViewById(R.id.othersId);
        atmsButton = (Button) findViewById(R.id.atmsId);
        gasStationsButton = (Button) findViewById(R.id.gasStationId);
        pharmaciesButton = (Button) findViewById(R.id.pharmaciesId);
        cafesButton = (Button) findViewById(R.id.cafesId);
        groceriesButton = (Button) findViewById(R.id.groceriesId);
        restaurantsButton = (Button) findViewById(R.id.restaurantsId);


        enableButton();
    }

    public String runDatabase(int noteId){
       String locationFound = "No Location Set";

        try{

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

            Cursor c = noteDB.rawQuery("SELECT * FROM reminders WHERE id ="+noteId ,null);

            int locationIndex = c.getColumnIndex("location");


            c.moveToFirst();

            while(c != null){

                locationFound = c.getString(locationIndex);

                c.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return locationFound;
    }

    public void runUserLocation(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/"+locationFound ;

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runOthers(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runNearByATMs(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/nearby+ATMs";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runNearByGasStation(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/nearby+Gas+Stations";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runNearByCafes(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/nearby+Cafes";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runNearByPharmacies(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/nearby+Pharmacies";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runNearByGroceries(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/nearby+Groceries";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public void runNearByRestaurants(View view){
        Intent intent = new Intent(this,WebViewGoogleMapActivity.class);

        String address = "https://www.google.com.my/maps/search/nearby+Restaurants";

        intent.putExtra("address",address);
        this.startActivity(intent);

    }

    public boolean networkStatus(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
            return true;

        return false;
    }
    @Override
    protected void onResume() {

        enableButton();
        super.onResume();
    }

    public void enableButton(){
        locationFound = runDatabase(noteId+1);
        boolean status = networkStatus();
        if(!status){
            Toast.makeText(this,"Please connect to internet!",Toast.LENGTH_LONG).show();
        }

            userLocationButton.setEnabled(status);
            othersButton.setEnabled(status);
            atmsButton.setEnabled(status);
            gasStationsButton.setEnabled(status);
            pharmaciesButton.setEnabled(status);
            cafesButton.setEnabled(status);
            groceriesButton.setEnabled(status);
            restaurantsButton.setEnabled(status);
        if(locationFound.equals("")){
            userLocationButton.setEnabled(false);
            locationFound = "No Location Set";
        }
        if(locationFound.length()> 15){
            setText = locationFound.substring(0,13)+ "...";
        }else{
            setText = locationFound;
        }
        userLocationButton.setText(setText);
    }
}

