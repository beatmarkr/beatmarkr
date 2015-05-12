package com.cs130.beatmarkr;

import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cs130.beatmarkr.Dialog.EditDialog;

import java.util.concurrent.TimeUnit;

public class BookmarksActivity extends ActionBarActivity {
    public TextView songName, duration;
    private int songPos;
    private MediaPlayer mediaPlayer;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Intent intent = getIntent();
        songPos = intent.getIntExtra("KEY_SONG_POS", 0);

        initializeViews();
        play(findViewById(android.R.id.content));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        durationHandler.removeCallbacks(updateSeekBarTime);
        durationHandler = null;
    }

    public void editBookmark(View view) {
        EditDialog edit = new EditDialog(this);
        edit.createDialog();
    }

    public void initializeViews() {
        songName = (TextView)findViewById(R.id.songName);
        duration = (TextView)findViewById(R.id.songDuration);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        Song song = SongListActivity.songList.get(songPos);

        long songID = song.getID();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID);
        mediaPlayer = MediaPlayer.create(this, trackUri);

        finalTime = mediaPlayer.getDuration();

        songName.setText(song.getTitle());
        duration.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long)finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long)finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)finalTime))));
        seekbar.setMax((int) finalTime);
        seekbar.setClickable(false);
    }

    // Play song
    public void play(View view) {
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    // Handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            // Get current position
            timeElapsed = mediaPlayer.getCurrentPosition();

            // Set seekbar progress
            seekbar.setProgress((int)timeElapsed);

            // Set time remaining
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long)timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long)timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)timeRemaining))));

            // Repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    // Pause song
    public void pause(View view) {
        mediaPlayer.pause();
    }

    // Go forward at forwardTime seconds
    public void forward(View view) {
        // Check if we can go forward at forwardTime seconds before song ends
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            // Seek to the exact second of the track
            mediaPlayer.seekTo((int)timeElapsed);
        }
    }

    // Go backward at backwardTime seconds
    public void rewind(View view) {
        // Check if we go back to beginning of song
        if ((timeElapsed - backwardTime) <= 0) {
            timeElapsed = 0;
        }
        else {
            timeElapsed = timeElapsed - backwardTime;
        }

        // Seek to the exact second of the track
        mediaPlayer.seekTo((int)timeElapsed);
    }
}
