package com.cs130.beatmarkr;

import java.util.concurrent.TimeUnit;

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

    public String getSeekTimeString() {
        String formatString = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(seekTime),
                TimeUnit.MILLISECONDS.toSeconds(seekTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seekTime))
        );

        return formatString;
    }

    public void setSeekTime(long time) {
        seekTime = time;
    }

    public void setDescription(String s) {
        description = s;
    }
}
