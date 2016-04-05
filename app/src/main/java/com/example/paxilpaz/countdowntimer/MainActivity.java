package com.example.paxilpaz.countdowntimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //private CountDownTimer quarterCountdown;

    private BasketballCountDownTimer countDownTimer;
    private Button start;
    private Button reset;
    private Button pause_resume;

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

        start = (Button)findViewById(R.id.start);
        reset = (Button)findViewById(R.id.reset);
        pause_resume = (Button)findViewById(R.id.pause_resume);

        tens_minutes = (ImageView)findViewById(R.id.tens_minutes);
        tens_seconds = (ImageView)findViewById(R.id.tens_seconds_period);
        seconds = (ImageView)findViewById(R.id.seconds_period);
        minutes = (ImageView)findViewById(R.id.minutes);
        periodSeparator = (ImageView)findViewById(R.id.colon);

        tens_seconds_action = (ImageView)findViewById(R.id.tens_seconds_action);
        seconds_action = (ImageView)findViewById(R.id.seconds_action);
        actionSeparator = (ImageView)findViewById(R.id.dot);
        tenths_of_seconds_action = (ImageView)findViewById(R.id.tenth_of_seconds);


        start.setOnClickListener(this);
        reset.setOnClickListener(this);
        pause_resume.setOnClickListener(this);
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
            case R.id.start:
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
                reset.setEnabled(true);
                pause_resume.setEnabled(true);
                pause_resume.setText("Pause");
                break;
            case  R.id.reset:
                countDownTimer.reset();
                break;
            case  R.id.pause_resume:
                if ( pause_resume.getText().equals("Pause") ) {
                    countDownTimer.stop();
                    pause_resume.setText("Resume");
                } else {
                    countDownTimer.resume();
                    pause_resume.setText("Pause");
                }
                break;
            default:
                break;
        }
    }
}
