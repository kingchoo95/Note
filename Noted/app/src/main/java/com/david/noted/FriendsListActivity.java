package com.david.noted;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsListActivity extends AppCompatActivity {
    ArrayList<String> helperArrayList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ArrayList<String> friendUsernames;
    ListView friendsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        setTitle("Nearby Helpers");
        friendUsernames = new ArrayList<>();
        friendsListView = (ListView) findViewById(R.id.listViewFriendsListId);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, friendUsernames);



        Log.i("helpers",helperArrayList.toString());
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("username", friendUsernames.get(position));
                startActivity(intent);
            }
        });




    }

    public void filterListView(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseUser user : objects){
                            for(String helper : helperArrayList ){
                                if(helper.equals(user.getUsername())){
                                    friendUsernames.add(user.getUsername());
                                }
                            }

                            friendsListView.setAdapter(arrayAdapter);
                        }
                    }
                }
            }
        });
        if(helperArrayList.isEmpty()){
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
        return true;
    }

    public void runFriendListDatabase() {
        helperArrayList.clear();

        try {

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            noteDB.execSQL("CREATE TABLE IF NOT EXISTS helpersName (helpersUsername VARCHAR)");

            Cursor c = noteDB.rawQuery("SELECT * FROM helpersName",null);
            int helperIndex = c.getColumnIndex("helpersUsername");
            c.moveToFirst();

            while(c != null){
                Log.i("yourhelper",c.getString(helperIndex));
                helperArrayList.add(c.getString(helperIndex));

                c.moveToNext();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        helperArrayList.clear();
        arrayAdapter.clear();
        runFriendListDatabase();
        filterListView();
        arrayAdapter.notifyDataSetChanged();
    }
}
