package com.example.paxilpaz.countdowntimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

/**
 * Created by paxilpaz on 07/04/16.
 */
public class TimerData extends Observable {

    //Only instance of TimerData within the application
    private static TimerData instance = null;

    //Number of seconds in a minute
    private static final int SECS_IN_MIN = 60;

    //Number of milliseconds in a minute
    private static final long MSEC_IN_MIN = 60000;

    //Durations (in seconds)
    private int periodTime;
    private int actionTime;

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

    //Context
    private Context context;


    /**
     * Gets the only copy of TimerData within the application
     * @param context the context that will be used to retrieve the preferences
     * @return the only copy of TimerData within the application
     */
    public static TimerData getInstance(Context context) {
        if (instance == null)
            instance = new TimerData(context);
        return instance;
    }

    private TimerData(Context context) {
        this.context = context;
        getTimersDurationFromPreferences();
    }

    /*
    Retrieves and parses the durations of period and action from the Preferences
     */
    private void getTimersDurationFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String periodDurationString = preferences.getString(context.getResources().getString(R.string.period_duration_setting),
                context.getResources().getString(R.string.period_duration_default_setting));
        String periodsMinutesSeconds[] = periodDurationString.split(":");
        periodTime = Integer.parseInt(periodsMinutesSeconds[0]) * SECS_IN_MIN + Integer.parseInt(periodsMinutesSeconds[1]);


        String actionDurationString = preferences.getString(context.getResources().getString(R.string.action_duration_setting),
                context.getResources().getString(R.string.action_duration_default_setting));
        actionTime = Integer.parseInt(actionDurationString);
    }

    /**
     * Updates the internal variables and notifies the observers of the change occurred
     * @param periodMillis the remaining milliseconds in the Period
     * @param actionMillis the remaining milliseconds in the Action
     */
    public void updateData(long periodMillis, long actionMillis) {
        //Evaluate if the period is finished
        if (periodMillis == 0) {
            /*
            In this case, notify the observers that the period is over by putting the tens of
            minutes variable for the period timer to -1
             */
            periodMinutesTens = Integer.MIN_VALUE;
        } else {
            //Evaluate if we are in the last minute
            if (periodMillis < MSEC_IN_MIN) {
                periodMinutesTens = -1;
                periodMinutes = -1;
                //We will start by computing the period timer in format ss.mm
                periodSecondsTens = (int) ((TimeUnit.MILLISECONDS.toSeconds(periodMillis)) / 10);
                periodSeconds = (int) ((TimeUnit.MILLISECONDS.toSeconds(periodMillis)) % 10);
                periodSecondsTenths = (int) ((periodMillis / 100) % 10);
                periodSecondsHundreths = (int) ((periodMillis / 10) % 10 );

                //Evaluate if we are in the last action
                if (periodMillis < actionMillis) {
                /*
                In this case, we will only update the Period timer in format ss.mm, the action
                timer will be shut down
                 */
                    actionSeconds = -1;
                    actionSecondsTens = -1;
                    actionSecondsTenths = -1;
                } else {
                    //If we are not in the last action, we will need to update the action timer as well
                    actionTimerUpdate(actionMillis);
                }
            } else {
                //We are not in the last minute, so normal behaviour
                periodMinutesTens = (int) (TimeUnit.MILLISECONDS.toMinutes(periodMillis) / 10);
                periodMinutes = (int) (TimeUnit.MILLISECONDS.toMinutes(periodMillis) % 10);
                periodSecondsTens = (int) ((TimeUnit.MILLISECONDS.toSeconds(periodMillis) -
                        TimeUnit.MINUTES.toSeconds(periodMinutesTens * 10 + periodMinutes)) / 10);
                periodSeconds = (int) ((TimeUnit.MILLISECONDS.toSeconds(periodMillis) -
                        TimeUnit.MINUTES.toSeconds(periodMinutesTens * 10 + periodMinutes)) % 10);
                actionTimerUpdate(actionMillis);
            }
        }
        //Notify observers
        setChanged();
        notifyObservers();
    }

    /*
    Updates the action timer with the correct values
     */
    private void actionTimerUpdate(long actionMillis) {
        //Handling action timer
        actionSecondsTens = (int) (TimeUnit.MILLISECONDS.toSeconds(actionMillis) / 10);
        actionSeconds = (int) (TimeUnit.MILLISECONDS.toSeconds(actionMillis) % 10);
        actionSecondsTenths = (int) ((actionMillis /100) % 10);
    }


    /**
     *
     * @return the period duration for the game in seconds
     */
    public long getPeriodTime() {
        return periodTime;
    }

    /**
     *
     * @return the action duration for the game in seconds
     */
    public long getActionTime() {
        return actionTime;
    }

    public int getPeriodMinutesTens() {
        return periodMinutesTens;
    }

    public int getPeriodMinutes() {
        return periodMinutes;
    }

    public int getPeriodSecondsTens() {
        return periodSecondsTens;
    }

    public int getPeriodSeconds() {
        return periodSeconds;
    }

    public int getPeriodSecondsTenths() {
        return periodSecondsTenths;
    }

    public int getPeriodSecondsHundreths() {
        return periodSecondsHundreths;
    }

    public int getActionSecondsTens() {
        return actionSecondsTens;
    }

    public int getActionSeconds() {
        return actionSeconds;
    }

    public int getActionSecondsTenths() {
        return actionSecondsTenths;
    }
}
