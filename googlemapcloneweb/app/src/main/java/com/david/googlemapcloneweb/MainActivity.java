package com.david.googlemapcloneweb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void runMap(View view){
        Button buttonClick = (Button) findViewById(R.id.buttonId);

        Intent intent = new Intent(this, mapWebViewActivity.class);
        String apple = "Sungai long";
        intent.putExtra("address",apple);
        this.startActivity(intent);

    }
}
