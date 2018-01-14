package com.david.noted;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String activeUser ="";
    ArrayList<String> messages = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Loading...");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);

        final Handler handler = new Handler();

        final int delay = 5000; //milliseconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = getIntent();


                activeUser = intent.getStringExtra("username");
                setTitle("Chat with " + activeUser);

                ListView listViewChat = (ListView) findViewById(R.id.listViewChatId);


                listViewChat.setAdapter(arrayAdapter);

                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");

                query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
                query1.whereEqualTo("receiver", activeUser);

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");

                query2.whereEqualTo("receiver", ParseUser.getCurrentUser().getUsername());
                query2.whereEqualTo("sender", activeUser);

                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                queries.add(query1);
                queries.add(query2);

                final ParseQuery<ParseObject> query = ParseQuery.or(queries);

                query.orderByDescending("createdAt");

                        query.findInBackground(new FindCallback<ParseObject>() {


                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        messages.clear();
                                        for (ParseObject message : objects) {

                                            String messageContent = message.getString("message");

                                            if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                                                messageContent = activeUser + " : " + messageContent;

                                            }
                                            if (!message.getString("receiver").equals(ParseUser.getCurrentUser().getUsername())) {

                                                messageContent = "You" + " : " + messageContent;

                                            }

                                            messages.add(messageContent);
                                        }

                                        arrayAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        });
                        handler.postDelayed(this, delay);
                    }
                }, delay);


            }


    public void sendChat(View view){

        final EditText editTextChat = (EditText) findViewById(R.id.editTextChatId);

        final String messageContent = editTextChat.getText().toString();

        ParseObject message = new ParseObject("Message");

        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("receiver", activeUser);
        message.put("message",messageContent);
        editTextChat.setText("");
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    Toast.makeText(ChatActivity.this, "Message send!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.remove_helper,menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        if(!activeUser.equals("")) {
            if (item.getItemId() == R.id.removeHelperId) {
                final String username = activeUser;
                new AlertDialog.Builder(ChatActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Are you sure you want to remove " + username + " from your helper list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ChatActivity.this, FriendsListActivity.class);

                                SQLiteDatabase noteDB = openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
                                noteDB.execSQL("DELETE FROM helpersName WHERE helpersUsername = '" + username + "'");
                                startActivity(intent);
                                Toast.makeText(ChatActivity.this, username + " is removed from your helper list", Toast.LENGTH_SHORT).show();

                            }

                        }).setNegativeButton("No", null).show();
                return false;
            }
        }else{
            Toast.makeText(this, "Chat history loading...", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}

