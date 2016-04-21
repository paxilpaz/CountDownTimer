package com.example.paxilpaz.countdowntimer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.paxilpaz.countdowntimer.R;
import com.example.paxilpaz.countdowntimer.timer.TimerData;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by paxilpaz on 20/04/16.
 */
public class PeriodTimer extends RelativeLayout implements Observer, View.OnClickListener, View.OnLongClickListener {

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

    //Period ImageViews
    private ImageView tens_minutes;
    private ImageView tens_seconds;
    private ImageView seconds;
    private ImageView minutes;
    private ImageView periodSeparator;

    //variables to store previous state of digits and avoid updating them if not necessary
    private int previousPeriodTensMinutes;
    private int previousPeriodMinutes;
    private int previousPeriodTensSeconds;
    private int previousPeriodSeconds;

    public PeriodTimer(Context context) {
        super(context);
        init();
    }

    public PeriodTimer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PeriodTimer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(this);
        setOnLongClickListener(this);
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.period_timer, this, true);
        //Referencing to the TimerData
        timerData = TimerData.getInstance(getContext());
        timerData.addObserver(this);
        //Get Period ImageViews
        tens_minutes = (ImageView)findViewById(R.id.tens_minutes);
        tens_seconds = (ImageView)findViewById(R.id.tens_seconds_period);
        seconds = (ImageView)findViewById(R.id.seconds_period);
        minutes = (ImageView)findViewById(R.id.minutes);
        periodSeparator = (ImageView)findViewById(R.id.colon);
        //Setting variables to -1 when creating the Activity
        resetVariablesForRefreshes();
    }

    private void resetVariablesForRefreshes() {
        previousPeriodTensMinutes = -1;
        previousPeriodMinutes = -1;
        previousPeriodTensSeconds = -1;
        previousPeriodSeconds = -1;
    }
    @Override
    public void update(Observable observable, Object o) {
        //Just to be sure, we check that the observable is our TimerData
        if (observable instanceof TimerData) {
            //Update the GUI, starting with the period timer
            if (timerData.getPeriodMinutesTens() == Integer.MIN_VALUE) {
                //In this case, period is finished
                tens_minutes.setImageResource(R.drawable.dash);
                minutes.setImageResource(R.drawable.dash);
                periodSeparator.setImageResource(R.drawable.middle_dot);
                tens_seconds.setImageResource(R.drawable.dash);
                seconds.setImageResource(R.drawable.dash);
                periodSeparator.setImageResource(R.drawable.middle_dot);

                resetVariablesForRefreshes();

            } else if (timerData.getPeriodMinutesTens() == -1) {
                //In this case, we are in the last minute

                //refreshing the period timer
                if (previousPeriodTensMinutes != timerData.getPeriodSecondsTens()) {
                    previousPeriodTensMinutes = timerData.getPeriodSecondsTens();
                    tens_minutes.setImageResource(digitsID[previousPeriodTensMinutes]);
                }
                if (previousPeriodMinutes != timerData.getPeriodSeconds()) {
                    previousPeriodMinutes = timerData.getPeriodSeconds();
                    minutes.setImageResource(digitsID[previousPeriodMinutes]);
                }
                if (previousPeriodTensSeconds != timerData.getPeriodSecondsTenths()) {
                    previousPeriodTensSeconds = timerData.getPeriodSecondsTenths();
                    tens_seconds.setImageResource(digitsID[previousPeriodTensSeconds]);
                }
                previousPeriodSeconds = timerData.getPeriodSecondsHundreths();
                seconds.setImageResource(digitsID[previousPeriodSeconds]);

                periodSeparator.setImageResource(R.drawable.dot);

            } else {
                //Normal behaviour
                //refreshing the period timer
                if (previousPeriodTensMinutes != timerData.getPeriodMinutesTens()) {
                    previousPeriodTensMinutes = timerData.getPeriodMinutesTens();
                    tens_minutes.setImageResource(digitsID[previousPeriodTensMinutes]);
                }
                if (previousPeriodMinutes != timerData.getPeriodMinutes()) {
                    previousPeriodMinutes = timerData.getPeriodMinutes();
                    minutes.setImageResource(digitsID[previousPeriodMinutes]);
                }
                if (previousPeriodTensSeconds != timerData.getPeriodSecondsTens()) {
                    previousPeriodTensSeconds = timerData.getPeriodSecondsTens();
                    tens_seconds.setImageResource(digitsID[previousPeriodTensSeconds]);
                }
                if (previousPeriodSeconds != timerData.getPeriodSeconds()) {
                    previousPeriodSeconds = timerData.getPeriodSeconds();
                    seconds.setImageResource(digitsID[previousPeriodSeconds]);
                }

                periodSeparator.setImageResource(R.drawable.colon);

            }
        }
    }

    @Override
    public void onClick(View view) {
        if (timerData.isPaused()) {
            timerData.resumeTimer();
        }
        else {
            timerData.pauseTimer();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        //TODO display a PopupDialog to manually set the PeriodTimer
        return true;
    }
}
