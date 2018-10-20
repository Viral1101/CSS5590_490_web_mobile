package com.csee5590.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    public void checkCredetials(View v){
        EditText usernameCtrl = (EditText)findViewById(R.id.username_text);
        EditText passwordCtrl = (EditText)findViewById(R.id.pwd_text);
        TextView errorText = (TextView)findViewById(R.id.lbl_Error);
        String userName = usernameCtrl.getText().toString();
        String password = passwordCtrl.getText().toString();

        boolean validationFlag = false;

        if(!userName.isEmpty() && !password.isEmpty()){
            if(userName.equals("Admin") && password.equals("Admin")){
                validationFlag = true;
            }
        }
        if(!validationFlag){
            errorText.setVisibility(View.VISIBLE);
        }
        else{
            reDirectToHomePage(v, userName);
        }
    }

    private void reDirectToHomePage(View v, String user){
        Intent redirect = new Intent(this, SignUpActivity.class);
        redirect.putExtra(EXTRA_MESSAGE,user);
        startActivity(redirect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
