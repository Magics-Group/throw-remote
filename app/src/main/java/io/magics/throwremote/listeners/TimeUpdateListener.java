package io.magics.throwremote.listeners;

import android.app.Activity;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import java.util.concurrent.TimeUnit;

public class TimeUpdateListener implements Emitter.Listener {

    Activity mActivity;
    Integer mTime;
    TextView mTimeTextView;

    public TimeUpdateListener(Activity activity, Integer time, TextView timeTextView) {
        mActivity = activity;
        mTime = time;
        mTimeTextView = timeTextView;
    }

    @Override
    public void call(Object... args) {
        mTime = (Integer) args[0];
        final String dateFormatted = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mTime),
                TimeUnit.MILLISECONDS.toMinutes(mTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTime)),
                TimeUnit.MILLISECONDS.toSeconds(mTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTime)));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimeTextView.setText(dateFormatted);
            }
        });
    }
}
