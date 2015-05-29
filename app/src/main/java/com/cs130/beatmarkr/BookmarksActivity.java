package com.cs130.beatmarkr;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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
import com.cs130.beatmarkr.Dialog.ErrorDialog;
import com.cs130.beatmarkr.Dialog.NameDialog;
import com.cs130.beatmarkr.Dialog.StartLoopDialog;
import com.cs130.beatmarkr.Settings.SettingsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

public class BookmarksActivity extends Activity {
    private TextView songName, duration;
    private TextView bmLoopStartText, bmLoopEndText, bmLoopStartTime, bmLoopEndTime;
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
    private String START_DESCR = "Start of Song";
    private String END_DESCR = "End of Song";

    private Bookmark bmLoopStart;
    private Bookmark bmLoopEnd;

    private SharedPreferences sharedPref;
    private Storage helper;

    private int startMarkerPos;
    private int endMarkerPos;
    private int seekbarWidth;

    private View startDivider, endDivider;

    private boolean shouldCheckLoop = true;
    private boolean isLoopPaused = false;

    private MediaPlayer soundCount;
    private int recentNumberPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        Intent intent = getIntent();
        songPos = intent.getIntExtra("KEY_SONG_POS", 0);
        song = SongListActivity.getSongList().get(songPos);
        helper = MusicSQLiteHelper.getInstance(getApplicationContext());

        initializeViews();
        getBookmarks();

        // Assume start bookmark is first, end bookmark is last
        bmLoopStart = bmList.get(0);
        bmLoopEnd = bmList.get(bmList.size() - 1);

        bmLoopStartText = (TextView)findViewById(R.id.bm_loop_start);
        bmLoopEndText = (TextView)findViewById(R.id.bm_loop_end);
        bmLoopStartTime = (TextView)findViewById(R.id.bm_loop_start_time);
        bmLoopEndTime = (TextView)findViewById(R.id.bm_loop_end_time);

        // Set text views for loop
        bmLoopStartText.setText(bmLoopStart.getDescription());
        bmLoopStartTime.setText(bmLoopStart.getSeekTimeString());
        bmLoopEndText.setText(bmLoopEnd.getDescription());
        bmLoopEndTime.setText(bmLoopEnd.getSeekTimeString());

