package com.cs130.beatmarkr;

/**
 * Created by Alina on 4/23/2015.
 */
public class Bookmark {
    long musicId;
    long seekTime;
    String description;

    public Bookmark(long mid, long time, String desc) {
        musicId = mid;
        seekTime = time;
        description = desc;
    }

    public long getMusicID() {
        return musicId;
    }

    public long getSeekTime() {
        return seekTime;
    }

    public String getDescription() {
        return description;
    }
}
