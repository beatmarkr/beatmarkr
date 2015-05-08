package com.cs130.beatmarkr;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import com.cs130.beatmarkr.Dialog.EditDialog;
import com.cs130.beatmarkr.MusicService.MusicBinder;

public class SongListActivity extends Activity implements MediaPlayerControl {
    private ArrayList<Song> songList; //List of songs displayed in alphabetical order
    private ListView songView;
    private SongAdapter songAdt;

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    private MusicController controller;
    private boolean paused = false;
    private boolean playbackPaused = false;

    private ArrayList<Bookmark> bmList;
    private ListView bmListView;
    private BookmarkAdapter bmAdt;

    private Button bookmarkButton;
    private TextView bookmarkTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();

        getSongList(); //see function for To Do

        // Sort the songs alphabetically
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);

        setController();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_back:
                setContentView(R.layout.activity_song_list);
                songView = (ListView)findViewById(R.id.song_list);
                songView.setAdapter(songAdt);
                controller.setAnchorView(findViewById(R.id.song_list));
                break;
            case R.id.action_close:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                break;
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }

        setContentView(R.layout.activity_bookmarks);

        // Dummy bookmarks
        bmListView = (ListView)findViewById(R.id.bm_list);
        if (bmList == null) {
            bmList = new ArrayList<Bookmark>();
            bmList.add(new Bookmark(0, 0, "Start"));
            bmList.add(new Bookmark(0, 0, "End"));
            bmAdt = new BookmarkAdapter(this, bmList);
        }

        bmListView.setAdapter(bmAdt);

        //controller.setAnchorView(findViewById(android.R.id.content));
        controller.setAnchorView(findViewById(R.id.player_view));
        controller.show(0);

        bookmarkButton = (Button)this.findViewById(R.id.bookmarkButton);
        bookmarkTime = (TextView)this.findViewById(R.id.timestamp);

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                bookmarkButton.setBackgroundColor(color);

                int millis = getCurrentPosition();

                String mstring = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                );

                bookmarkTime.setText("Last bookmark: " + mstring);
                //bookmarkTime.setText(Integer.toString(musicSrv.getSongPos()));
                //create bookmark using Bookmark class here
            }
        });
        bookmarkButton.setVisibility(View.VISIBLE);
    }

    public void editBookmark(View view) {
        EditDialog edit = new EditDialog(this);
        edit.createDialog();
    }

    /** Called each time the app is opened. It updates the database's songs by getting the list of
     * songs (audio media) on the device. It also populates songList.
     * TODO: add/delete songs from database based on current songs on device + multithreading
     * As of now, it will try to add songs each time (database will reject existing entries
     * by music ID).
     */

    public void getSongList() {

        Storage helper = MusicSQLiteHelper.getInstance(getApplicationContext());
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            // Get columns
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            // Add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                helper.addMusicEntry(new Song(thisId, thisTitle, thisArtist));
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
            helper.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicSrv.go();
        bookmarkButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();
        bookmarkButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return musicSrv.getDur();
        }
        else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return musicSrv.getPos();
        }
        else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv != null && musicBound) {
            return musicSrv.isPng();
        }

        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    // Connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicBinder)service;

            // Get service
            musicSrv = binder.getService();

            // Pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void setController(){
        if (controller == null) {
            controller = new MusicController(this);
        }

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    private void playPrev(){
        musicSrv.playPrev();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void playNext(){
        musicSrv.playNext();

        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }
}
