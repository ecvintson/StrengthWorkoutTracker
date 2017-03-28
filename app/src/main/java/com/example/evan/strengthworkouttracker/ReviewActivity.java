//to do:
//      implement default workouts sorted by date
//      move delete button from main to review
//      add delete button function, delete a specific row
//      implement buttons on bottom row to sort table by different stats
//      refactor weightSort to use more efficient SQLite "ORDER BY"
//          refactor how the id column is implemented (have column populated by a counter in loadActivity?)
//          add month/day/year columns
//          fully implement sortDate
//      create personal library of sqlite functions from functions implemented here

package com.example.evan.strengthworkouttracker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {

    public SQLiteDatabase workoutBase;
    public String entry;
    public int indexCounter;
    public boolean looper = true;
    public boolean tableFirstCreate = true;


    public Button weightSortButton;
    public Button dateSortButton;
    public Button deleteButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        weightSortButton = (Button)findViewById(R.id.weightSortButton);
        weightSortButton.setOnClickListener(this);
        dateSortButton = (Button)findViewById(R.id.dateSortButton);
        dateSortButton.setOnClickListener(this);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        loadActivity("records");


    }



    private void loadActivity(String pickTable){

        //this method creates and populates the table


        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        table.removeAllViews();
        looper = true;

        //Set up database
        MyDatabase db = new MyDatabase(this);
        workoutBase = db.getWritableDatabase();

        //Establish cursor and selects table based on the arg
        Cursor c;
        switch (pickTable){
            case "records": c = workoutBase.rawQuery("SELECT * FROM records", null);
                break;
            case "weightsort": c = workoutBase.rawQuery("SELECT * FROM tempsort ORDER BY weight DESC", null);
                break;
            case "datesort": c = workoutBase.rawQuery("SELECT * FROM tempsort ORDER BY weight DESC", null);
                break;
            default: c = workoutBase.rawQuery("SELECT * FROM records", null);
                break;
        }




        //if the headers haven't been created yet, this creates them
        if(tableFirstCreate){
            TableRow frow = new TableRow(this);
            TextView ft1 = new TextView(this);
            TextView ft2 = new TextView(this);
            TextView ft3 = new TextView(this);
            TextView ft4 = new TextView(this);
            TextView ft5 = new TextView(this);

            ft1.setText("");
            ft2.setText("Date");
            ft3.setText("Workout");
            ft4.setText("Weight");
            ft5.setText("Reps");
            frow.addView(ft1);
            frow.addView(ft2);
            frow.addView(ft3);
            frow.addView(ft4);
            frow.addView(ft5);
            table.addView(frow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tableFirstCreate = false;
        }


        //create 4 textviews, one for each column
        //extract string from database, assign to appropriate textviews
        //add each textview to the new row
        //add the row to the table
        //loop until all entries finished
        indexCounter = 1;
        if(c!=null && c.getCount()>0) {
            c.moveToFirst();
            while (looper) {
                TableRow row = new TableRow(this);
                TextView t1 = new TextView(this);
                TextView t2 = new TextView(this);
                TextView t3 = new TextView(this);
                TextView t4 = new TextView(this);
                TextView t5 = new TextView(this);

                    entry = Integer.toString(c.getInt(c.getColumnIndex("id")));
                    t1.setText(entry);
                    entry = c.getString(c.getColumnIndex("date"));
                    t2.setText(entry);
                    entry = c.getString(c.getColumnIndex("workout"));
                    t3.setText(entry);
                    entry = Integer.toString(c.getInt(c.getColumnIndex("weight")));
                    t4.setText(entry);
                    entry = c.getString(c.getColumnIndex("reps"));
                    t5.setText(entry);
                    row.addView(t1);
                    row.addView(t2);
                    row.addView(t3);
                    row.addView(t4);
                    row.addView(t5);
                    table.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (c.isLast()) {
                        looper = false;
                    } else {
                        indexCounter++;
                        c.moveToNext();
                    }

            }


        }
    }

    public void sortDate(){

        MyDatabase db = new MyDatabase(this);
        workoutBase = db.getWritableDatabase();

        workoutBase.execSQL("DELETE FROM tempsort"); //empties tempsort from any previous uses
        workoutBase.execSQL("INSERT INTO tempsort SELECT * FROM records"); //copies the contents of records table into tempsort

        tableFirstCreate=true;
        loadActivity("tempsort");
    }

    public void sortWeight(){

        MyDatabase db = new MyDatabase(this);
        workoutBase = db.getWritableDatabase();


        workoutBase.execSQL("DELETE FROM tempsort"); //empties tempsort from any previous uses
        workoutBase.execSQL("INSERT INTO tempsort SELECT * FROM records"); //copies the contents of records table into tempsort

        tableFirstCreate=true;
        loadActivity("weightsort");

    }

    @Override
    public void onClick(View v){
        if (v==weightSortButton){
            sortWeight();

        }
        else if (v==dateSortButton){
            sortDate();
        }
        else if (v==deleteButton){
            MyDatabase db = new MyDatabase(this);
            workoutBase = db.getWritableDatabase();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This will delete all workouts. Are you sure?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //clicked OK
                    //resets the id autoincrementer
                    //Deletes all rows from the records table
                    //resets the header checker and calls method to recreate the table
                    workoutBase.execSQL("DELETE FROM records");
                    workoutBase.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='records'");
                    tableFirstCreate=true;
                    loadActivity("records");

                }
            } );

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}
