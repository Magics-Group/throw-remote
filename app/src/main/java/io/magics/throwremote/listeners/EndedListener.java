package io.magics.throwremote.listeners;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import com.github.nkzawa.emitter.Emitter;

import io.magics.throwremote.MainActivity;
import io.magics.throwremote.R;

public class EndedListener implements Emitter.Listener{

    Activity mActivity;
    Boolean mEnded;
    ImageView mImageView;

    public EndedListener(Activity activity, Boolean ended, ImageView imageView) {
        mActivity = activity;
        mEnded = ended;
        mImageView = imageView;
    }

    @Override
    public void call(Object... args) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageResource(R.drawable.ic_cloud_off_256dp);
            }
        });
        Log.i("ended", "ended");
    }
}

