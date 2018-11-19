package com.vijaya.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vijaya.sqlite.databinding.ActivityEmployerBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EmployerActivity extends AppCompatActivity {

    private static final String TAG = "EmployerActivity";
    private ActivityEmployerBinding binding;
    private String rowID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_employer);

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDB();
            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromDB();
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromDB();
            }
        });

        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDB();
            }
        });

    }

    private void updateDB(){

        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SampleDBContract.Employer.COLUMN_NAME, binding.nameEditText.getText().toString());
        values.put(SampleDBContract.Employer.COLUMN_DESCRIPTION, binding.descEditText.getText().toString());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    binding.foundedEditText.getText().toString()));
            long date = calendar.getTimeInMillis();
            values.put(SampleDBContract.Employer.COLUMN_FOUNDED_DATE, date);
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            return;
        }

        //Construct the update format using the rowID set by readDB function
        int success = database.update(SampleDBContract.Employer.TABLE_NAME, values,
                "_id=?", new String[]{rowID} );

        if(success!=0){
            Toast.makeText(this, "Updated Row Id: " + rowID, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Update Failed", Toast.LENGTH_LONG).show();
        }

        //Refresh the recyclerView
        readFromDB();
    }

    private void deleteFromDB(){

        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getWritableDatabase();

        String selection =
                SampleDBContract.Employer._ID + " = ? ";

        String[] selectionArgs = {rowID};

        long success = database.delete(SampleDBContract.Employer.TABLE_NAME, selection, selectionArgs);
        readFromDB();

        //Report if the deletion was successful
        if (success != 0) {
            Toast.makeText(this, "Deleted Row Id: " + rowID, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Delete Failed", Toast.LENGTH_LONG).show();
        }    }

    private void saveToDB() {
        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SampleDBContract.Employer.COLUMN_NAME, binding.nameEditText.getText().toString());
        values.put(SampleDBContract.Employer.COLUMN_DESCRIPTION, binding.descEditText.getText().toString());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((new SimpleDateFormat("dd/MM/yyyy")).parse(
                    binding.foundedEditText.getText().toString()));
            long date = calendar.getTimeInMillis();
            values.put(SampleDBContract.Employer.COLUMN_FOUNDED_DATE, date);
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
            Toast.makeText(this, "Date is in the wrong format", Toast.LENGTH_LONG).show();
            return;
        }
        long newRowId = database.insert(SampleDBContract.Employer.TABLE_NAME, null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        String name = binding.nameEditText.getText().toString();
        String desc = binding.descEditText.getText().toString();

        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getReadableDatabase();

        String[] selectionArgs = {"%" + name + "%", "%" + desc + "%"};

        Cursor cursor = database.rawQuery(SampleDBContract.SELECT_EMPLOYER, selectionArgs);
        binding.recycleView.setAdapter(new SampleRecyclerViewCursorAdapter(this, cursor));

        if(cursor.getCount() == 1){
            //move the cursor to the first (and only item)
            cursor.moveToFirst();
            //save the ID to update the row if desired
            rowID = Long.toString(cursor.getLong(cursor.getColumnIndexOrThrow(SampleDBContract.Employer._ID)));
            //make the buttons visible to allow delete and update functions
            binding.deleteButton.setVisibility(View.VISIBLE);
            binding.updateButton.setVisibility(View.VISIBLE);

        }else{
            //hide the buttons to disable the delete and update functions
            binding.deleteButton.setVisibility(View.INVISIBLE);
            binding.updateButton.setVisibility(View.INVISIBLE);
        }
    }
}