//to do:
//     Add funtions to DatabaseHelper to simplify things

package com.example.evan.strengthworkouttracker;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener{

    public SQLiteDatabase workoutBase;
    public String entry;
    public int indexCounter;
    public boolean looper = true;


    public Button weightSortButton;
    public Button dateSortButton;
    public Button deleteButton;
    public Spinner graphDropdown;




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
        graphDropdown = (Spinner)findViewById(R.id.graphSpinner);
        graphDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                createChart(graphDropdown.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });

        loadActivity("records");


        String[] workoutList = new String[]{"Select Workout", "Pushups", "Situps", "Benchpress", "Squats", "Deadlifts"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, workoutList);
        graphDropdown.setAdapter(adapter);


    }


    //organizes the records table, selecting only the weight column from specific workout
    //makes a DataPoint list, traversing through the table and assigning the weight value
    //returns when called by createChart
    private DataPoint[] generateData(String selection){
        DatabaseHelper db = new DatabaseHelper(this);
        workoutBase = db.getWritableDatabase();
        String sortMetric;
        if(selection.equals("Pushups") || selection.equals("Situps")){
            sortMetric = "totalreps";
        }
        else{
            sortMetric = "weight";
        }

        Cursor c = workoutBase.rawQuery("SELECT " + sortMetric + ", sortdate FROM records WHERE workout='" + selection +  "' ORDER BY sortdate ASC", null);
        int listLength = c.getCount();
        c.moveToFirst();

        DataPoint[] values = new DataPoint[listLength];
        for (int i=0; i<listLength; i++){
            DataPoint v = new DataPoint(i+1, c.getInt(c.getColumnIndex(sortMetric)));
            values[i] = v;
            c.moveToNext();
        }
        return values;
    }

    //creates the graph, takes param from the graphDropdown spinner
    private void createChart(String selection){


        GraphView graph = (GraphView)findViewById(R.id.graph);
        graph.removeAllSeries();
        if(selection.equals("Select Workout")==false) {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(generateData(selection));
            graph.addSeries(series);


        }
    }


    private void loadActivity(String pickTable){

        //creates and populates the table


        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        table.removeAllViews();
        looper = true;

        //Set up database
        DatabaseHelper db = new DatabaseHelper(this);
        workoutBase = db.getWritableDatabase();

        //Establish cursor and selects table based on the arg
        Cursor c;
        switch (pickTable){
            case "records": c = workoutBase.rawQuery("SELECT * FROM records", null);
                break;
            case "weightsort": c = workoutBase.rawQuery("SELECT * FROM tempsort ORDER BY weight DESC", null);
                break;
            case "datesort": c = workoutBase.rawQuery("SELECT * FROM tempsort ORDER BY sortdate DESC", null);
                break;
            default: c = workoutBase.rawQuery("SELECT * FROM records", null);
                break;
        }




        //creates the headers

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
            ft5.setText("Sets");
            frow.addView(ft1);
            frow.addView(ft2);
            frow.addView(ft3);
            frow.addView(ft4);
            frow.addView(ft5);
            table.addView(frow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));




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
                    entry = (Integer.toString(c.getInt(c.getColumnIndex("sets"))) + "x" + c.getString(c.getColumnIndex("reps")));
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

        DatabaseHelper db = new DatabaseHelper(this);
        workoutBase = db.getWritableDatabase();

        workoutBase.execSQL("DELETE FROM tempsort"); //empties tempsort from any previous uses
        workoutBase.execSQL("INSERT INTO tempsort SELECT * FROM records"); //copies the contents of records table into tempsort

        loadActivity("datesort");
    }

    public void sortWeight(){

        DatabaseHelper db = new DatabaseHelper(this);
        workoutBase = db.getWritableDatabase();


        workoutBase.execSQL("DELETE FROM tempsort"); //empties tempsort from any previous uses
        workoutBase.execSQL("INSERT INTO tempsort SELECT * FROM records"); //copies the contents of records table into tempsort


        loadActivity("weightsort");

    }

    public void deleteRow(){
        DatabaseHelper db = new DatabaseHelper(this);
        workoutBase = db.getWritableDatabase();




        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setMessage("Enter the number of the row to delete:");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                int inputRow;
                int fixIndexCounter = 0;

                Cursor c = workoutBase.rawQuery("SELECT * FROM records", null);
                if(c!=null && c.getCount()>0 && (input.getText().toString().equals("")==false)){

                    inputRow = Integer.parseInt(input.getText().toString());
                    workoutBase.execSQL("DELETE FROM records WHERE id = " + inputRow);
                    fixIndexCounter = c.getCount() - inputRow + 1;
                    while (fixIndexCounter>0){
                        workoutBase.execSQL("UPDATE records SET id=" + (inputRow-1) + " WHERE id=" + (inputRow));
                        fixIndexCounter--;
                        inputRow++;
                    }
                    loadActivity("records");
                    graphDropdown.setSelection(0);
                    createChart(graphDropdown.getSelectedItem().toString());
                }

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

    @Override
    public void onClick(View v){
        if (v==weightSortButton){
            sortWeight();

        }
        else if (v==dateSortButton){
            sortDate();
        }
        else if (v==deleteButton){
            DatabaseHelper db = new DatabaseHelper(this);
            workoutBase = db.getWritableDatabase();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This will delete all workouts. Are you sure?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                    //clicked OK
                    //Deletes all rows from the records table
                    //calls method to recreate the table
                    //resets the graph
                    workoutBase.execSQL("DELETE FROM records");
                    loadActivity("records");
                    graphDropdown.setSelection(0);
                    createChart(graphDropdown.getSelectedItem().toString());



                }
            } );

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            builder.setNeutralButton("Delete Row", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteRow();
                }
            });


            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

}
