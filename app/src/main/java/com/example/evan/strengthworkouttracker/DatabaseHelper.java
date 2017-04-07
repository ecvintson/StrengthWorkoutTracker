package com.example.evan.strengthworkouttracker;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "workoutBase.db";
    public static final String TABLE_NAME = "records";
    public static final String TABLE_NAME2 = "tempsort";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER, year INTEGER, month INTEGER, day INTEGER, date STRING, workout STRING, weight STRING, reps STRING, sortdate INTEGER, sets INTEGER, totalreps INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME2 + "(id INTEGER, year INTEGER, month INTEGER, day INTEGER, date STRING, workout STRING, weight STRING, reps STRING, sortdate INTEGER, sets INTEGER, totalreps INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(int id, int year, int month, int day, String date, String workout, int weight, String reps, int datesort, int sets, int totalreps ){
        SQLiteDatabase workoutBase = this.getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put("id", id);
        insertValues.put("year", year);
        insertValues.put("month", month);
        insertValues.put("day", day);
        insertValues.put("date", date);
        insertValues.put("workout", workout);
        insertValues.put("weight", weight);
        insertValues.put("reps", reps);
        insertValues.put("sets", sets);
        insertValues.put("sortdate", datesort);
        insertValues.put("totalreps", totalreps);
        long result = workoutBase.insert("records", null, insertValues);
       if (result == -1){
           return false;
       }
        else   {
           return true;
       }
    }
}
