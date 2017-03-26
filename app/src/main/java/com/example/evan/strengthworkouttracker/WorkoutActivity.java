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
    public int index;
    public String monthString;
    public String dayString;
    public String yearString;
    public String dateString;
    public String workout;
    public String weight;
    public String reps;
    public String indexString;

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






    }

    public void updateDB(int id, String d, String wrk, String wght, String rps){





        ContentValues insertValues = new ContentValues();
        insertValues.put("id", id);
        insertValues.put("date", d);
        insertValues.put("workout", wrk);
        insertValues.put("weight", wght);
        insertValues.put("reps", rps);



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
            year = year - 2000;

            monthString = Integer.toString(monthNum);
            dayString = Integer.toString(day);
            yearString = Integer.toString(year);

            dateString = (monthString + "/" + dayString + "/" + yearString);



            workout = workoutEdit.getText().toString();
            weight = weightEdit.getText().toString();
            reps = repsEdit.getText().toString();

            if(weight.equals("")){
                weight = "0";
            }

            checkLength(workout);
            checkLength(weight);
            checkLength(reps);

            Cursor c = workoutBase.rawQuery("SELECT * FROM records", null);
            if (c!=null && c.getCount()>0) {
                c.moveToLast();
                indexString = c.getString(c.getColumnIndex("id"));
                index = Integer.parseInt(indexString);
                index++;
            }
            else{
                index = 1;
            }


            if(tooLong == false) {

                updateDB(index, dateString, workout, weight, reps);


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

            tooLong = false;
            workoutEdit.setText("");
            weightEdit.setText("");
            repsEdit.setText("");
            workoutEdit.requestFocus();



        }

    }

}
