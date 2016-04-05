package com.example.paxilpaz.countdowntimer;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ImageView;

import java.util.concurrent.TimeUnit;

/**
 * Created by paxilpaz on 05/04/16.
 */
public class BasketballCountDownTimer {

    //Digits
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

    private static final int minutes_seconds_separator = R.drawable.colon;

    private static final int seconds_cents_separator = R.drawable.dot;


    private static final long MINUTE = 60000;

    //ImageViews to update (Period)
    private ImageView tensOfMinutes, minutes, tensOfSecondsPeriod, secondsPeriod, separatorPeriod;

    //ImageViews to update (Action)
    private ImageView tensOfSecondsAction, secondsAction, separatorAction, tenthsOfSeconds;

    //10 msec = 0.01sec = shortest time unit in Basketball
    private static final long TICK = 10;

    //Messages variables to Start/Stop/Reset/Resume the timer
    private static final int START = 1;
    private static final int STOP = 2;
    private static final int RESUME = 3;
    private static final int RESET = 4;

    //Needed to configure the proper timers according to the leagues
    private long actionTime;
    private long periodTime;

    //Remaining time for action/period
    private long remainingActionTime;
    private long remainingPeriodTime;

    //Stop time for action/period
    private long stopTimePeriod;
    private long stopTimeAction;

    //Values to prevent irrelevant refreshes (Period)
    private int previus_tens_of_mins;
    private int previous_mins;
    private int previous_tens_of_secs_period;
    private int previous_secs_period;

    //Values to prevent irrelevant refreshes (Period when counting last minute)
    private int previous_last_minute_tens_of_seconds;
    private int previous_last_minute_seconds;
    private int previous_last_minute_tenths_of_seconds;
    private int previous_last_minute_hundreths_of_seconds;

    //Values to prevent irrelevant refreshes (Action)
    private int previous_tens_of_seconds_action;
    private int previous_seconds_action;
    private int previous_tenths_of_seconds_action;

    private boolean isLastMinute;
    private boolean isSeparatorPeriodChanged = false;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;

    public BasketballCountDownTimer(long actionTime, long periodTime, ImageView views[]) {
        this.actionTime = actionTime;
        this.periodTime = periodTime;

        if (periodTime < MINUTE)
            isLastMinute = true;

        //Get Views for Period timer
        tensOfMinutes = views[0];
        minutes = views[1];
        separatorPeriod = views[2];
        tensOfSecondsPeriod = views[3];
        secondsPeriod = views[4];

        //Get Views for Action timer
        tensOfSecondsAction = views[5];
        secondsAction = views[6];
        separatorAction = views[7];
        tenthsOfSeconds = views[8];

        //Previous values for period timer
        previous_mins = -1;
        previous_secs_period = -1;
        previous_tens_of_secs_period = -1;
        previus_tens_of_mins = -1;

        //Previous values for action timer
        previous_tens_of_seconds_action = -1;
        previous_seconds_action = -1;
        previous_tenths_of_seconds_action = -1;

        //Previous values for period timer when counting last minute
        previous_last_minute_hundreths_of_seconds = -1;
        previous_last_minute_seconds = -1;
        previous_last_minute_tens_of_seconds = -1;
        previous_last_minute_tenths_of_seconds = -1;

    }

    public void onTick() {

        //evaluate if last action
        if ( isLastMinute ) {

            //last action: clear action timer
            if (remainingPeriodTime < remainingActionTime) {
                secondsAction.setImageResource(0);
                tensOfSecondsAction.setImageResource(0);
                tenthsOfSeconds.setImageResource(0);
                separatorAction.setImageResource(0);
            } else {
                actionTimerUpdate();
            }

            //change separator the first time
            if (!isSeparatorPeriodChanged) {
                isSeparatorPeriodChanged = true;
                separatorPeriod.setImageResource(R.drawable.dot);
            }


            //switch period timer to hundreths of seconds
            //Handling period timer
            int tens_of_seconds_last_minute = (int) ((TimeUnit.MILLISECONDS.toSeconds(remainingPeriodTime)) / 10);
            int seconds_last_minute = (int) ((TimeUnit.MILLISECONDS.toSeconds(remainingPeriodTime)) % 10);
            int tenths_of_seconds_last_minute = (int) ((remainingPeriodTime % 1000) / 100);
            int hundreths_of_seconds_last_minute = (int) ((remainingPeriodTime % 100) / 10 );

            if (previous_last_minute_tens_of_seconds != tens_of_seconds_last_minute) {
                tensOfMinutes.setImageResource(digitsID[tens_of_seconds_last_minute]);
                previous_last_minute_tens_of_seconds = tens_of_seconds_last_minute;
            }

            if (previous_last_minute_seconds != seconds_last_minute) {
                minutes.setImageResource(digitsID[seconds_last_minute]);
                previous_last_minute_seconds = seconds_last_minute;
            }

            if (previous_last_minute_tenths_of_seconds != tenths_of_seconds_last_minute) {
                tensOfSecondsPeriod.setImageResource(digitsID[tenths_of_seconds_last_minute]);
                previous_last_minute_tenths_of_seconds = tenths_of_seconds_last_minute;
            }

            if (previous_last_minute_hundreths_of_seconds != hundreths_of_seconds_last_minute) {
                secondsPeriod.setImageResource(digitsID[hundreths_of_seconds_last_minute]);
                previous_last_minute_hundreths_of_seconds = hundreths_of_seconds_last_minute;
            }

        } else {
            //Handling period timer
            int tens_of_minutes_to_fininsh = (int) (TimeUnit.MILLISECONDS.toMinutes(remainingPeriodTime) / 10);
            int minutes_to_finish = (int) (TimeUnit.MILLISECONDS.toMinutes(remainingPeriodTime) % 10);
            int tens_of_seconds_to_finish = (int) ((TimeUnit.MILLISECONDS.toSeconds(remainingPeriodTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingPeriodTime))) / 10);
            int seconds_to_finish = (int) ((TimeUnit.MILLISECONDS.toSeconds(remainingPeriodTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingPeriodTime))) % 10);

