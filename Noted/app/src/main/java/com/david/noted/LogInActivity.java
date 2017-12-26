package com.david.noted;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    Boolean signUpMode = true;
    TextView changeSignUpMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        changeSignUpMode = (TextView) findViewById(R.id.textViewLogInId);

        changeSignUpMode.setOnClickListener(this);
        //ParseUser user = new ParseUser();
        //user.getCurrentUser().logOut();
        if(ParseUser.getCurrentUser() != null){
            goFriendsListActivity();
        }
    }

    public void goFriendsListActivity(){
        Intent intent = new Intent(this, FriendsListActivity.class);
        startActivity(intent);
    }
    public void onClick(View view){
        if(view.getId() == R.id.textViewLogInId){

            Button signUpButton = (Button) findViewById(R.id.buttonSignUpId);
            if(signUpMode){
                signUpMode = false;
                signUpButton.setText("Log In");
                changeSignUpMode.setText("or Sign Up");

            }else{
                signUpMode = true;
                signUpButton.setText("Sign Up");
                changeSignUpMode.setText("or Log In");
            }
        }
    }
    public void signUp(View view){
        EditText usernameEditText = (EditText) findViewById(R.id.editTextUsernameId);
        EditText passwordEditText = (EditText) findViewById(R.id.editTextPasswordId);

        if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            Toast.makeText(this, "A username and password is required!", Toast.LENGTH_SHORT).show();
        }else {

            if (signUpMode) {
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(LogInActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                            goFriendsListActivity();
                        } else {
                            Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null){

                            Toast.makeText(LogInActivity.this, "Log In successful!", Toast.LENGTH_SHORT).show();
                            goFriendsListActivity();
                        }else{
                            Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        }
}
