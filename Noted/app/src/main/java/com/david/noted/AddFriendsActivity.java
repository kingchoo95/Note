package com.david.noted;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class AddFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        setTitle("Find Helpers");
        final ArrayList<String> friendUsernames = new ArrayList<>();
        final ListView friendsListView = (ListView) findViewById(R.id.listViewFriendsListId);

        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, friendUsernames);
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseUser user : objects){
                            friendUsernames.add(user.getUsername());
                            friendsListView.setAdapter(arrayAdapter);
                        }
                    }
                }else{

                }
            }
        });


        //when user click on the helperslist

        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final  int position, long id) {
                final String username= friendsListView.getItemAtPosition(position).toString();
                new AlertDialog.Builder(AddFriendsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to add "+ username +" as helper?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(checkexistingFriend(username)){
                                    Toast.makeText(AddFriendsActivity.this, username+" is already your helper", Toast.LENGTH_SHORT).show();
                                }else {
                                    addToHlperlistDatabase(username);
                                }

                            }

                        }).setNegativeButton("No",null).show();




                return true;
            }
        });

    }
    public void addToHlperlistDatabase(String username){

        Toast.makeText(this, username +" is your helper now", Toast.LENGTH_SHORT).show();

        SQLiteDatabase noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);

        noteDB.execSQL("INSERT INTO helpersName (helpersUsername) VALUES ('"+username+"')");


    }

    public Boolean checkexistingFriend(String username){

        Boolean isExist= false;
        try {

            SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
            noteDB.execSQL("CREATE TABLE IF NOT EXISTS helpersName (helpersUsername VARCHAR)");

            Cursor c = noteDB.rawQuery("SELECT * FROM helpersName",null);
            int helperIndex = c.getColumnIndex("helpersUsername");
            c.moveToFirst();

            while(c != null){
                Log.i("yourhelper",c.getString(helperIndex));
                if(username.equals(c.getString(helperIndex))){
                    isExist = true;
                    break;
                }

                c.moveToNext();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExist;
    }
}
