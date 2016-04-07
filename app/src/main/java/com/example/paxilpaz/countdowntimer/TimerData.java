package com.example.paxilpaz.countdowntimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.Observable;

/**
 * Created by paxilpaz on 07/04/16.
 */
public class TimerData extends Observable {

    private static TimerData instance = null;

    private static final int MSEC_IN_SEC = 1000;
    private static final int SECS_IN_MIN = 60;


    //Durations (in seconds)
    private int periodDuration;
    private int actionDuration;

    //Periods instants
    private int periodMinutesTens;
    private int periodMinutes;
    private int periodSecondsTens;
    private int periodSeconds;
    private int periodSecondsTenths;
    private int periodSecondsHundreths;

    //Action instants
    private int actionSecondsTens;
    private int actionSeconds;
    private int actionSecondsTenths;

    //

    //Context
    private Context context;

    public static TimerData getInstance(Context context) {
        if (instance == null)
            instance = new TimerData(context);
        return instance;
    }

    private TimerData(Context context) {
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String periodDurationString = preferences.getString(context.getResources().getString(R.string.period_duration_setting),
                context.getResources().getString(R.string.period_duration_default_setting));
        String periodsMinutesSeconds[] = periodDurationString.split(":");
        periodDuration = Integer.parseInt(periodsMinutesSeconds[0]) * SECS_IN_MIN + Integer.parseInt(periodsMinutesSeconds[1]);


        String actionDurationString = preferences.getString(context.getResources().getString(R.string.action_duration_setting),
                context.getResources().getString(R.string.action_duration_default_setting));
        actionDuration = Integer.parseInt(actionDurationString);
    }

    @Nullable
    private int getPeriodMinutesTens() { return periodMinutesTens; }

}
