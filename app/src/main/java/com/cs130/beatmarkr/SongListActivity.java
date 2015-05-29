package com.cs130.beatmarkr;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import com.cs130.beatmarkr.Settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SongListActivity extends Activity {
    private static ArrayList<Song> songList; //List of songs displayed in alphabetical order
    private static ArrayList<Song> filteredSongs;
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songView = (ListView)findViewById(R.id.song_list);
        songList = new ArrayList<Song>();
        filteredSongs = new ArrayList<Song>();

        getSongs();

        // Sort the songs alphabetically
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        filteredSongs = songList;
        final SongAdapter songAdt = new SongAdapter(this, filteredSongs);
        songView.setAdapter(songAdt);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Called when selecting a song from the list
    public void songPicked(View view) {
        Intent intent = new Intent(this, BookmarksActivity.class);
        intent.putExtra("KEY_SONG_POS", Integer.parseInt(view.getTag().toString()));
        startActivity(intent);
    }


    /** Called each time the app is opened. It updates the database's songs by getting the list of
     * songs (audio media) on the device. It also populates songList.
     * It will add/delete songs from database to match with current songs on device.
     * As of now, it will try to add songs each time (database will reject existing entries
     * by music ID).
     */

    private void getSongs() {
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
                //helper.addMusicEntry(new Song(thisId, thisTitle, thisArtist));
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
            //helper.close();
        }
        // Sort the songs by ID
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return (int)(a.getID() - b.getID());
            }
        });

        // Get list of songs in database and update database on new songs and songs that no longer exist
        Cursor dbCursor = helper.queryMusic(new String[]{}); //all songs in alphabetical order
        int idCol = dbCursor.getColumnIndex(MusicDBContract.MusicEntry.COLUMN_MUSIC_ID);
        int titleCol = dbCursor.getColumnIndex(MusicDBContract.MusicEntry.COLUMN_TITLE);
        int artistCol = dbCursor.getColumnIndex(MusicDBContract.MusicEntry.COLUMN_ARTIST);
        ArrayList<Song> dbSongs = new ArrayList<Song>();
        while (dbCursor.moveToNext()) {
            dbSongs.add(new Song(Long.valueOf(dbCursor.getString(idCol)),
                                 dbCursor.getString(titleCol),
                                 dbCursor.getString(artistCol)));
        }
        dbCursor.close();
        int min = Math.min(songList.size(),dbSongs.size());
        int i=0; //index for songList
        int j=0; //index for dbSongs
        for (; i<min && j<min;) {
            if (songList.get(i).getID() == dbSongs.get(j).getID() &&
                songList.get(i).getTitle().equals(dbSongs.get(j).getTitle()) &&
                songList.get(i).getArtist().equals(dbSongs.get(j).getArtist())) {
                i++; j++;
            } else if (songList.get(i).getID() < dbSongs.get(j).getID()) { //song got added
                helper.addMusicEntry(songList.get(i));
                i++;
            } else if (songList.get(i).getID() > dbSongs.get(j).getID()) { //song got deleted
                helper.deleteMusicEntry(dbSongs.get(j).getID());
                j++;
            } else { //new song got assigned to ID existing in database
                helper.deleteMusicEntry(songList.get(i).getID());
                helper.addMusicEntry(songList.get(i));
                i++; j++;
            }
        }
        if (min == songList.size()) {
            //delete rest of entries in database
            for (;j < dbSongs.size(); j++) {
                helper.deleteMusicEntry(j);
            }
        } else { // min == dbSongs.size()
            //add rest of entries in songList
            for (;i < songList.size(); i++) {
                helper.addMusicEntry(songList.get(i));
            }
        }
        helper.close();
    }

    // Getter method to use in BookmarksActivity
    public static ArrayList<Song> getSongList() {
        return songList;
    }
}
