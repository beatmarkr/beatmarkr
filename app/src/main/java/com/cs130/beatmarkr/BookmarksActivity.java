package com.cs130.beatmarkr;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cs130.beatmarkr.Dialog.EditDialog;
import com.cs130.beatmarkr.Dialog.NameDialog;
import com.cs130.beatmarkr.Settings.SettingsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import android.widget.SeekBar.OnSeekBarChangeListener;

public class BookmarksActivity extends Activity {
    private TextView songName, duration, bmLoopStartText, bmLoopEndText;
    private Song song;
    private int songPos;
    private MediaPlayer mediaPlayer;
    private long timeElapsed = 0, finalTime = 0, offset = 50;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;

    private ArrayList<Bookmark> bmList;
    private ListView bmListView;
    private BookmarkAdapter bmAdt;

    private Bookmark bmLoopStart;
    private Bookmark bmLoopEnd;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Intent intent = getIntent();
        songPos = intent.getIntExtra("KEY_SONG_POS", 0);
        song = SongListActivity.getSongList().get(songPos);

        initializeViews();
        getBookmarks();

        // Assume start bookmark is first, end bookmark is last
        bmLoopStart = bmList.get(0);
        bmLoopEnd = bmList.get(bmList.size()-1);

        bmLoopStartText = (TextView)findViewById(R.id.bm_loop_start);
        bmLoopEndText = (TextView)findViewById(R.id.bm_loop_end);
        bmLoopStartText.setText("START: " + bmLoopStart.getDescription() + " " + bmLoopStart.getSeekTimeString());
        bmLoopEndText.setText("END: " + bmLoopEnd.getDescription() + " " + bmLoopEnd.getSeekTimeString());

        play(findViewById(android.R.id.content));

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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

    private void initializeViews() {
        songName = (TextView)findViewById(R.id.songName);
        duration = (TextView)findViewById(R.id.songDuration);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int prog = seekBar.getProgress();

                duration.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(prog),
                                TimeUnit.MILLISECONDS.toSeconds(prog) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(prog)))
                );
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    int prog = seekBar.getProgress();
                    mediaPlayer.seekTo(prog);
                    durationHandler.postDelayed(updateSeekBarTime, 100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO Auto-generated method stub
                durationHandler.removeCallbacks(updateSeekBarTime);

            }
        });

        long songID = song.getID();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID);
        mediaPlayer = MediaPlayer.create(this, trackUri);

        finalTime = mediaPlayer.getDuration();

        songName.setText(song.getTitle());
        duration.setText(String.format("%02d:%02d", 0, 0));
        seekbar.setMax((int) finalTime);
        seekbar.setClickable(false);
    }

    // Called when play button is pressed
    public void play(View view) {
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int)timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    // Handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            checkLoop();

            timeElapsed = mediaPlayer.getCurrentPosition();

            // Set seekbar progress
            seekbar.setProgress((int)timeElapsed);

            duration.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
                            TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)))
            );

            // Repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    private void checkLoop() {
        timeElapsed = mediaPlayer.getCurrentPosition();

        // Uncomment the code below if you want to use looping
//        if (timeElapsed >= bmLoopEnd.getSeekTime() - offset ||
//                timeElapsed < bmLoopStart.getSeekTime() - offset) {
//            playLoop();
//        }
    }

    private void playLoop() {
        mediaPlayer.stop();

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.seekTo((int) bmLoopStart.getSeekTime());
        mediaPlayer.start();
    }

    // Called when pause button is pressed
    public void pause(View view) {
        mediaPlayer.pause();
    }

    // Called when forward button is pressed
    public void forward(View view) {
        // Check if we can go forward at forwardTime seconds before song ends
        if ((timeElapsed + forwardTime) <= finalTime) {
            timeElapsed = timeElapsed + forwardTime;

            // Seek to the exact second of the track
            mediaPlayer.seekTo((int)timeElapsed);
        }
    }

    // Called when backward button is pressed
    public void rewind(View view) {
        // Check if we go back to beginning of song
        if ((timeElapsed - backwardTime) <= 0) {
            timeElapsed = 0;
        }
        else {
            timeElapsed = timeElapsed - backwardTime;
        }

        // Seek to the exact second of the track
        mediaPlayer.seekTo((int) timeElapsed);
    }

    // Called when edit button is pressed
    public void editBookmark(View view) {
        int bm_pos = Integer.parseInt(view.getTag().toString());

        EditDialog edit = new EditDialog(this, bm_pos);
        edit.createDialog();
    }

    // Get the bookmarks from the database
    private void getBookmarks() {
        bmListView = (ListView)findViewById(R.id.bm_list);
        bmList = new ArrayList<Bookmark>();

        // Add start and end bookmarks if they don't exist already
        bmList.add(new Bookmark(song.getID(), 0, "Start of song"));
        bmList.add(new Bookmark(song.getID(), mediaPlayer.getDuration(), "End of song"));

        bmAdt = new BookmarkAdapter(this, bmList);
        bmListView.setAdapter(bmAdt);
    }

    // Called when add button is pressed
    public void newBookmark(View view) {
        int bmTime = mediaPlayer.getCurrentPosition();
        boolean namePref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_NAME, true);

        // Check settings for auto-generating bookmark names
        if (namePref) {
            Bookmark bm = new Bookmark(song.getID(), bmTime, "Bookmark-" + (bmList.size() + 1));
            bmList.add(bm);
            update();

            int position = bmList.size()-1;

            // Scroll the list to the bookmark's new position
            for (Bookmark b : bmList) {
                if (b.equals(bm)) {
                    position = bmList.indexOf(b);
                    break;
                }
            }

            bmListView.setSelection(position);
        }
        else {
            pause(findViewById(android.R.id.content));
            bmList.add(new Bookmark(song.getID(), bmTime, ""));
            int position = bmList.size()-1;

            NameDialog name = new NameDialog(this, position);
            name.createDialog();
        }
    }

    // Called when selecting a bookmark from the list
    public void playBookmark(View view) {
        int bm_pos = Integer.parseInt(view.getTag().toString());
        Bookmark bm = bmList.get(bm_pos);

        mediaPlayer.seekTo((int) bm.getSeekTime());

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Called when selecting start of the loop
    public void setBmLoopStart(Bookmark bm) {
        bmLoopStart = bm;
        bmLoopStartText.setText("START: " + bmLoopStart.getDescription() + " " + bmLoopStart.getSeekTimeString());
        playLoop();
    }

    // Called when selecting end of the loop
    public void setBmLoopEnd(Bookmark bm) {
        bmLoopEnd = bm;
        bmLoopEndText.setText("END: " + bmLoopEnd.getDescription() + " " + bmLoopEnd.getSeekTimeString());
        playLoop();
    }

    // Update the various things on the screen
    public void update() {
        Collections.sort(bmList, new Comparator<Bookmark>() {
            public int compare(Bookmark a, Bookmark b) {
                return a.getSeekTimeString().compareTo(b.getSeekTimeString());
            }
        });

        bmAdt.notifyDataSetChanged();
        bmLoopStartText.setText("START: " + bmLoopStart.getDescription() + " " + bmLoopStart.getSeekTimeString());
        bmLoopEndText.setText("END: " + bmLoopEnd.getDescription() + " " + bmLoopEnd.getSeekTimeString());
    }

    // Getter method to use in dialogs
    public ArrayList<Bookmark> getBmList() {
        return bmList;
    }

    // Getter method to use in dialogs
    public ListView getBmListView() {
        return bmListView;
    }

    // Getter method to use in dialogs
    public long getFinalTime() {
        return finalTime;
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
