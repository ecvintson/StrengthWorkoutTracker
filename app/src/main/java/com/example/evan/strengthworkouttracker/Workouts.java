package com.example.evan.strengthworkouttracker;

/**
 * Created by Evan on 3/14/2017.
 */

public class Workouts {

    public String[] workout = new String[3];
    public int[] reps = new int[3];
    public int[] sets = new int[3];

    public Workouts(){

    }

    public void makeDay(String a, String b, String c, int repx, int repy, int repz, int setx, int sety, int setz){
        workout[0] = a;
        workout[1] = b;
        workout[2] = c;

        reps[1] = repx;
        reps[2] = repy;
        reps[3] = repz;

        sets[1] = setx;
        sets[2] = sety;
        sets[3] = setz;
    }

}
