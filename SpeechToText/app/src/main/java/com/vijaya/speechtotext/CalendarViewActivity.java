package com.vijaya.speechtotext;

//import android.icu.text.SimpleDateFormat;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewActivity extends AppCompatActivity {

    private CalendarView mCalendar;
    private TextView mText;
    public String outputPattern = "EEEE MMM dd yyyy";
    private Button mButton;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        SimpleDateFormat formatter = new SimpleDateFormat(outputPattern);

        mText = findViewById(R.id.dateSelectText);
        Date now = Calendar.getInstance().getTime();
        mYear = now.getYear();
        mMonth = now.getMonth();
        mDay = now.getDate();
        mText.setText(formatter.format(now));

        mButton = findViewById(R.id.buttonApt);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });

        mCalendar = findViewById(R.id.calendarView);
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                SimpleDateFormat formatter = new SimpleDateFormat(outputPattern);
                Calendar c = Calendar.getInstance();
                c.set(year,month,dayOfMonth);
                Date outDate = c.getTime();
                String outString = formatter.format(outDate);
                mText.setText(outString);

            }
        });
    }
    public void insert() {
        Intent intent = new Intent(Intent.ACTION_INSERT,
                CalendarContract.Events.CONTENT_URI);
        // Add the calendar event details
        intent.putExtra(CalendarContract.Events.TITLE, "Appointment");
        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                "Your doctor's appointment");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,
                "Doctor Nick's Medical Pracitce");
        Calendar aptDate = Calendar.getInstance();
        aptDate.set(mYear,mMonth,mDay);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                aptDate.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        // Use the Calendar app to add the new event.
        startActivity(intent);
    }
}
