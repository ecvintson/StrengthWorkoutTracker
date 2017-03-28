package com.example.evan.strengthworkouttracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WorkoutActivity extends AppCompatActivity implements OnClickListener{

    public Button button;
    public EditText workoutEdit;
    public EditText weightEdit;
    public EditText repsEdit;
    public DatePicker datePick;

    public int monthNum;
    public int day;
    public int year;
    public int dateSort;
    public int index;
    public String monthString;
    public String dayString;
    public String yearString;
    public String dateString;
    public String workout;
    public int weight;
    public String reps;


    public int strLength;
    public boolean tooLong;



    public SQLiteDatabase workoutBase;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        button  = (Button)findViewById(R.id.submitButton);
        button.setOnClickListener(this);

        workoutEdit = (EditText)findViewById(R.id.editWorkout);
        weightEdit = (EditText)findViewById(R.id.editWeight);
        repsEdit = (EditText)findViewById(R.id.editReps);
        datePick = (DatePicker)findViewById(R.id.datePicker5);

        tooLong = false;


        workoutEdit.requestFocus();


        MyDatabase db = new MyDatabase(this);
        workoutBase = db.getWritableDatabase();
        Cursor c = workoutBase.rawQuery("SELECT * FROM records", null);
        if(c!=null && c.getCount()>0){
            c.moveToLast();
            index = c.getInt(c.getColumnIndex("id"));
        }
        else{
            index = 0;
        }






    }

    public void updateDB(int id, int yr, int mo, int dy, String dt, String wrk, int wght, String rps, int dtsrt){





        ContentValues insertValues = new ContentValues();
        insertValues.put("id", id);
        insertValues.put("year", yr);
        insertValues.put("month", mo);
        insertValues.put("day", dy);
        insertValues.put("date", dt);
        insertValues.put("workout", wrk);
        insertValues.put("weight", wght);
        insertValues.put("reps", rps);
        insertValues.put("sortdate", dtsrt);



        workoutBase.insert("records", null, insertValues);

    }

    public void checkLength(String toCheck){
        strLength = toCheck.length();
        if (strLength>20){
            tooLong = true;
        }
    }

    @Override
    public void onClick(View v){
        if(v==button){

            monthNum = datePick.getMonth();
            day = datePick.getDayOfMonth();
            year = datePick.getYear();

            monthNum++;


            monthString = Integer.toString(monthNum);
            if(monthString.length()<2){
                monthString = ("0" + monthString);
            }
            dayString = Integer.toString(day);
            if(dayString.length()<2){
                dayString = ("0" + dayString);
            }
            yearString = Integer.toString(year);



            dateString = (yearString + monthString + dayString );
            dateSort = Integer.parseInt(dateString);
            year = year - 2000;
            dateString = (monthString + "/" + dayString + "/" + yearString);



            workout = workoutEdit.getText().toString();
            reps = repsEdit.getText().toString();


            //checks if weight or reps box is empty
            //if empty, sets the vars to 0
            //otherwise, sets to contents of box
            if((weightEdit.getText().toString()).equals("")){
                weight = 0;
            }
            else{
                weight = Integer.parseInt(weightEdit.getText().toString());
            }
            if((repsEdit.getText().toString()).equals("")){
                reps = "0";
            }
            else{
                reps = repsEdit.getText().toString();
            }

            checkLength(workout);
            checkLength(weightEdit.getText().toString());
            checkLength(reps);

            //checks to make sure entries aren't too long before updating the db
            if(tooLong == false) {
                index++;
                updateDB(index, year, monthNum, day, dateString, workout, weight, reps, dateSort);


                CharSequence text = "Workout saved!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }
            else {
                CharSequence text = "Entry too long!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }

            //resets editText boxes, focus set to top box
            tooLong = false;
            workoutEdit.setText("");
            weightEdit.setText("");
            repsEdit.setText("");
            workoutEdit.requestFocus();



        }

    }

}
