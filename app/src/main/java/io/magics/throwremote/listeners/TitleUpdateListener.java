package io.magics.throwremote.listeners;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

public class TitleUpdateListener implements Emitter.Listener {

    Activity mActivity;
    String mTitle;
    TextView mTitleTextView;

    public TitleUpdateListener(Activity activity, String title, TextView titleTextView) {
        mActivity = activity;
        mTitle = title;
        mTitleTextView = titleTextView;
    }

    @Override
    public void call(Object ... args) {
        mTitle = (String) args[0];
        Log.i("title", mTitle);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitleTextView.setText(mTitle);
            }
        });
    }
}
