package com.cs130.beatmarkr;

import android.provider.BaseColumns;

/**
 * Created by Alina on 4/23/2015.
 */

/**
 * The contract class for the Music database. It lists the tables and their column names.
 * This way, all constants for the database is defined in one class.
 */
public final class MusicDBContract {
    // Don't instantiate this class.
    private MusicDBContract() {}

    /* Music table contents */
    public static abstract class MusicEntry implements BaseColumns {
        public static final String TABLE_NAME = "Music";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST = "artist";
        public static final String COLUMN_MUSIC_ID = "id";
    }

    /* Bookmarks table contents */
    public static abstract class BookmarkEntry implements BaseColumns {
        public static final String TABLE_NAME = "Bookmarks";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_TIME = "seekTime";
        public static final String COLUMN_MUSIC_ID = "id";
    }
}
