package com.example.evan.strengthworkouttracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import java.sql.PreparedStatement;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    public Button workoutButton;
    public Button reviewButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workoutButton = (Button)findViewById(R.id.workoutButton);
        workoutButton.setOnClickListener(this);

        reviewButton = (Button)findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(this);


    }

    @Override
    public void onClick (View v){
        if (v == workoutButton){
            launchWorkout();
        }
        else if (v==reviewButton){
            launchReview();
        }

    }

    private void launchWorkout(){
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    private void launchReview(){
        Intent intent = new Intent(this, ReviewActivity.class);
        startActivity(intent);
    }
}
