package com.david.googlemapcloneweb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class mapWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_web_view);
        Intent intent = getIntent();
        String loacation = intent.getExtras().getString("address");
        WebView webView = (WebView) findViewById(R.id.webId);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://www.google.com.my/maps/dir/'3.0486785,101.4561956'/"+loacation+"");
        //webView.loadUrl("https://www.google.com.my/maps/search/near+by+atm");
    }
}
