package com.example.paxilpaz.countdowntimer;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //private CountDownTimer quarterCountdown;

    private BasketballCountDownTimer countDownTimer;
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
    private ImageView tens_minutes, tens_seconds, seconds, minutes, periodSeparator;

    //Action ImageViews
    private ImageView tens_seconds_action, seconds_action, actionSeparator, tenths_of_seconds_action;

    private int previus_tens_of_mins, previous_mins, previous_tens_of_secs_perios, previous_secs_period;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        start_pause_resume = (ImageButton)findViewById(R.id.start_pause_resume_button);
        reset14 = (ImageButton)findViewById(R.id.reset_14_button);
        reset24 = (ImageButton)findViewById(R.id.reset_24_button);
        stop = (ImageButton)findViewById(R.id.stop_button);

        tens_minutes = (ImageView)findViewById(R.id.tens_minutes);
        tens_seconds = (ImageView)findViewById(R.id.tens_seconds_period);
        seconds = (ImageView)findViewById(R.id.seconds_period);
        minutes = (ImageView)findViewById(R.id.minutes);
        periodSeparator = (ImageView)findViewById(R.id.colon);

        tens_seconds_action = (ImageView)findViewById(R.id.tens_seconds_action);
        seconds_action = (ImageView)findViewById(R.id.seconds_action);
        actionSeparator = (ImageView)findViewById(R.id.dot);
        tenths_of_seconds_action = (ImageView)findViewById(R.id.tenth_of_seconds);


        start_pause_resume.setOnClickListener(this);
        reset14.setOnClickListener(this);
        reset24.setOnClickListener(this);
        stop.setOnClickListener(this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_pause_resume_button:
                /*previus_tens_of_mins = 10;
                previous_mins = 0;
                previous_tens_of_secs= 0;
                previous_secs = 0;

                quarterCountdown = new CountDownTimer(600000, 1000) {



                    @Override
                    public void onTick(long millisUntilFinished) {
                        int tens_of_minutes_to_fininsh = (int) (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) / 10);
                        int minutes_to_finish = (int) (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 10);
                        int tens_of_seconds_to_finish = (int) ((TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) / 10);
                        int seconds_to_finish = (int) ((TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) % 10);

                        if ( tens_of_minutes_to_fininsh != previus_tens_of_mins) {
                            tens_minutes.setImageResource(digitsID[tens_of_minutes_to_fininsh]);
                            previus_tens_of_mins = tens_of_minutes_to_fininsh;
                        }

                        if ( minutes_to_finish != previous_mins) {
                            minutes.setImageResource(digitsID[minutes_to_finish]);
                            previous_mins = minutes_to_finish;
                        }

                        if ( tens_of_seconds_to_finish != previous_tens_of_secs) {
                            tens_seconds.setImageResource(digitsID[tens_of_seconds_to_finish]);
                            previous_tens_of_secs = tens_of_seconds_to_finish;
                        }

                        if (seconds_to_finish != previous_secs) {
                            seconds.setImageResource(digitsID[seconds_to_finish]);
                            previous_secs = seconds_to_finish;
                        }


                    }

                    @Override
                    public void onFinish() {
                        minutes.setImageResource(digitsID[0]);
                        seconds.setImageResource(digitsID[0]);
                        tens_seconds.setImageResource(digitsID[0]);
                        tens_minutes.setImageResource(digitsID[0]);
                    }
                }.start();*/
                countDownTimer = new BasketballCountDownTimer(24000, 30000, new ImageView[] {
                        tens_minutes, minutes, periodSeparator, tens_seconds, seconds, tens_seconds_action,
                        seconds_action, actionSeparator, tenths_of_seconds_action
                });
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
                break;
            case R.id.resume:
                start_pause_resume.setId(R.id.pause);
                start_pause_resume.setImageResource(R.drawable.pause);
                break;
            case  R.id.reset_14_button:
                countDownTimer.reset14();
                break;
            case  R.id.reset_24_button:
                countDownTimer.reset24();
                break;
            case  R.id.stop_button:
                //stop countdown

                //reset views

                //reset buttons
                start_pause_resume.setId(R.id.start_pause_resume_button);
                start_pause_resume.setImageResource(R.drawable.start);

                stop.setEnabled(false);
                stop.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));

                reset14.setEnabled(false);
                reset14.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));

                reset24.setEnabled(false);
                reset24.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
                break;
            default:
                break;
        }
    }
}
