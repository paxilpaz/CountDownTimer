package com.example.paxilpaz.countdowntimer.views.listeners;

import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.View;

/**
 * Created by paxilpaz on 15/04/16.
 */
public abstract class DoubleClickListener implements View.OnClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = 300;

    private long lastClickTime = 0;

    @Override
    public void onClick(View view) {
        long clickTime = SystemClock.currentThreadTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(view);
        } else {
            onSingleClick(view);
        }
        lastClickTime = clickTime;
    }

    public abstract void onDoubleClick(View view);
    public abstract void onSingleClick(View view);
}
