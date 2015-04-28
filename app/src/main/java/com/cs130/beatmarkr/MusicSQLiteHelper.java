package com.cs130.beatmarkr;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alina on 4/23/2015.
 * This is a Singleton class.
 */

public class MusicSQLiteHelper extends SQLiteOpenHelper {

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

    /** Call this function when you're done writing. */
    @Override
    public synchronized void close() {
        if (helper != null)
            db.close();
    }

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
        this.close();
    }

    public void addBookmarkEntry(Bookmark bm) {
        ContentValues values = new ContentValues();
        values.put(MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID, bm.getMusicID());
        values.put(MusicDBContract.BookmarkEntry.COLUMN_TIME, bm.getSeekTime());
        values.put(MusicDBContract.BookmarkEntry.COLUMN_DESC, bm.getDescription());

        // Insert the new row
        db.insert(MusicDBContract.BookmarkEntry.TABLE_NAME,null,values);
    }

    /** User can change seekTime and/or description only. Put null for nonnew values. */
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
        this.close();
    }

    public void deleteBookmarkEntry(Bookmark bm) {
        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ? AND " +
                       MusicDBContract.BookmarkEntry.COLUMN_TIME + " = ?";
        String[] whereArgs = { String.valueOf(bm.getMusicID()), String.valueOf(bm.getSeekTime()) };
        db.delete(MusicDBContract.BookmarkEntry.TABLE_NAME, where, whereArgs);
        this.close();
    }

    public void deleteAllBookmarksForSong(long songId) {
        String where = MusicDBContract.BookmarkEntry.COLUMN_MUSIC_ID + " = ?";
        String[] whereArgs = { String.valueOf(songId) };
        db.delete(MusicDBContract.BookmarkEntry.TABLE_NAME, where, whereArgs);
        this.close();
    }
}
