package com.example.paxilpaz.countdowntimer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.paxilpaz.countdowntimer.R;
import com.example.paxilpaz.countdowntimer.timer.TimerData;
import com.example.paxilpaz.countdowntimer.views.listeners.DoubleClickListener;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by paxilpaz on 21/04/16.
 */
public class ShotClockTimer extends RelativeLayout implements View.OnLongClickListener, Observer {

    private static final int digitsID[] = {R.drawable.digit_0,
            R.drawable.digit_1,
            R.drawable.digit_2,
            R.drawable.digit_3,
            R.drawable.digit_4,
            R.drawable.digit_5,
            R.drawable.digit_6,
            R.drawable.digit_7,
            R.drawable.digit_8,
            R.drawable.digit_9};

    private TimerData timerData;

    private LayoutInflater layoutInflater;

    //Action ImageViews
    private ImageView tens_seconds_shot_clock;
    private ImageView seconds_shot_clock;
    private ImageView shot_clock_separator;
    private ImageView tenths_of_seconds_shot_clock;

    //Variables to prevent refreshes
    private int previousActionTensSeconds;
    private int previousActionSeconds;
    private int previousActionTenthsSeconds;

    public ShotClockTimer(Context context) {
        super(context);
        init();

    }

    public ShotClockTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShotClockTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Inflate layout
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.shot_clock_timer, this, true);
        //Timer data setting
        timerData = TimerData.getInstance(getContext());
        timerData.addObserver(this);
        //Get ShotClock ImageViews
        tens_seconds_shot_clock = (ImageView)findViewById(R.id.tens_seconds_action);
        seconds_shot_clock = (ImageView)findViewById(R.id.seconds_action);
        shot_clock_separator = (ImageView)findViewById(R.id.dot);
        tenths_of_seconds_shot_clock = (ImageView)findViewById(R.id.tenth_of_seconds);

        //Setting variables to -1 when creating the Activity
        resetVariablesForRefreshes();

        //Setting the click listener
        setClickListener();
    }

    private void setClickListener() {
        this.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View view) {
                //Reset shot clock rebound
                timerData.resetOffensiveRebound();
                if (timerData.isPaused()) {
                    //Timer was paused... We will update the action timer in order to show 14,0
                    resetActionTimerToSeconds(1, 4); //TODO reset to proper time
                }
            }

            @Override
            public void onSingleClick(View view) {
                //Reset shot clock
                timerData.resetShotClock();
                if (timerData.isPaused()) {
                    //Timer was paused... We will update the action timer in order to show 24,0
                    resetActionTimerToSeconds(2, 4); //TODO reset to proper time
                }
            }
        });
    }

    @Override
    public boolean onLongClick(View view) {
        //TODO display a PopupDialog to set the ShotCLock Timer manually
        Toast.makeText(getContext(),"Long clicked", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void update(Observable observable, Object o) {
        //Just to be sure, we check that the observable is our TimerData
        if (observable instanceof TimerData) {
            //Update the GUI, starting with the period timer
            if (timerData.getPeriodMinutesTens() == Integer.MIN_VALUE) {

                tens_seconds_shot_clock.setImageResource(R.drawable.dash);
                seconds_shot_clock.setImageResource(R.drawable.dash);
                shot_clock_separator.setImageResource(R.drawable.middle_dot);
                tenths_of_seconds_shot_clock.setImageResource(R.drawable.dash);

                shot_clock_separator.setImageResource(R.drawable.middle_dot);

                resetVariablesForRefreshes();

            } else if (timerData.getPeriodMinutesTens() == -1) {
                //In this case, we are in the last minute

                //refreshing the action timer
                if (timerData.getActionSecondsTens() == -1) {
                    //Action time is more than peridd time, display dashes and reset values of variables
                    dashAction();
                } else {
                    if (timerData.getActionSecondsTens() == 0 &&
                            timerData.getActionSeconds() == 0 &&
                            timerData.getActionSecondsTenths() == 0) {
                        //Action finished. Display dashes, refactor pause button
                        dashAction();
                    } else {
                        if (previousActionTensSeconds != timerData.getActionSecondsTens()) {
                            previousActionTensSeconds = timerData.getActionSecondsTens();
                            tens_seconds_shot_clock.setImageResource(digitsID[previousActionTensSeconds]);
                        }
                        if (previousActionSeconds != timerData.getActionSeconds()) {
                            previousActionSeconds = timerData.getActionSeconds();
                            seconds_shot_clock.setImageResource(digitsID[previousActionSeconds]);
                        }
                        if (previousActionTenthsSeconds != timerData.getActionSecondsTenths()) {
                            previousActionTenthsSeconds = timerData.getActionSecondsTenths();
                            tenths_of_seconds_shot_clock.setImageResource(digitsID[previousActionTenthsSeconds]);
                        }
                        shot_clock_separator.setImageResource(R.drawable.dot);
                    }
                }
            } else {
                //Normal behaviour

                //refreshing the action timer
                if (timerData.getActionSecondsTens() == -1) {
                    //Action is finished, display dashes and reset values of variables
                    dashAction();
                } else {
                    if (timerData.getActionSecondsTens() == 0 &&
                            timerData.getActionSeconds() == 0 &&
                            timerData.getActionSecondsTenths() == 0) {
                        //Action finished. Display dashes, refactor pause button
                        dashAction();
                    } else {
                        if (previousActionTensSeconds != timerData.getActionSecondsTens()) {
                            previousActionTensSeconds = timerData.getActionSecondsTens();
                            tens_seconds_shot_clock.setImageResource(digitsID[previousActionTensSeconds]);
                        }
                        if (previousActionSeconds != timerData.getActionSeconds()) {
                            previousActionSeconds = timerData.getActionSeconds();
                            seconds_shot_clock.setImageResource(digitsID[previousActionSeconds]);
                        }
                        if (previousActionTenthsSeconds != timerData.getActionSecondsTenths()) {
                            previousActionTenthsSeconds = timerData.getActionSecondsTenths();
                            tenths_of_seconds_shot_clock.setImageResource(digitsID[previousActionTenthsSeconds]);
                        }
                        shot_clock_separator.setImageResource(R.drawable.dot);
                    }

                }
            }
        }
    }

    private void resetVariablesForRefreshes() {
        previousActionTensSeconds = -1;
        previousActionSeconds = -1;
        previousActionTenthsSeconds = -1;
    }

    private void resetActionTimerToSeconds(int tensOfSecs, int secs) {
        tens_seconds_shot_clock.setImageResource(digitsID[tensOfSecs]);
        seconds_shot_clock.setImageResource(digitsID[secs]);
        tenths_of_seconds_shot_clock.setImageResource(digitsID[0]);
        shot_clock_separator.setImageResource(R.drawable.dot);
        previousActionTensSeconds = tensOfSecs;
        previousActionSeconds = secs;
        previousActionTenthsSeconds = 0;
    }

    private void dashAction() {
        tens_seconds_shot_clock.setImageResource(R.drawable.dash);
        seconds_shot_clock.setImageResource(R.drawable.dash);
        tenths_of_seconds_shot_clock.setImageResource(R.drawable.dash);
        shot_clock_separator.setImageResource(R.drawable.middle_dot);

        previousActionTensSeconds = -1;
        previousActionTenthsSeconds = -1;
        previousActionSeconds = -1;
    }
}
