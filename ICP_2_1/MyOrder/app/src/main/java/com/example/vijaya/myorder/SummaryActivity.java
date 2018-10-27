package com.example.vijaya.myorder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SummaryActivity extends Activity {

    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary);

        final ListView listview = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();

        message = intent.getStringExtra(EXTRA_MESSAGE);

        String[] values = message.split("\n");
        final ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,list);
        listview.setAdapter(adapter);

    }

    public void submitOrder(View view) {

        Uri uri = Uri.parse("mailto:" + "dpwh24@umkc.edu")
                .buildUpon()
                .appendQueryParameter("to", "dpwh24@umkc.edu")
                .appendQueryParameter("subject", "Your pizza order")
                .appendQueryParameter("body", message)
                .build();

        sendEmail(uri, "Select Email");
    }

    public void sendEmail(Uri uri, String chooserTitle) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(emailIntent, chooserTitle));
    }



}
