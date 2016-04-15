package com.example.paxilpaz.countdowntimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Observer {

    private BasketballCountDownTimer countDownTimer;
    private TimerData timerData;
    private ImageButton reset14;
    private ImageButton stop;
    private ImageButton reset24;
    private ImageButton start_pause_resume;

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

    //Period ImageViews
    private ImageView tens_minutes;
    private ImageView tens_seconds;
    private ImageView seconds;
    private ImageView minutes;
    private ImageView periodSeparator;

    //Action ImageViews
    private ImageView tens_seconds_action;
    private ImageView seconds_action;
    private ImageView actionSeparator;
    private ImageView tenths_of_seconds_action;

    //variables to store previous state of digits and avoid updating them if not necessary
    private int previousPeriodTensMinutes;
    private int previousPeriodMinutes;
    private int previousPeriodTensSeconds;
    private int previousPeriodSeconds;

    private int previousActionTensSeconds;
    private int previousActionSeconds;
    private int previousActionTenthsSeconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        //Get Period ImageViews
        tens_minutes = (ImageView)findViewById(R.id.tens_minutes);
        tens_seconds = (ImageView)findViewById(R.id.tens_seconds_period);
        seconds = (ImageView)findViewById(R.id.seconds_period);
        minutes = (ImageView)findViewById(R.id.minutes);
        periodSeparator = (ImageView)findViewById(R.id.colon);
        //Get Action ImageViews
        tens_seconds_action = (ImageView)findViewById(R.id.tens_seconds_action);
        seconds_action = (ImageView)findViewById(R.id.seconds_action);
        actionSeparator = (ImageView)findViewById(R.id.dot);
        tenths_of_seconds_action = (ImageView)findViewById(R.id.tenth_of_seconds);
        //Setting variables to -1 when creating the Activity
        resetVariablesForRefreshes();

        //Retrieve preferences and set them according to the default or previously selected values
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String duration_from_preferences = preferences.getString(getString(R.string.preference_period_duration_of_periods),
                getString(R.string.preference_period_duration_default_setting));
        String[] splittedDuration = duration_from_preferences.split(":");

        int tens_of_mins_starting_app = Integer.parseInt(splittedDuration[0]) / 10;
        int mins_starting_app = Integer.parseInt(splittedDuration[0]) % 10;
        int tens_of_secs_starting_app = Integer.parseInt(splittedDuration[1]) / 10;
        int secs_starting_app = Integer.parseInt(splittedDuration[1]) % 10;

        String shot_clock_duration_string = preferences.getString(getString(R.string.preference_shot_clock_duration),
                getString(R.string.preference_shot_clock_duration_default_setting));
        int shot_clock_duration = Integer.parseInt(shot_clock_duration_string);

        tens_minutes.setImageResource(digitsID[tens_of_mins_starting_app]);
        minutes.setImageResource(digitsID[mins_starting_app]);
        tens_seconds.setImageResource(digitsID[tens_of_secs_starting_app]);
        seconds.setImageResource(digitsID[secs_starting_app]);

        tens_seconds_action.setImageResource(digitsID[shot_clock_duration / 10]);
        seconds_action.setImageResource(digitsID[shot_clock_duration % 10]);


        //Apply double and single click listener to the shotclock
        RelativeLayout shot_clock_layout = (RelativeLayout)findViewById(R.id.action_timer);
        shot_clock_layout.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View view) {
                countDownTimer.reset14();
                start_pause_resume.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));
                start_pause_resume.setEnabled(true);
                if (start_pause_resume.getId() == R.id.resume) {
                    //Timer was paused... We will update the action timer in order to show 14,0
                    resetActionTimerToSeconds(1, 4);
                }
            }

            @Override
            public void onSingleClick(View view) {
                countDownTimer.reset24();
                start_pause_resume.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));
                start_pause_resume.setEnabled(true);
                if (start_pause_resume.getId() == R.id.resume) {
                    //Timer was paused... We will update the action timer in order to show 24,0
                    resetActionTimerToSeconds(2, 4);
                }
            }
        });
        shot_clock_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getApplicationContext(),"Long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), TimerSettings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_pause_resume_button:
                //Getting TimerData and adding this activity as Observer
                timerData = TimerData.getInstance(this);
                timerData.addObserver(this);

                countDownTimer = new BasketballCountDownTimer(this);
                countDownTimer.start();
                //handle buttons
                start_pause_resume.setImageResource(R.drawable.pause);
                start_pause_resume.setId(R.id.pause);

                reset14.setEnabled(true);
                reset14.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));

                reset24.setEnabled(true);
                reset24.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));

                stop.setEnabled(true);
                stop.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black));
                break;
            case R.id.pause:
                start_pause_resume.setId(R.id.resume);
                start_pause_resume.setImageResource(R.drawable.start);
                countDownTimer.pause();
                break;
            case R.id.resume:
                start_pause_resume.setId(R.id.pause);
                start_pause_resume.setImageResource(R.drawable.pause);
                countDownTimer.resume();
                break;
            case  R.id.reset_14_button:
                countDownTimer.reset14();
                start_pause_resume.setColorFilter(ContextCompat.getColor(this, R.color.black));
                start_pause_resume.setEnabled(true);
                if (start_pause_resume.getId() == R.id.resume) {
                    //Timer was paused... We will update the action timer in order to show 14,0
                    resetActionTimerToSeconds(1, 4);
                }
                break;
            case  R.id.reset_24_button:
                countDownTimer.reset24();
                start_pause_resume.setColorFilter(ContextCompat.getColor(this, R.color.black));
                start_pause_resume.setEnabled(true);
                if (start_pause_resume.getId() == R.id.resume) {
                    //Timer was paused... We will update the action timer in order to show 24,0
                    resetActionTimerToSeconds(2, 4);
                }
                break;
            case  R.id.stop_button:
                resetTimerAndButtons();
                break;
            default:
                break;
        }
    }

    private void resetActionTimerToSeconds(int tensOfSecs, int secs) {
        tens_seconds_action.setImageResource(digitsID[tensOfSecs]);
        seconds_action.setImageResource(digitsID[secs]);
        tenths_of_seconds_action.setImageResource(digitsID[0]);
        actionSeparator.setImageResource(R.drawable.dot);
        previousActionTensSeconds = tensOfSecs;
        previousActionSeconds = secs;
        previousActionTenthsSeconds = 0;
    }

    @Override
    public void update(Observable observable, Object data) {
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

                tens_seconds_action.setImageResource(R.drawable.dash);
                seconds_action.setImageResource(R.drawable.dash);
                actionSeparator.setImageResource(R.drawable.middle_dot);
                tenths_of_seconds_action.setImageResource(R.drawable.dash);

                actionSeparator.setImageResource(R.drawable.middle_dot);
                periodSeparator.setImageResource(R.drawable.middle_dot);

                resetVariablesForRefreshes();

                resetTimerAndButtons();

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
                        actionFinishedRefactorPauseButton();
                    } else {
                        if (previousActionTensSeconds != timerData.getActionSecondsTens()) {
                            previousActionTensSeconds = timerData.getActionSecondsTens();
                            tens_seconds_action.setImageResource(digitsID[previousActionTensSeconds]);
                        }
                        if (previousActionSeconds != timerData.getActionSeconds()) {
                            previousActionSeconds = timerData.getActionSeconds();
                            seconds_action.setImageResource(digitsID[previousActionSeconds]);
                        }
                        if (previousActionTenthsSeconds != timerData.getActionSecondsTenths()) {
                            previousActionTenthsSeconds = timerData.getActionSecondsTenths();
                            tenths_of_seconds_action.setImageResource(digitsID[previousActionTenthsSeconds]);
                        }
                        actionSeparator.setImageResource(R.drawable.dot);
                    }
                }
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
                        actionFinishedRefactorPauseButton();
                    } else {
                        if (previousActionTensSeconds != timerData.getActionSecondsTens()) {
                            previousActionTensSeconds = timerData.getActionSecondsTens();
                            tens_seconds_action.setImageResource(digitsID[previousActionTensSeconds]);
                        }
                        if (previousActionSeconds != timerData.getActionSeconds()) {
                            previousActionSeconds = timerData.getActionSeconds();
                            seconds_action.setImageResource(digitsID[previousActionSeconds]);
                        }
                        if (previousActionTenthsSeconds != timerData.getActionSecondsTenths()) {
                            previousActionTenthsSeconds = timerData.getActionSecondsTenths();
                            tenths_of_seconds_action.setImageResource(digitsID[previousActionTenthsSeconds]);
                        }
                        actionSeparator.setImageResource(R.drawable.dot);
                    }

                }
            }
        }
    }

    private void resetTimerAndButtons() {
        //stop countdown
        countDownTimer.cancel();
        countDownTimer = null;

        //reset buttons
        start_pause_resume.setId(R.id.start_pause_resume_button);
        start_pause_resume.setImageResource(R.drawable.start);

        stop.setEnabled(false);
        stop.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));

        reset14.setEnabled(false);
        reset14.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));

        reset24.setEnabled(false);
        reset24.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
    }

    private void resetVariablesForRefreshes() {
        previousPeriodTensMinutes = -1;
        previousPeriodMinutes = -1;
        previousPeriodTensSeconds = -1;
        previousPeriodSeconds = -1;
        previousActionTensSeconds = -1;
        previousActionSeconds = -1;
        previousActionTenthsSeconds = -1;
    }

    private void actionFinishedRefactorPauseButton() {
        start_pause_resume.setId(R.id.resume);
        start_pause_resume.setImageResource(R.drawable.start);
        start_pause_resume.setEnabled(false);
        start_pause_resume.setColorFilter(ContextCompat.getColor(this, R.color.dark_grey));
    }

    private void dashAction() {
        tens_seconds_action.setImageResource(R.drawable.dash);
        seconds_action.setImageResource(R.drawable.dash);
        tenths_of_seconds_action.setImageResource(R.drawable.dash);
        actionSeparator.setImageResource(R.drawable.middle_dot);

        previousActionTensSeconds = -1;
        previousActionTenthsSeconds = -1;
        previousActionSeconds = -1;
    }
}
