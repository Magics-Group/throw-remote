package io.magics.throwremote.listeners;

import android.app.Activity;
import android.widget.SeekBar;

import com.github.nkzawa.emitter.Emitter;

public class PositionUpdateListener implements Emitter.Listener {

    Activity mActivity;
    Integer mPosition;
    SeekBar mSeekbar;


    public PositionUpdateListener(Activity activity, Integer position, SeekBar seekbar) {
        mActivity = activity;
        mPosition = position;
        mSeekbar = seekbar;
    }

    @Override
    public void call(Object... args) {
        Double percentDecimal = (Double) args[0];
        mPosition = (int) Math.round(percentDecimal * 10000);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSeekbar.setProgress(mPosition);
            }
        });
    }
}
