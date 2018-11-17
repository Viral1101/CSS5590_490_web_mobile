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
/*
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDB();
            }
        });
*/
    }

    private void updateDB(){

    }

    private void deleteFromDB(){
        String name = binding.nameEditText.getText().toString();
        String desc = binding.descEditText.getText().toString();

        SQLiteDatabase database = new SampleDBSQLiteHelper(this).getWritableDatabase();

        String selection =
                SampleDBContract.Employer.COLUMN_NAME + " like ? and " +
                        SampleDBContract.Employer.COLUMN_DESCRIPTION + " like ?";

        String[] selectionArgs = {"%" + name + "%", "%" + desc + "%"};

        long newRowId = database.delete(SampleDBContract.Employer.TABLE_NAME, selection, selectionArgs);
        readFromDB();

        Toast.makeText(this, "Deleted Row Id: " + newRowId, Toast.LENGTH_LONG).show();
    }

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

        //if the search returns only 1 row, activate the delete and update buttons
        if(binding.recycleView.getAdapter().getItemCount() == 1){
            binding.deleteButton.setVisibility(View.VISIBLE);
        }else{
            binding.deleteButton.setVisibility(View.INVISIBLE);
        }

    }
}