package com.example.samplemediaplayer;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback {
    private MediaPlayer sampleMediaPlayer;
    private final Runnable playbackTimeUpdateTask = new Runnable() {
        public void run() {
            if (sampleMediaPlayer.isPlaying()) {
                int playtime;
                int duration;
                TextView textView;
                textView = (TextView)findViewById(R.id.textview_time);
                duration = sampleMediaPlayer.getDuration();
                playtime = sampleMediaPlayer.getCurrentPosition();
                textView.setText(String.valueOf(playtime) + " ms / " + String.valueOf(duration + " ms"));
            }
        }
    };
    class PlaybackTimerTask extends TimerTask {
        @Override
        public void run() {
            // Updating TextView must be in UI thread
            runOnUiThread(playbackTimeUpdateTask);
        }
    }
    private Timer playbackTimer = null;
    private PlaybackTimerTask timerTask = null;

    // SurfaceView and SurfaceHolder for MediaPlayer video surface
    private SurfaceView videoSurfaceView;
    private SurfaceHolder surfaceHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sampleMediaPlayer = new MediaPlayer();
        sampleMediaPlayer.setOnCompletionListener(this);
        sampleMediaPlayer.setOnPreparedListener(this);

        // Set MediaPlayer video playback surface
        videoSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolder = videoSurfaceView.getHolder();
        // MediaPlayer.setDisplay() will be called in :surfaceCreated() callback
        surfaceHolder.addCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_audio) {
            AssetFileDescriptor afd;
            afd = getBaseContext().getResources().openRawResourceFd(R.raw.handel_water_music);
            try {
                sampleMediaPlayer.reset();
                sampleMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                sampleMediaPlayer.prepare();
                sampleMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (id == R.id.action_video) {
            String videoPath;
            videoPath = "http://ie.microsoft.com/TEStdrive/Graphics/VideoFormatSupport/big_buck_bunny_trailer_480p_baseline.mp4";
            try {
                sampleMediaPlayer.reset();
                sampleMediaPlayer.setDataSource(videoPath);
                sampleMediaPlayer.prepareAsync();
                TextView t = (TextView)findViewById(R.id.textview_time);
                t.setText("Buffering...");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Implementation of MediaPlayer Listener callbacks
    @Override
    public void onCompletion(MediaPlayer mp) {
        TextView t = (TextView)findViewById(R.id.textview_time);
        t.setText("Complete");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        TextView t = (TextView)findViewById(R.id.textview_time);
        MediaPlayer.TrackInfo info[] = mp.getTrackInfo();
        t.setText(info[0].toString());

        sampleMediaPlayer.start();

        // Cancel previous Timer
        if (playbackTimer != null) {
            playbackTimer.cancel();
            timerTask.cancel();
        }
        // Once Timer is canceled, it is required to create a new timer.
        playbackTimer = new Timer();
        timerTask = new PlaybackTimerTask();
        // Timer starts after 1000 ms with 1000 ms period
        playbackTimer.schedule(timerTask, 1000, 1000);
    }

    //  Implementation of SurfaceHolder callbacks
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sampleMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
