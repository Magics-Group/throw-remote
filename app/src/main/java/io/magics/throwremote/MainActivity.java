package io.magics.throwremote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.lang.Math;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private SeekBar mSeekbar;
    private TextView mTimeTextView;
    private ImageButton mPlayImageButton;
    private Boolean mPlaying = true;
    private ImageButton mMuteImageButton;
    private Boolean mMuted = false;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.19.223.8:1337");
        } catch (URISyntaxException e) {}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekbar = (SeekBar) findViewById(R.id.seekBar2);
        mTimeTextView = (TextView) findViewById(R.id.textView);
        mPlayImageButton = (ImageButton) findViewById(R.id.imageButton2);
        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlaying = !mPlaying;
                mSocket.emit("playing", mPlaying);
                runOnUiThread(new Runnable() {
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
        });
        mMuteImageButton = (ImageButton) findViewById(R.id.imageButton5);

        mMuteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMuted = !mMuted;
                mSocket.emit("muted", mMuted);
                runOnUiThread(new Runnable() {
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
        });

        mSocket.on("position", onPositionUpdate);
        mSocket.on("time", onTimeUpdate);
        mSocket.on("playing", onPlayToggle);
        mSocket.on("muted", onMuteToggle);
        mSocket.on("ended", onEnded);
        mSocket.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_scan:
                try {
                    intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                    break;
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                    break;
                }
            case R.id.action_stream:
                try {
                    intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                    break;
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                    break;
                }
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.action_help:
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.i("qr", contents);

            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    private Emitter.Listener onPositionUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Double position = (Double) args[0];
            Log.i("position", position.toString());
            final int positionPercent = (int) Math.round(position * 100);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSeekbar.setProgress(positionPercent);
                }
            });
        }
    };

    private Emitter.Listener onTimeUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Integer time = (Integer) args[0];
            Log.i("time", time.toString());

            final String dateFormatted = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTimeTextView.setText(dateFormatted);
                }
            });
        }
    };

    private Emitter.Listener onPlayToggle = new Emitter.Listener() {
        @Override
        public void call (Object... args) {
            mPlaying = (Boolean) args[0];
            Log.i("playing", mPlaying.toString());
            runOnUiThread(new Runnable() {
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
    };

    private Emitter.Listener onMuteToggle = new Emitter.Listener() {
        @Override
        public void call (Object... args) {
            mMuted = (Boolean) args[0];
            Log.i("muted", mMuted.toString());
            runOnUiThread(new Runnable() {
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
    };

    private Emitter.Listener onEnded = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("ended", "ended");
        }
    };



}
