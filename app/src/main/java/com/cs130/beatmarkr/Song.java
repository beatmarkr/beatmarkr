package com.cs130.beatmarkr;

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

    public String getArtist() {
        return artist;
    }
}
