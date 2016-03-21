package io.magics.throwremote.listeners;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import com.github.nkzawa.emitter.Emitter;

import io.magics.throwremote.R;

public class MuteToggleListener implements Emitter.Listener {

    Activity mActivity;
    Boolean mMuted;
    ImageButton mMuteImageButton;

    public MuteToggleListener(Activity activity, Boolean muted, ImageButton muteImageButton) {
        mActivity = activity;
        mMuted = muted;
        mMuteImageButton = muteImageButton;
    }

    @Override
    public void call (Object... args) {
        mMuted = (Boolean) args[0];
        Log.i("muted", mMuted.toString());
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMuted) {
                    mMuteImageButton.setBackgroundResource(R.drawable.ic_volume_off_64dp);
                } else {
                    mMuteImageButton.setBackgroundResource(R.drawable.ic_volume_up_64dp);
                }
            }
        });
    }
}
