package io.magics.throwremote.listeners;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import java.util.concurrent.TimeUnit;

public class LengthUpdateListener implements Emitter.Listener {

    Activity mActivity;
    Integer mLength;
    TextView mLengthTextView;

    public LengthUpdateListener(Activity activity, Integer length, TextView lengthTextView) {
        mActivity = activity;
        mLength = length;
        mLengthTextView = lengthTextView;
    }

    @Override
    public void call(Object... args) {
        mLength = (Integer) args[0];
        Log.i("length", mLength.toString());
        final String dateFormatted = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mLength),
                TimeUnit.MILLISECONDS.toMinutes(mLength) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mLength)),
                TimeUnit.MILLISECONDS.toSeconds(mLength) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mLength)));
        Log.i("length", dateFormatted);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLengthTextView.setText(dateFormatted);
            }
        });
    }
}
