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
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private SeekBar mSeekbar;
    private int mPositionValue;
    private TextView mTimeTextView;
    private TextView mLengthTextView;
    private TextView mTitleTextView;
    private ImageButton mPlayImageButton;
    private Boolean mPlaying;
    private ImageButton mMuteImageButton;
    private Boolean mMuted;
    private ImageButton mForwardImageButton;
    private ImageButton mBackwardImageButton;
    private ImageButton mStopButton;
    private ImageView mImageView;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekbar = (SeekBar) findViewById(R.id.seekBar2);
        mTimeTextView = (TextView) findViewById(R.id.textView);
        mLengthTextView = (TextView) findViewById(R.id.textView2);
        mTitleTextView = (TextView) findViewById(R.id.textView3);
        mPlayImageButton = (ImageButton) findViewById(R.id.imageButton2);
        mMuteImageButton = (ImageButton) findViewById(R.id.imageButton4);
        mForwardImageButton = (ImageButton) findViewById(R.id.imageButton5);
        mBackwardImageButton = (ImageButton) findViewById(R.id.imageButton3);
        mStopButton = (ImageButton) findViewById(R.id.imageButton7);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mPositionValue = 0;
        mMuted = false;
        mPlaying = true;

        /*mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSocket.emit("position", progress / 10000.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/


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
                            Log.i("pause","pause");
                        } else {
                            mPlayImageButton.setBackgroundResource(R.drawable.ic_play_circle_outline_64dp);
                            Log.i("play", "play");
                        }
                    }
                });
            }
        });


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

        mForwardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double percent = mPositionValue / 10000.0;
                Log.i("percent", String.valueOf(percent));
                mSocket.emit("forward", percent);
            }
        });

        mBackwardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double percent = mPositionValue / 10000.0;
                Log.i("percent", String.valueOf(percent));
                mSocket.emit("backward", percent);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("end", "end");
                mSocket.emit("ended");
            }
        });

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
                String json = data.getStringExtra("SCAN_RESULT");
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    String ip = jsonObj.getString("ip");
                    int port = jsonObj.getInt("port");
                    int pin = jsonObj.getInt("pin");
                    String uri = "http://" + ip + ":" + String.valueOf(port);
                    Log.i("uri", uri);
                    Log.i("pin", String.valueOf(pin));
                    try {
                        mSocket = IO.socket(uri);
                        mSocket.connect();
                        mSocket.emit("pin", pin);
                        mSocket.on("title", onTitleUpdate);
                        mSocket.on("length", onLengthUpdate);
                        mSocket.on("position", onPositionUpdate);
                        mSocket.on("time", onTimeUpdate);
                        mSocket.on("playing", onPlayToggle);
                        mSocket.on("muted", onMuteToggle);
                        mSocket.on("ended", onEnded);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImageView.setImageResource(R.drawable.ic_done_256dp);
                            }
                        });

                    } catch (Exception e) {e.printStackTrace();}
                } catch (JSONException e) {e.printStackTrace();}
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

    private Emitter.Listener onTitleUpdate = new Emitter.Listener() {
        @Override
        public void call(Object ... args) {
            final String title = (String) args[0];
            Log.i("title", title);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTitleTextView.setText(title);
                }
            });
        }
    };

    private Emitter.Listener onLengthUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Integer length = (Integer) args[0];
            Log.i("length", length.toString());
            final String dateFormatted = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(length),
                    TimeUnit.MILLISECONDS.toMinutes(length) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(length)),
                    TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length)));
            Log.i("length", dateFormatted);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLengthTextView.setText(dateFormatted);
                }
            });
        }
    };

    private Emitter.Listener onPositionUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Double position = (Double) args[0];
            //Log.i("position", position.toString());
            mPositionValue = (int) Math.round(position * 10000);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSeekbar.setProgress(mPositionValue);
                }
            });
        }
    };

    private Emitter.Listener onTimeUpdate = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Integer time = (Integer) args[0];
            //Log.i("time", time.toString());
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageResource(R.drawable.ic_cloud_off_256dp);
                }
            });
            Log.i("ended", "ended");
        }
    };
}