            if ( tens_of_minutes_to_fininsh != previus_tens_of_mins) {
                tensOfMinutes.setImageResource(digitsID[tens_of_minutes_to_fininsh]);
                previus_tens_of_mins = tens_of_minutes_to_fininsh;
            }

            if ( minutes_to_finish != previous_mins) {
                minutes.setImageResource(digitsID[minutes_to_finish]);
                previous_mins = minutes_to_finish;
            }

            if ( tens_of_seconds_to_finish != previous_tens_of_secs_period) {
                tensOfSecondsPeriod.setImageResource(digitsID[tens_of_seconds_to_finish]);
                previous_tens_of_secs_period = tens_of_seconds_to_finish;
            }

            if (seconds_to_finish != previous_secs_period) {
                secondsPeriod.setImageResource(digitsID[seconds_to_finish]);
                previous_secs_period = seconds_to_finish;
            }

            actionTimerUpdate();

        }


    }

    private void actionTimerUpdate() {
        //Handling action timer
        int tens_of_seconds_action = (int) ((TimeUnit.MILLISECONDS.toSeconds(remainingActionTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingActionTime))) / 10);
        int seconds_action = (int) ((TimeUnit.MILLISECONDS.toSeconds(remainingActionTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingActionTime))) % 10);
        int tenths_of_seconds_action = (int) ((remainingActionTime - tens_of_seconds_action * 10000 - seconds_action * 1000) / 100);

        if (previous_tens_of_seconds_action != tens_of_seconds_action) {
            tensOfSecondsAction.setImageResource(digitsID[tens_of_seconds_action]);
            previous_tens_of_seconds_action = tens_of_seconds_action;
        }

        if (previous_seconds_action != seconds_action) {
            secondsAction.setImageResource(digitsID[seconds_action]);
            previous_seconds_action = seconds_action;
        }

        if (previous_tenths_of_seconds_action != tenths_of_seconds_action) {
            tenthsOfSeconds.setImageResource(digitsID[tenths_of_seconds_action]);
            previous_tenths_of_seconds_action = tenths_of_seconds_action;
        }
    }

    public void onFinish() {

    }


    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(START);
        mHandler.removeMessages(STOP);
        mHandler.removeMessages(RESUME);
        mHandler.removeMessages(RESET);
    }

    /**
     * Start the countdown.
     */
    public synchronized final void start() {
        mCancelled = false;
        //If periodTime is less or equal to 0, then no point in starting the timer
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        //Compute Stop time for both action and period timers
        stopTimePeriod = SystemClock.elapsedRealtime() + periodTime;
        stopTimeAction = SystemClock.elapsedRealtime() + actionTime;
        mHandler.sendMessage(mHandler.obtainMessage(START));
    }

    //Stops the timer
    public synchronized final void stop() {
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        mHandler.removeMessages(START);
        mHandler.removeMessages(RESUME);
        mHandler.removeMessages(RESET);
        mHandler.sendMessage(mHandler.obtainMessage(STOP));
    }

    //Resumes the timer
    public synchronized final void resume() {
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        mHandler.removeMessages(START);
        mHandler.removeMessages(STOP);
        mHandler.removeMessages(RESET);
        mHandler.sendMessage(mHandler.obtainMessage(RESUME));
    }

    //Resets the timer
    public synchronized final void reset() {
        mHandler.sendMessage(mHandler.obtainMessage(RESET));
    }


    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (BasketballCountDownTimer.this) {

                switch (msg.what) {
                    case STOP:
                        remainingActionTime = stopTimeAction - SystemClock.elapsedRealtime();
                        remainingPeriodTime = stopTimePeriod - SystemClock.elapsedRealtime();
                        break;

                    case RESET:
                        stopTimeAction = SystemClock.elapsedRealtime() + actionTime;
                        remainingActionTime = actionTime;
                        this.removeMessages(RESET);
                        break;

                    case RESUME:
                        stopTimeAction = SystemClock.elapsedRealtime() + remainingActionTime;
                        stopTimePeriod = SystemClock.elapsedRealtime() + remainingPeriodTime;

                    case START:
                        if (mCancelled) {
                           return;
                        }

                        remainingPeriodTime = stopTimePeriod - SystemClock.elapsedRealtime();
                        remainingActionTime = stopTimeAction - SystemClock.elapsedRealtime();

                        if (remainingPeriodTime < MINUTE) {
                            isLastMinute = true;
                        }

                        //countdown finished
                        if (remainingPeriodTime <= 0) {
                            onFinish();
                        } else if (remainingPeriodTime < TICK) {
                            // no tick, just delay until done
                            sendMessageDelayed(obtainMessage(START), remainingPeriodTime);
                        } else {
                            long lastTickStart = SystemClock.elapsedRealtime();
                            onTick();

                            // take into account user's onTick taking time to execute
                            long delay = lastTickStart + TICK - SystemClock.elapsedRealtime();

                            // special case: user's onTick took more than interval to
                            // complete, skip to next interval
                            while (delay < 0) delay += TICK;

                            sendMessageDelayed(obtainMessage(START), delay);
                        }
                        break;
                    default:
                        break;
                }


            }
        }
    };





}
