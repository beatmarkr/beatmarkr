package com.cs130.beatmarkr;

import android.database.Cursor;

/**
 * Created by Alina on 5/1/2015.
 */
public interface Storage {

    /** Modification functions ********************************************/

    /**
     * Adds a song to the storage.
     * @param song Takes in a song to add to the storage
     */
    public void addMusicEntry(Song song);

    /**
     * Deletes a song from storage. Deleting a song also deletes all its bookmarks.
     * @param songId The music ID (identifier for device)
     */
    public void deleteMusicEntry(long songId);

    /**
     * Adds a bookmark to the storage.
     * @param bm Takes in a bookmark to add to the storage
     */
    public void addBookmarkEntry(Bookmark bm);

    /**
     * Change seekTime and/or description for a given bookmark.
     * Put null for unchanged values.
     * @param bmOld The old bookmark to modify
     * @param newSeekTime The new time of the bookmark. Null if not changed.
     * @param newDesc The new description of the bookmark. Null if not changed.
     */
      public void updateBookmarkEntry(Bookmark bmOld, Long newSeekTime, String newDesc);

    /**
     * Deletes bookmark from storage.
     * @param bm The bookmark to delete
     */
    public void deleteBookmarkEntry(Bookmark bm);

    /**
     * Delete all bookmarks for a given song.
     * @param songId The music ID (identifier for device)
     */
    public void deleteAllBookmarksForSong(long songId);

    /** Query functions ********************************************/

    /**
     * Search songs by song title and artist keywords.
     * @param keywords Word array of keywords. Empty (not null) keywords will return all songs.
     * @return Cursor that iterates over the results
     */
    public Cursor queryMusic(String [] keywords);

    /**
     * Search bookmarks by song and description.
     *  If there is only 1 element in descWords, it will return all bookmarks for that song.
     * @param descWords First string must be musicID of the song. The rest are words in the description.
     * @return Cursor that iterates over the results
     */
    public Cursor queryBookmarks(String [] descWords);

    /**
     * Returns all bookmarks with a seekTime in the range [start, end] for a given song.
     * @param musicId The music ID (identifier for device)
     * @param start The start of the time range
     * @param end The end of the time range
     * @return Cursor that iterates over the results
     */
    public Cursor queryBookmarksByRange(long musicId, long start, long end);

    /**
     * Any cleanup that might be needed.
     */
    public void close();
}
