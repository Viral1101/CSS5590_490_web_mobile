package com.csee5590.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SignUpActivity extends AppCompatActivity {

    public void onClick(View v){
        Intent redirect = new Intent(this, MainActivity.class);
        startActivity(redirect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        String userName = intent.getStringExtra(EXTRA_MESSAGE);

        TextView user = findViewById(R.id.userTxt);
        user.setText(userName);
        user.setVisibility(View.VISIBLE);
    }
}
