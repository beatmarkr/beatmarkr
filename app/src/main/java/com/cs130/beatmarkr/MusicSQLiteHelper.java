package com.cs130.beatmarkr;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alina on 4/23/2015.
 * This is a Singleton class. In using this class, when calling database modification functions,
 * you must call
 * helper.close();
 * Where helper is your reference to MusicSQLiteHelper.getInstance(). The database will be open upon
 */

public class MusicSQLiteHelper extends SQLiteOpenHelper implements Storage {

    private static MusicSQLiteHelper helper;
    private static SQLiteDatabase db;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Music.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA = ",";


    private MusicSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //might need synchronize or something for thread safety
    public static MusicSQLiteHelper getInstance(Context context) {
        if (helper == null) {
            helper = new MusicSQLiteHelper(context);
            db = helper.getWritableDatabase();
        }
        if (!db.isOpen()) {
            db = helper.getWritableDatabase();
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MUSIC_TABLE =
                "CREATE TABLE " + MusicDBContract.MusicEntry.TABLE_NAME + " (" +
                        MusicDBContract.MusicEntry.COLUMN_MUSIC_ID + INT_TYPE + " PRIMARY KEY," +
                        MusicDBContract.MusicEntry.COLUMN_TITLE + TEXT_TYPE + COMMA +
                        MusicDBContract.MusicEntry.COLUMN_ARTIST + TEXT_TYPE +
                " )";
        String SQL_CREATE_BOOKMARKS_TABLE =
                "CREATE TABLE " + MusicDBContract.BookmarkEntry.TABLE_NAME + " (" +
                        MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + INT_TYPE + COMMA +
                        MusicDBContract.BookmarkEntry.COLUMN_DESC + TEXT_TYPE + COMMA +
                        MusicDBContract.BookmarkEntry.COLUMN_TIME + INT_TYPE + COMMA +
                        " PRIMARY KEY (" + MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + COMMA +
                                           MusicDBContract.BookmarkEntry.COLUMN_TIME + " )" +
                " )";
        db.execSQL(SQL_CREATE_MUSIC_TABLE);
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
    }

    /** Used when schema changes (and thus database version updates).
     * TODO: 1. Move data from the old to new. 2. Delete old tables. 3. onCreate(db)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onCreate(db);
    }

    /** Call this function when you're done with the database.
     *  This will invalidate any open Cursors. */
    @Override
    public synchronized void close() {
        if (helper != null)
            db.close();
    }

    /** Modification functions ********************************************/
    /*  These require a writable database. */
    public void addMusicEntry(Song song) {
        ContentValues values = new ContentValues();
        values.put(MusicDBContract.MusicEntry.COLUMN_MUSIC_ID, song.getID());
        values.put(MusicDBContract.MusicEntry.COLUMN_TITLE, song.getTitle());
        values.put(MusicDBContract.MusicEntry.COLUMN_ARTIST, song.getArtist());

        // Insert the new row
        db.insert(MusicDBContract.MusicEntry.TABLE_NAME,null,values);
    }

    /** Deleting a song also deletes all its bookmarks. */
    public void deleteMusicEntry(long songId) {
        deleteAllBookmarksForSong(songId);

        String where = MusicDBContract.MusicEntry.COLUMN_MUSIC_ID + " = ?";
        String[] whereArgs = { String.valueOf(songId) };
        db.delete(MusicDBContract.MusicEntry.TABLE_NAME, where, whereArgs);
    }

    public void addBookmarkEntry(Bookmark bm) {
        ContentValues values = new ContentValues();
        values.put(MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID, bm.getMusicID());
        values.put(MusicDBContract.BookmarkEntry.COLUMN_TIME, bm.getSeekTime());
        values.put(MusicDBContract.BookmarkEntry.COLUMN_DESC, bm.getDescription());

        // Insert the new row
        db.insert(MusicDBContract.BookmarkEntry.TABLE_NAME,null,values);
    }

    /** User can change seekTime and/or description only. Put null for unchanged values. */
    public void updateBookmarkEntry(Bookmark bmOld, Long newSeekTime, String newDesc) {
        ContentValues values = new ContentValues();
        if (newSeekTime != null) {
            values.put(MusicDBContract.BookmarkEntry.COLUMN_TIME, newSeekTime);
        }
        if (newDesc != null) {
            values.put(MusicDBContract.BookmarkEntry.COLUMN_DESC, newDesc);
        }

        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ? AND " +
                MusicDBContract.BookmarkEntry.COLUMN_TIME + " = ?";
        String[] whereArgs = { String.valueOf(bmOld.getMusicID()), String.valueOf(bmOld.getSeekTime()) };

        db.update(MusicDBContract.BookmarkEntry.TABLE_NAME, values, where, whereArgs);
    }

    public void deleteBookmarkEntry(Bookmark bm) {
        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ? AND " +
                       MusicDBContract.BookmarkEntry.COLUMN_TIME + " = ?";
        String[] whereArgs = { String.valueOf(bm.getMusicID()), String.valueOf(bm.getSeekTime()) };
        db.delete(MusicDBContract.BookmarkEntry.TABLE_NAME, where, whereArgs);
    }

    public void deleteAllBookmarksForSong(long songId) {
        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ?";
        String[] whereArgs = { String.valueOf(songId) };
        db.delete(MusicDBContract.BookmarkEntry.TABLE_NAME, where, whereArgs);
    }

    /** Query functions ********************************************/
    /*  These require a readable database. */

    /** Search songs by song title keywords.
     *  Null keywords will return all songs. */
    public Cursor queryMusic(String [] keywords) {
        String[] projection = { //return values
                MusicDBContract.MusicEntry.COLUMN_MUSIC_ID,
                MusicDBContract.MusicEntry.COLUMN_TITLE,
                MusicDBContract.MusicEntry.COLUMN_ARTIST
        };

        String sortOrder =
                MusicDBContract.MusicEntry.COLUMN_TITLE + " DESC";

        String where = null;
        if (keywords.length > 0) {
            where = "";
            for (int i = 0; i < keywords.length - 1; i++) {
                where += MusicDBContract.MusicEntry.COLUMN_TITLE + " LIKE ?% AND ";
            }
            where += MusicDBContract.MusicEntry.COLUMN_TITLE + " LIKE ?%";
        }

        Cursor c = db.query(
                MusicDBContract.MusicEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                where,                                    // The columns for the WHERE clause
                keywords,                                 // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    /** Search bookmarks by song and description.
     *  First string in descWords must be the musicID of the song. The rest are words in the
     *  description.
     *  If there is only 1 element in descWords, it will return all bookmarks for that song. */
    public Cursor queryBookmarks(String [] descWords) {
        String[] projection = { //return values
                MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID,
                MusicDBContract.BookmarkEntry.COLUMN_TIME,
                MusicDBContract.BookmarkEntry.COLUMN_DESC
        };

        String sortOrder =
                MusicDBContract.BookmarkEntry.COLUMN_TIME + " DESC";

        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ?";
        if (descWords.length > 1) {
            for (int i = 1; i < descWords.length; i++) {
                where += " AND " + MusicDBContract.BookmarkEntry.COLUMN_DESC + " LIKE ?%";
            }
        }

        Cursor c = db.query(
                MusicDBContract.BookmarkEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                where,                                    // The columns for the WHERE clause
                descWords,                                 // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    /** Returns all bookmarks with a seekTime in the range [start, end] for a given song. */
    public Cursor queryBookmarksByRange(long musicId, long start, long end) {
        String[] projection = { //return values
                MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID,
                MusicDBContract.BookmarkEntry.COLUMN_TIME,
                MusicDBContract.BookmarkEntry.COLUMN_DESC
        };

        String sortOrder =
                MusicDBContract.BookmarkEntry.COLUMN_TIME + " DESC";

        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ? AND " +
                       MusicDBContract.BookmarkEntry.COLUMN_TIME + " >= ? AND " +
                       MusicDBContract.BookmarkEntry.COLUMN_TIME + "<= ?";
        String [] whereArgs = {String.valueOf(musicId), String.valueOf(start), String.valueOf(end)};

        Cursor c = db.query(
                MusicDBContract.BookmarkEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                where,                                    // The columns for the WHERE clause
                whereArgs,                                 // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }
}