        play(findViewById(android.R.id.content));

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
        durationHandler.removeCallbacks(resumeLoop);
        durationHandler = null;
        helper.close();
        if (soundCount != null) {
            //soundCount.stop();
            soundCount.release();
        }
    }

    private void initializeViews() {
        songName = (TextView)findViewById(R.id.songName);
        duration = (TextView)findViewById(R.id.songDuration);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        startDivider = (View)findViewById(R.id.seek_divider_1);
        endDivider = (View)findViewById(R.id.seek_divider_2);
        startDivider.bringToFront();
        endDivider.bringToFront();
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
        if (!isLoopPaused) {
            mediaPlayer.start();
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekbar.setProgress((int) timeElapsed);
            durationHandler.postDelayed(updateSeekBarTime, 100);
        }
    }

    // Handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if (shouldCheckLoop) {
                checkLoop();
            }

            timeElapsed = mediaPlayer.getCurrentPosition();

            // Set seekbar progress
            seekbar.setProgress((int)timeElapsed);

            duration.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeElapsed),
                            TimeUnit.MILLISECONDS.toSeconds(timeElapsed) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)))
            );

            // Repeat yourself that again in 100 milliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    private Runnable resumeLoop = new Runnable() {
        @Override
        public void run() {
            playLoop();
            shouldCheckLoop = true;
            isLoopPaused = false;
        }
    };

    // Check if media player's current position is outside of the loop
    private void checkLoop() {
        timeElapsed = mediaPlayer.getCurrentPosition();

        if (timeElapsed >= bmLoopEnd.getSeekTime() - offset ||
                timeElapsed < bmLoopStart.getSeekTime() - offset) {

            mediaPlayer.pause();
            shouldCheckLoop = false;
            isLoopPaused = true;

            final int gapInterval = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_GAP_INTERVAL, "5"));
            boolean gapSound = sharedPref.getBoolean(SettingsActivity.KEY_PREF_GAP_SOUND, true);

            durationHandler.postDelayed(resumeLoop, gapInterval*1000);

            if (gapSound) {
                recentNumberPlayed = gapInterval;
                playNextSoundNumber(recentNumberPlayed);
            }
        }
    }

    private void playLoop() {
//        if (soundCount != null && soundCount.isPlaying()) {
//            soundCount.stop();
//        }
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
        if (!isLoopPaused) {
            mediaPlayer.pause();
        }
    }

    // Called when forward button is pressed
    public void forward(View view) {
        if (!isLoopPaused) {
            // Check if we can go forward at forwardTime seconds before song ends
            if ((timeElapsed + forwardTime) <= finalTime) {
                timeElapsed = timeElapsed + forwardTime;

                // Seek to the exact second of the track
                mediaPlayer.seekTo((int) timeElapsed);
            }
        }
    }

    // Called when backward button is pressed
    public void rewind(View view) {
        if (!isLoopPaused) {
            // Check if we go back to beginning of song
            if ((timeElapsed - backwardTime) <= 0) {
                timeElapsed = 0;
            } else {
                timeElapsed = timeElapsed - backwardTime;
            }

            // Seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    // Called when edit button is pressed
    public void editBookmark(View view) {
        int bm_pos = Integer.parseInt(view.getTag().toString());
        Bookmark bm = bmList.get(bm_pos);

//        if ((bm.getSeekTime() == 0 && bm.getDescription() == START_DESCR) ||
//                (bm.getSeekTime() == finalTime && bm.getDescription() == END_DESCR)) {
//            ErrorDialog error = new ErrorDialog(this, "You cannot edit this bookmark.");
//            error.createDialog();
//        }
//        else {
//            EditDialog edit = new EditDialog(this, bm_pos);
//            edit.createDialog();
//        }

        EditDialog edit = new EditDialog(this, bm_pos);
        edit.createDialog();
    }

    // Get the bookmarks from the database
    private void getBookmarks() {
        bmListView = (ListView)findViewById(R.id.bm_list);
        bmList = new ArrayList<Bookmark>();

        Cursor cursor = helper.queryBookmarks(new String[]{Long.toString(song.getID())});
        int index_id = cursor.getColumnIndex(MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID);
        int index_time = cursor.getColumnIndex(MusicDBContract.BookmarkEntry.COLUMN_TIME);
        int index_desc = cursor.getColumnIndex(MusicDBContract.BookmarkEntry.COLUMN_DESC);
        while (cursor.moveToNext()) {
            bmList.add(new Bookmark(Long.valueOf(cursor.getString(index_id)),
                    Long.valueOf(cursor.getString(index_time)),
                    cursor.getString(index_desc)));
        }
        cursor.close();

        // Add start and end bookmarks if they don't exist already
        if (bmList.size() == 0 || bmList.get(0).getSeekTime() != 0) {
            Bookmark startBm = new Bookmark(song.getID(), 0, START_DESCR);
            bmList.add(0, startBm);
            helper.addBookmarkEntry(startBm);
        }
        if (bmList.get(bmList.size() - 1).getSeekTime() != mediaPlayer.getDuration()) {
            Bookmark endBm = new Bookmark(song.getID(), mediaPlayer.getDuration(), END_DESCR);
            bmList.add(endBm);
            helper.addBookmarkEntry(endBm);
        }

        bmAdt = new BookmarkAdapter(this, bmList);
        bmListView.setAdapter(bmAdt);
    }

    // Called when add button is pressed
    // If user attempts to create bookmark with same timestamp, it will silently ignore the attempt.
    public void newBookmark(View view) {
        if (!isLoopPaused) {
            int bmTime = mediaPlayer.getCurrentPosition();
            boolean namePref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_NAME, true);

            Cursor cursor = helper.queryBookmarks(new String[]{Long.toString(song.getID())});
            int index_time = cursor.getColumnIndex(MusicDBContract.BookmarkEntry.COLUMN_TIME);

            while (cursor.moveToNext()) {
                if (Long.valueOf(cursor.getString(index_time)) == bmTime) {
                    return; //don't create a new bookmark - can add a popup alert
                }
            }
            cursor.close();

            // Check settings for auto-generating bookmark names
            if (namePref) {
                Bookmark bm = new Bookmark(song.getID(), bmTime, "Bookmark-" + (bmList.size() + 1));
                bmList.add(bm);
                helper.addBookmarkEntry(bm);
                update();

                int position = bmList.size() - 1;

                // Scroll the list to the bookmark's new position
                for (Bookmark b : bmList) {
                    if (b.equals(bm)) {
                        position = bmList.indexOf(b);
                        break;
                    }
                }

                bmListView.setSelection(position);
            } else {
                pause(findViewById(android.R.id.content));
                bmList.add(new Bookmark(song.getID(), bmTime, ""));
                int position = bmList.size() - 1;

                NameDialog name = new NameDialog(this, position);
                name.createDialog();
            }
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

        //unset the previous listview indicator
        if (bmLoopStart != null) {
            setIndicatorInList(2,bmLoopStart);
        }

        bmLoopStart = bm;
        bmLoopStartText.setText(bmLoopStart.getDescription());
        bmLoopStartTime.setText(bmLoopStart.getSeekTimeString());
        //update start of loop indicator
        seekbarWidth = seekbar.getWidth() - seekbar.getPaddingLeft() - seekbar.getPaddingRight();
        startMarkerPos = ((int)bmLoopStart.getSeekTime() * seekbarWidth /mediaPlayer.getDuration())+ seekbar.getPaddingLeft();
        MarginLayoutParams params = (MarginLayoutParams) startDivider.getLayoutParams();
        params.leftMargin = startMarkerPos;
        startDivider.setLayoutParams(params);
        seekbar.setSecondaryProgress(startMarkerPos);

        setIndicatorInList(0,bmLoopStart);

        playLoop();
    }

    // Called when selecting end of the loop
    public void setBmLoopEnd(Bookmark bm) {

        //unset the previous listview indicator
        if (bmLoopEnd != null && bmLoopEnd != bmLoopStart) {
            setIndicatorInList(2,bmLoopEnd);
        }

        bmLoopEnd = bm;
        bmLoopEndText.setText(bmLoopEnd.getDescription());
        bmLoopEndTime.setText(bmLoopEnd.getSeekTimeString());
        //update end of loop indicator
        seekbarWidth = seekbar.getWidth() - seekbar.getPaddingLeft() - seekbar.getPaddingRight();
        endMarkerPos = ((int)bmLoopEnd.getSeekTime() * seekbarWidth /mediaPlayer.getDuration())+ seekbar.getPaddingLeft();
        MarginLayoutParams params = (MarginLayoutParams) endDivider.getLayoutParams();
        params.leftMargin = endMarkerPos;
        endDivider.setLayoutParams(params);

        setIndicatorInList(1,bmLoopEnd);

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
        bmLoopStartText.setText(bmLoopStart.getDescription());
        bmLoopStartTime.setText(bmLoopStart.getSeekTimeString());
        bmLoopEndText.setText(bmLoopEnd.getDescription());
        bmLoopEndTime.setText(bmLoopEnd.getSeekTimeString());
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

    // Called when set loop button is pressed
    public void setLoop(View view) {
        if (!isLoopPaused) {
            StartLoopDialog startLoop = new StartLoopDialog(this);
            startLoop.createDialog();
        }
    }

    // Getter method to use in dialogs
    public Storage getStorage() { return helper; }

    // Getter method to use in dialogs
    public Bookmark getBmLoopStart() {
        return bmLoopStart;
    }

    // Getter method to use in dialogs
    public Bookmark getBmLoopEnd() {
        return bmLoopEnd;
    }

    // Getter method to use in dialogs
    public Song getSong() {
        return song;
    }

    // Set the indicators in the bookmark list
    // 0 for loop start bookmark
    // 1 for loop end bookmark
    // 2 for setting transparent
    public void setIndicatorInList(int loop, Bookmark bm) {
        int pos=0;
        Cursor cursor = helper.queryBookmarks(new String[]{Long.toString(song.getID())});
        int index_time = cursor.getColumnIndex(MusicDBContract.BookmarkEntry.COLUMN_TIME);
        while (cursor.moveToNext()) {
            if (Long.valueOf(cursor.getString(index_time)) == bm.getSeekTime()) {
                pos = cursor.getPosition();
                break;
            }
        }
        cursor.close();
        if (loop == 0) {
            bmListView.getChildAt(pos).setBackgroundResource(R.drawable.start_selector);
        }
        else if (loop == 1) {
            bmListView.getChildAt(pos).setBackgroundResource(R.drawable.end_selector);
        }
        else if (loop == 2) {
            bmListView.getChildAt(pos).setBackgroundResource(R.drawable.song_selector);
        }
    }

    private void playNextSoundNumber(int number) {
        if (soundCount != null) {
            soundCount.release();
        }

        switch (number) {
            case 1:
                soundCount = MediaPlayer.create(this, R.raw.one);
                break;
            case 2:
                soundCount = MediaPlayer.create(this, R.raw.two);
                break;
            case 3:
                soundCount = MediaPlayer.create(this, R.raw.three);
                break;
            case 4:
                soundCount = MediaPlayer.create(this, R.raw.four);
                break;
            case 5:
                soundCount = MediaPlayer.create(this, R.raw.five);
                break;
            case 6:
                soundCount = MediaPlayer.create(this, R.raw.six);
                break;
            case 7:
                soundCount = MediaPlayer.create(this, R.raw.seven);
                break;
            case 8:
                soundCount = MediaPlayer.create(this, R.raw.eight);
                break;
            case 9:
                soundCount = MediaPlayer.create(this, R.raw.nine);
                break;
            case 10:
                soundCount = MediaPlayer.create(this, R.raw.ten);
                break;
            default:
                break;
        }
        if (soundCount != null) {

            soundCount.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (recentNumberPlayed > 1) {
                        recentNumberPlayed--;
                        playNextSoundNumber(recentNumberPlayed);
                    }
                }
            });

            if (number != 0) {
                soundCount.start();
            }
        }
    }
}
