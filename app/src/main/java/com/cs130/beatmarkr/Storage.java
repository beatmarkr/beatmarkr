package com.cs130.beatmarkr;

import android.database.Cursor;

/**
 * Created by Alina on 5/1/2015.
 */
public interface Storage {

    /** Modification functions ********************************************/

    /** Adds a song to the storage. */
    public void addMusicEntry(Song song);

    /** Deletes a song from storage. Deleting a song also deletes all its bookmarks. */
    public void deleteMusicEntry(long songId);

    /** Adds a bookmark to the storage. */
    public void addBookmarkEntry(Bookmark bm);

    /** Change seekTime and/or description for a given bookmark.
     *  Put null for unchanged values. */
    public void updateBookmarkEntry(Bookmark bmOld, Long newSeekTime, String newDesc);

    /** Deletes bookmark from storage. */
    public void deleteBookmarkEntry(Bookmark bm);

    /** Delete all bookmarks for a given song. */
    public void deleteAllBookmarksForSong(long songId);

    /** Query functions ********************************************/

    /** Search songs by song title keywords.
     *  Null keywords will return all songs. */
    public Cursor queryMusic(String [] keywords);

    /** Search bookmarks by song and description.
     *  First string in descWords must be the musicID of the song. The rest are words in the
     *  description.
     *  If there is only 1 element in descWords, it will return all bookmarks for that song. */
    public Cursor queryBookmarks(String [] descWords);

    /** Returns all bookmarks with a seekTime in the range [start, end] for a given song. */
    public Cursor queryBookmarksByRange(long musicId, long start, long end);

    /** Any cleanup that might be needed. */
    public void close();
}
