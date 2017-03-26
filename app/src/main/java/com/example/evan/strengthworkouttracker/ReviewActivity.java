//to do:
//      implement default workouts sorted by date
//      move delete button from main to review
//      add delete button function, delete a specific row
//      implement buttons on bottom row to sort table by different stats
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
    public boolean looper = true;
    public boolean tableFirstCreate = true;


    public Button weightSortButton;
    public Button deleteButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        weightSortButton = (Button)findViewById(R.id.weightSortButton);
        weightSortButton.setOnClickListener(this);
        deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        loadActivity("records");


    }

    public void updateDB(int id, String d, String wrk, String wght, String rps, String tableName){





        ContentValues insertValues = new ContentValues();
        insertValues.put("id", id);
        insertValues.put("date", d);
        insertValues.put("workout", wrk);
        insertValues.put("weight", wght);
        insertValues.put("reps", rps);



        workoutBase.insert(tableName, null, insertValues);

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
            case "weightsort": c = workoutBase.rawQuery("SELECT * FROM weightsort", null);
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

        if(c!=null && c.getCount()>0) {
            c.moveToFirst();
            while (looper) {
                TableRow row = new TableRow(this);
                TextView t1 = new TextView(this);
                TextView t2 = new TextView(this);
                TextView t3 = new TextView(this);
                TextView t4 = new TextView(this);
                TextView t5 = new TextView(this);

                    entry = c.getString(c.getColumnIndex("id"));
                    t1.setText(entry);
                    entry = c.getString(c.getColumnIndex("date"));
                    t2.setText(entry);
                    entry = c.getString(c.getColumnIndex("workout"));
                    t3.setText(entry);
                    entry = c.getString(c.getColumnIndex("weight"));
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
                        //                    index++;
                        c.moveToNext();
                    }

            }


        }
    }

    public void sortWeight(){
        boolean swLooper = true;
        boolean topLooper = true;
        String getWeightString;
        int getWeight;

        int newID = 1;
        int maxIDInt = 0;
        int lastWeight = 0;


        MyDatabase db = new MyDatabase(this);
        workoutBase = db.getWritableDatabase();

        workoutBase.execSQL("DELETE FROM tempsort"); //empties tempsort from any previous uses
        workoutBase.execSQL("DELETE FROM weightsort"); //empties weightsort from any previous uses
        workoutBase.execSQL("INSERT INTO tempsort SELECT * FROM records"); //copies the contents of records table into tempsort



        while(topLooper) {
            Cursor tempC = workoutBase.rawQuery("SELECT * FROM tempsort", null);
            //inner loop, runs through the tempsort table, finds the highest
            //weight, adds that row to weightsort, deletes the row from tempsort
            tempC.moveToFirst();
            while (swLooper) {
                getWeightString = tempC.getString(tempC.getColumnIndex("weight"));  //grabs the weight column as a string
                getWeight = Integer.parseInt(getWeightString); //converts it to an int
                if (getWeight >= lastWeight) { //if weight of current index is greater than the previous
                    lastWeight = getWeight;
                    maxIDInt = tempC.getInt(tempC.getColumnIndex("id")); //the max ID is set to this current index
                    //maxIDInt = Integer.parseInt(maxID); //and changed to an integer

                }
                if (tempC.isLast()) { //if the cursor is at the end of this run through
                    swLooper = false; //reset the inner looper


                    tempC = workoutBase.rawQuery("SELECT * FROM tempsort WHERE id= " + maxIDInt, null); //make a new cursor, consisting of only the row matching the ID of the current maxweight
                    tempC.moveToFirst(); //initialize the cursor to the only row
                    //run updateDB, with all the values from the current row, and whatever newID is currently incremented to
                    updateDB(newID, tempC.getString(tempC.getColumnIndex("date")), tempC.getString(tempC.getColumnIndex("workout")), tempC.getString(tempC.getColumnIndex("weight")), tempC.getString(tempC.getColumnIndex("reps")), "weightsort");
                    tempC = workoutBase.rawQuery("SELECT * FROM tempsort", null); //renew the cursor to include the entire tempsort table
                    workoutBase.execSQL("DELETE FROM tempsort WHERE id = " + maxIDInt); //and drop that row from the temp table
                    newID++;
                }
                else{ //if not at the end yet, move cursor and loop again
                    tempC.moveToNext();
                }
            }
            tempC = workoutBase.rawQuery("SELECT * FROM tempsort", null); //refreshes the cursor before checking if the table is empty
            if (tempC==null || tempC.getCount()==0) {
                //checks to see if all rows have been removed from tempsort
                //if so, ends the top loop, and calls loadActivity
                topLooper = false;
                tableFirstCreate=true;
                loadActivity("weightsort");
            }
            else{ //if not finished, reset all counters and looper
                swLooper=true;
                getWeight = 0;
                lastWeight=0;
                //maxID = "";
                maxIDInt = 0;
            }
        }

    }

    @Override
    public void onClick(View v){
        if (v==weightSortButton){
            sortWeight();

        }
        else if (v==deleteButton){
            MyDatabase db = new MyDatabase(this);
            workoutBase = db.getWritableDatabase();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This will delete all workouts. Are you sure?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //clicked OK
                    //Deletes all rows from the records table
                    //resets the header checker and calls method to recreate the table
                    workoutBase.execSQL("DELETE FROM records");
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
