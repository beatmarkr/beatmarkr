package com.cs130.beatmarkr;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Alina on 4/23/2015.
 */
public class Song {
    long id;
    String title;
    String artist;

    public Song(long sId, String sTitle, String sArtist) {
        id = sId;
        title = sTitle;
        artist = sArtist;
    }

    public long getID() { return id; }

    public String getTitle() {
        return title;
    }

    public String getArtist() { return artist; }

    public Bitmap getArtwork(Context c) {

        // Get song from media store
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID };
        String selection = MediaStore.Audio.Media._ID+"=?";
        String[] selectionArgs = new String[] { "" + String.valueOf(this.id) };
        Cursor mediaCursor = c.getContentResolver().query(mediaUri,projection,selection,selectionArgs,null);

        // Get album id from song
        long album_id = -1;
        if(mediaCursor.getCount() >= 0) {
            mediaCursor.moveToPosition(0);
            album_id = mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        }
        else {
            return null;
        }

        // Get artwork from album id
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri helperUri = ContentUris.withAppendedId(artworkUri, album_id);
        ContentResolver res = c.getContentResolver();
        Bitmap artwork = null;
        try {
            InputStream in = res.openInputStream(helperUri);
            artwork = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            return null;
        }

        return artwork;
    }
}
