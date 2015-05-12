package com.cs130.beatmarkr;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SongListActivity extends Activity {
    public static ArrayList<Song> SONG_LIST; //List of songs displayed in alphabetical order
    private ListView songView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songView = (ListView)findViewById(R.id.song_list);
        SONG_LIST = new ArrayList<Song>();

        getSongList(); //see function for To Do

        // Sort the songs alphabetically
        Collections.sort(SONG_LIST, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        SongAdapter songAdt = new SongAdapter(this, SONG_LIST);
        songView.setAdapter(songAdt);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void songPicked(View view) {
        Intent intent = new Intent(this, BookmarksActivity.class);
        intent.putExtra("KEY_SONG_POS", Integer.parseInt(view.getTag().toString()));
        startActivity(intent);
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
                SONG_LIST.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
            helper.close();
        }
    }
}
