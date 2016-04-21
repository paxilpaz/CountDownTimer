package com.example.paxilpaz.countdowntimer.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.paxilpaz.countdowntimer.R;
import com.example.paxilpaz.countdowntimer.timer.TimerData;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by paxilpaz on 21/04/16.
 */
public class TimerButtonsLayout extends LinearLayout implements View.OnClickListener, Observer {

    private LayoutInflater layoutInflater;

    private TimerData timerData;

    //ImageButtons
    private ImageButton reset14;
    private ImageButton stop;
    private ImageButton reset24;
    private ImageButton start_pause_resume;

    public TimerButtonsLayout(Context context) {
        super(context);
        init();
    }

    public TimerButtonsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimerButtonsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(this);
        //Layout inflating
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.timer_buttons, this, true);
        //Timer Data
        timerData = TimerData.getInstance(getContext());
        timerData.addObserver(this);

        //Get buttons
        start_pause_resume = (ImageButton)findViewById(R.id.start_pause_resume_button);
        reset14 = (ImageButton)findViewById(R.id.reset_14_button);
        reset24 = (ImageButton)findViewById(R.id.reset_24_button);
        stop = (ImageButton)findViewById(R.id.stop_button);
        //Add listeners to the buttons
        start_pause_resume.setOnClickListener(this);
        reset14.setOnClickListener(this);
        reset24.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_pause_resume_button:
                //Getting TimerData and adding this activity as Observer
                timerData.startTimer();

                //handle buttons
                start_pause_resume.setImageResource(R.drawable.pause);
                start_pause_resume.setId(R.id.pause);

                reset14.setEnabled(true);
                reset14.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

                reset24.setEnabled(true);
                reset24.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));

                stop.setEnabled(true);
                stop.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                break;
            case R.id.pause:
                start_pause_resume.setId(R.id.resume);
                start_pause_resume.setImageResource(R.drawable.start);
                //countDownTimer.pause();
                timerData.pauseTimer();
                break;
            case R.id.resume:
                start_pause_resume.setId(R.id.pause);
                start_pause_resume.setImageResource(R.drawable.pause);
                //countDownTimer.resume();
                timerData.resumeTimer();
                break;
            case  R.id.reset_14_button:
                //countDownTimer.reset_offensive_rebound();
                timerData.resetOffensiveRebound();
                start_pause_resume.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                start_pause_resume.setEnabled(true);
                break;
            case  R.id.reset_24_button:
                //countDownTimer.reset_shot_clock();
                timerData.resetShotClock();
                start_pause_resume.setColorFilter(ContextCompat.getColor(getContext(), R.color.black));
                start_pause_resume.setEnabled(true);
                break;
            case  R.id.stop_button:
                //stop countdown
                timerData.cancelTimer();
                resetTimerAndButtons();
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        //Just to be sure, we check that the observable is our TimerData
        if (observable instanceof TimerData) {
            //Update the GUI, starting with the period timer
            if (timerData.getPeriodMinutesTens() == Integer.MIN_VALUE) {
                resetTimerAndButtons();

            } else if (timerData.getPeriodMinutesTens() == -1) {
                //In this case, we are in the last minute

                //refreshing the action timer
                if (timerData.getActionSecondsTens() == -1) {
                    //Action time is more than peridd time, display dashes and reset values of variables
                } else {
                    if (timerData.getActionSecondsTens() == 0 &&
                            timerData.getActionSeconds() == 0 &&
                            timerData.getActionSecondsTenths() == 0) {
                        //Action finished. Display dashes, refactor pause button
                        actionFinishedRefactorPauseButton();
                    }
                }
            } else {
                //Normal behaviour

                //refreshing the action timer
                if (timerData.getActionSecondsTens() == -1) {
                    //Action is finished, display dashes and reset values of variables
                } else {
                    if (timerData.getActionSecondsTens() == 0 &&
                            timerData.getActionSeconds() == 0 &&
                            timerData.getActionSecondsTenths() == 0) {
                        //Action finished. Display dashes, refactor pause button
                        actionFinishedRefactorPauseButton();
                    }
                }
            }
        }
    }

    private void resetTimerAndButtons() {

        //reset buttons
        start_pause_resume.setId(R.id.start_pause_resume_button);
        start_pause_resume.setImageResource(R.drawable.start);

        stop.setEnabled(false);
        stop.setColorFilter(ContextCompat.getColor(getContext(), R.color.dark_grey));

        reset14.setEnabled(false);
        reset14.setColorFilter(ContextCompat.getColor(getContext(), R.color.dark_grey));

        reset24.setEnabled(false);
        reset24.setColorFilter(ContextCompat.getColor(getContext(), R.color.dark_grey));
    }

    private void actionFinishedRefactorPauseButton() {
        start_pause_resume.setId(R.id.resume);
        start_pause_resume.setImageResource(R.drawable.start);
        start_pause_resume.setEnabled(false);
        start_pause_resume.setColorFilter(ContextCompat.getColor(getContext(), R.color.dark_grey));
    }
}
