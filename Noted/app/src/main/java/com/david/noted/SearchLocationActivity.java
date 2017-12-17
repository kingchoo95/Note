package com.david.noted;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SearchLocationActivity extends AppCompatActivity {

    int noteId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        Intent intentGetNoteId = getIntent();
        noteId = intentGetNoteId.getIntExtra("noteId",noteId);
        String locationFound = runDatabase(noteId+1);
        Button userLocationButton = (Button) findViewById(R.id.userLocationId);
        if(locationFound.equals("")){
            userLocationButton.setEnabled(false);
            locationFound = "No Location Set";
        }
        userLocationButton.setText(locationFound);
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
        Button buttonClicked = (Button) findViewById(R.id.userLocationId);

        String userLocation = buttonClicked.getText().toString();
        String address = "https://www.google.com.my/maps/search/"+userLocation ;


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

}

