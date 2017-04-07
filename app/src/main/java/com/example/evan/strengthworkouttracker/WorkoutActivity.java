package com.example.evan.strengthworkouttracker;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public class WorkoutActivity extends AppCompatActivity implements OnClickListener{

    public Button button;
    public EditText weightEdit;
    public EditText repsEdit;
    public EditText dateEdit;
    public EditText setsEdit;
    public Spinner workoutDropdown;


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
    public int sets;
    public int totalReps;

    Calendar cal = Calendar.getInstance();



    public int strLength;
    public boolean tooLong;



    public SQLiteDatabase workoutBase;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateEditSetter();
        }

    };


    public void dateEditSetter(){
        monthNum = cal.get(Calendar.MONTH);
        monthNum++;
        day = cal.get(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        dateEdit.setText(monthNum + "/" + day + "/" + year);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        button  = (Button)findViewById(R.id.submitButton);
        button.setOnClickListener(this);


        weightEdit = (EditText)findViewById(R.id.editWeight);
        repsEdit = (EditText)findViewById(R.id.editReps);
        setsEdit = (EditText)findViewById(R.id.editSets);
        dateEdit = (EditText)findViewById(R.id.editDate);
        dateEdit.setOnClickListener(this);

        workoutDropdown = (Spinner)findViewById(R.id.workoutSpinner);
        String[] workoutList = new String[]{"Select Workout", "Pushups", "Situps", "Benchpress", "Squats", "Deadlifts"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, workoutList);
        workoutDropdown.setAdapter(adapter);
        workoutDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                if(workoutDropdown.getSelectedItem().toString().equals("Pushups") || workoutDropdown.getSelectedItem().toString().equals("Situps")) {
                    weightEdit.setText("");
                    weightEdit.setFocusable(false);
                }
                else{
                    weightEdit.setFocusable(true);
                    weightEdit.setFocusableInTouchMode(true);
                    weightEdit.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                    weightEdit.setFocusable(true);
                    weightEdit.setFocusableInTouchMode(true);
            }
        });


        tooLong = false;

        LinearLayout myLayout = (LinearLayout)findViewById(R.id.myLayout);
         myLayout.requestFocus();


        DatabaseHelper db = new DatabaseHelper(this);
        workoutBase = db.getWritableDatabase();
        Cursor c = workoutBase.rawQuery("SELECT * FROM records", null);
        if(c!=null && c.getCount()>0){
            c.moveToLast();
            index = c.getInt(c.getColumnIndex("id"));
        }
        else{
            index = 0;
        }

        dateEditSetter();




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

            monthNum = cal.get(Calendar.MONTH);
            monthNum++;
            day = cal.get(Calendar.DAY_OF_MONTH);
            year = cal.get(Calendar.YEAR);
            dateEdit.setText(monthNum + "/" + day + "/" + year);


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



            workout = workoutDropdown.getSelectedItem().toString();



            //checks if weight. resps or sets box is empty
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
            if((setsEdit.getText().toString()).equals("")){
                sets = 0;
            }
            else{
                sets = Integer.parseInt(setsEdit.getText().toString());
            }


            checkLength(weightEdit.getText().toString());
            checkLength(reps);
            checkLength(Integer.toString(sets));

            //checks to make sure entries aren't too long before updating the db
            if(tooLong == false && workout.equals("Select Workout")==false) {
                index++;
                totalReps = Integer.parseInt(reps) * sets;
                DatabaseHelper db = new DatabaseHelper(this);
                db.insertData(index, year, monthNum, day, dateString, workout, weight, reps, dateSort, sets, totalReps);



                CharSequence text = "Workout saved!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }
            else if (tooLong){
                CharSequence text = "Entry too long!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }
            else{
                CharSequence text = "Please select a workout!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }

            //resets editText boxes, clears focus
            tooLong = false;
            workoutDropdown.setSelection(0);
            weightEdit.setText("");
            repsEdit.setText("");
            setsEdit.setText("");
            weightEdit.setFocusable(true);
            weightEdit.setFocusableInTouchMode(true);
            LinearLayout myLayout = (LinearLayout)findViewById(R.id.myLayout);
            myLayout.requestFocus();



        }
        else if (v==dateEdit){
            new DatePickerDialog(this, date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();

        }

    }

}
