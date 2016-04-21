package com.example.paxilpaz.countdowntimer.timer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by paxilpaz on 05/04/16.
 */
public class BasketballCountDownTimer {

    //Constants
    private static final long MSEC_IN_SEC = 1000;
    
    //The instance of TimerData
    private TimerData timerData;

    //10 msec = 0.01sec = shortest time unit in Basketball
    private static final long TICK = 10;

    //Messages variables to Start/Stop/Reset/Resume the timer
    private static final int START = 1;
    private static final int PAUSE = 2;
    private static final int RESUME = 3;
    private static final int RESET_OFFENSIVE_REBOUND = 4;
    private static final int RESET_ACTION = 5;

    //Needed to configure the proper timers according to the leagues
    private long shotClockTime;
    private long shotClockOffensiveReboundTime;
    private long periodTime;

    //Remaining time for action/period
    private long remainingShotClockTime;
    private long remainingPeriodTime;

    //Stop time for action/period
    private long stopTimePeriod;
    private long stopTimeShotClock;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;
    private boolean isPaused = false;

    public BasketballCountDownTimer(Context context) {

        timerData = TimerData.getInstance(context);

        shotClockTime = timerData.getShotClockTime() * MSEC_IN_SEC;
        shotClockOffensiveReboundTime = timerData.getShotClockTimeOffensiveRebound() * MSEC_IN_SEC;
        periodTime = timerData.getPeriodTime() * MSEC_IN_SEC;
    }

    public void onFinish() {
        isPaused = true;
        timerData.updateData(0,0);
        cancel();
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mCancelled = true;
        isPaused = true;
        mHandler.removeMessages(START);
        mHandler.removeMessages(PAUSE);
        mHandler.removeMessages(RESUME);
        mHandler.removeMessages(RESET_OFFENSIVE_REBOUND);
        mHandler.removeMessages(RESET_ACTION);
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
        isPaused = false;
        //Compute Stop time for both action and period timers
        stopTimePeriod = SystemClock.elapsedRealtime() + periodTime;
        stopTimeShotClock = SystemClock.elapsedRealtime() + shotClockTime;
        mHandler.sendMessage(mHandler.obtainMessage(START));
    }

    //Stops the timer
    public synchronized final void pause() {
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        isPaused = true;
        mHandler.removeMessages(START);
        mHandler.removeMessages(RESUME);
        mHandler.removeMessages(RESET_OFFENSIVE_REBOUND);
        mHandler.removeMessages(RESET_ACTION);
        mHandler.sendMessage(mHandler.obtainMessage(PAUSE));
    }

    //Resumes the timer
    public synchronized final void resume() {
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        isPaused = false;
        mHandler.removeMessages(START);
        mHandler.removeMessages(PAUSE);
        mHandler.removeMessages(RESET_OFFENSIVE_REBOUND);
        mHandler.removeMessages(RESET_ACTION);
        mHandler.sendMessage(mHandler.obtainMessage(RESUME));
    }

    //Resets the timer
    public synchronized final void reset_offensive_rebound() {
        mHandler.sendMessage(mHandler.obtainMessage(RESET_OFFENSIVE_REBOUND));
    }

    //Resets the timer
    public synchronized final void reset_shot_clock() {
        mHandler.sendMessage(mHandler.obtainMessage(RESET_ACTION));
    }


    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (BasketballCountDownTimer.this) {

                switch (msg.what) {
                    case PAUSE:
                        remainingShotClockTime = stopTimeShotClock - SystemClock.elapsedRealtime();
                        remainingPeriodTime = stopTimePeriod - SystemClock.elapsedRealtime();

                        break;

                    case RESET_OFFENSIVE_REBOUND:
                        stopTimeShotClock = SystemClock.elapsedRealtime() + shotClockOffensiveReboundTime;
                        remainingShotClockTime = shotClockOffensiveReboundTime;
                        removeMessages(RESET_OFFENSIVE_REBOUND);
                        break;

                    case RESET_ACTION:
                        stopTimeShotClock = SystemClock.elapsedRealtime() + shotClockTime;
                        remainingShotClockTime = shotClockTime;
                        removeMessages(RESET_ACTION);
                        break;

                    case RESUME:
                        stopTimeShotClock = SystemClock.elapsedRealtime() + remainingShotClockTime;
                        stopTimePeriod = SystemClock.elapsedRealtime() + remainingPeriodTime;

                    case START:
                        if (mCancelled) {
                           return;
                        }

                        remainingPeriodTime = stopTimePeriod - SystemClock.elapsedRealtime();
                        remainingShotClockTime = stopTimeShotClock - SystemClock.elapsedRealtime();

                        if (remainingShotClockTime <= 0) {
                            pause();
                            break;
                        }

                        //countdown finished
                        if (remainingPeriodTime <= 0) {
                            onFinish();
                        } else if (remainingPeriodTime < TICK) {
                            // no tick, just delay until done
                            sendMessageDelayed(obtainMessage(START), remainingPeriodTime);
                        } else {
                            long lastTickStart = SystemClock.elapsedRealtime();
                            timerData.updateData(remainingPeriodTime, remainingShotClockTime);

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

    public synchronized boolean isPaused() {
        return isPaused;
    }
}
