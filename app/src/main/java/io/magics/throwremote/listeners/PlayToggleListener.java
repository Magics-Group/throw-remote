package io.magics.throwremote.listeners;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;

import com.github.nkzawa.emitter.Emitter;

import io.magics.throwremote.*;

public class PlayToggleListener implements Emitter.Listener{

     Activity mActivity;
     Boolean mPlaying;
     ImageButton mPlayImageButton;

    public PlayToggleListener(Activity activity, Boolean playing, ImageButton playImageButton) {
        mActivity = activity;
        mPlaying = playing;
        mPlayImageButton = playImageButton;
    }

    @Override
    public void call (Object... args) {
        mPlaying = (Boolean) args[0];
        Log.i("playing", mPlaying.toString());
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPlaying) {
                    mPlayImageButton.setBackgroundResource(R.drawable.ic_pause_circle_outline_64dp);
                } else {
                    mPlayImageButton.setBackgroundResource(R.drawable.ic_play_circle_outline_64dp);
                }
            }
        });
    }
}