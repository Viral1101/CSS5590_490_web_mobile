package com.neilpirch.socialloginapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;

public class LoggedInActivity extends AppCompatActivity {

    String id;
    String email;
    String name;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText editTextUser = findViewById(R.id.txt_username);
        EditText editTextEmail = findViewById(R.id.txt_email);
        EditText editTextGender = findViewById(R.id.txt_gender);



        
        setContentView(R.layout.activity_logged_in);
    }


}
