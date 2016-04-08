package com.example.paxilpaz.countdowntimer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.ImageView;

/**
 * Created by paxilpaz on 05/04/16.
 */
public class BasketballCountDownTimer {

    //Constants
    private static final long MSEC_IN_SEC = 1000;


    //The instance of TimerData
    private TimerData timerData;

    private static final int FOURTEEN = 14000;

    private static final long MINUTE = 60000;

    //ImageViews to update (Period)
    private ImageView tensOfMinutes, minutes, tensOfSecondsPeriod, secondsPeriod, separatorPeriod;

    //ImageViews to update (Action)
    private ImageView tensOfSecondsAction, secondsAction, separatorAction, tenthsOfSeconds;

    //10 msec = 0.01sec = shortest time unit in Basketball
    private static final long TICK = 10;

    //Messages variables to Start/Stop/Reset/Resume the timer
    private static final int START = 1;
    private static final int PAUSE = 2;
    private static final int RESUME = 3;
    private static final int RESET14 = 4;
    private static final int RESET24 = 5;

    //Needed to configure the proper timers according to the leagues
    private long actionTime;
    private long periodTime;

    //Remaining time for action/period
    private long remainingActionTime;
    private long remainingPeriodTime;

    //Stop time for action/period
    private long stopTimePeriod;
    private long stopTimeAction;

    /**
     * boolean representing if the timer was cancelled
     */
    private boolean mCancelled = false;

    public BasketballCountDownTimer(Context context) {

        timerData = TimerData.getInstance(context);

        actionTime = timerData.getActionTime() * MSEC_IN_SEC;
        periodTime = timerData.getPeriodTime() * MSEC_IN_SEC;
    }

    public void onFinish() {
        timerData.updateData(0,0);
        cancel();
    }


    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(START);
        mHandler.removeMessages(PAUSE);
        mHandler.removeMessages(RESUME);
        mHandler.removeMessages(RESET14);
        mHandler.removeMessages(RESET24);
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
    public synchronized final void pause() {
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        mHandler.removeMessages(START);
        mHandler.removeMessages(RESUME);
        mHandler.removeMessages(RESET14);
        mHandler.removeMessages(RESET24);
        mHandler.sendMessage(mHandler.obtainMessage(PAUSE));
    }

    //Resumes the timer
    public synchronized final void resume() {
        if (periodTime <= 0) {
            onFinish();
            return;
        }
        mHandler.removeMessages(START);
        mHandler.removeMessages(PAUSE);
        mHandler.removeMessages(RESET14);
        mHandler.removeMessages(RESET24);
        mHandler.sendMessage(mHandler.obtainMessage(RESUME));
    }

    //Resets the timer
    public synchronized final void reset14() {
        mHandler.sendMessage(mHandler.obtainMessage(RESET14));
    }

    //Resets the timer
    public synchronized final void reset24() {
        mHandler.sendMessage(mHandler.obtainMessage(RESET24));
    }


    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (BasketballCountDownTimer.this) {

                switch (msg.what) {
                    case PAUSE:
                        remainingActionTime = stopTimeAction - SystemClock.elapsedRealtime();
                        remainingPeriodTime = stopTimePeriod - SystemClock.elapsedRealtime();
                        break;

                    case RESET14:
                        stopTimeAction = SystemClock.elapsedRealtime() + FOURTEEN;
                        remainingActionTime = FOURTEEN;
                        removeMessages(RESET14);
                        break;

                    case RESET24:
                        stopTimeAction = SystemClock.elapsedRealtime() + actionTime;
                        remainingActionTime = actionTime;
                        removeMessages(RESET24);
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

                        if (remainingActionTime <= 0) {
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
                            timerData.updateData(remainingPeriodTime, remainingActionTime);

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
