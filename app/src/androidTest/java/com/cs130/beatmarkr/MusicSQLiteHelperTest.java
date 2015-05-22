package com.cs130.beatmarkr;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import junit.framework.Assert;

public class MusicSQLiteHelperTest extends AndroidTestCase {

    private MusicSQLiteHelper helper;   //Note that this database is shared across all test cases.
                                        //So try not to use concrete numbers when counting the number
                                        //of entries.
    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        helper = MusicSQLiteHelper.getInstance(context);

    }

    public void tearDown() throws Exception {
        helper.close();
        super.tearDown();
    }

    public void testGetInstance() throws Exception {
        Assert.assertTrue(helper != null);
    }

    public void testAddMusicEntry() throws Exception {
        addTestMusicEntry();
        Cursor cursor = helper.queryMusic(new String[]{});
        cursor.moveToNext();
            Assert.assertEquals(3,cursor.getColumnCount());
            Assert.assertEquals("1",cursor.getString(0));
            Assert.assertEquals("Title",cursor.getString(1));
            Assert.assertEquals("Artist",cursor.getString(2));
        cursor.close();
    }

    public void testDeleteMusicEntry() throws Exception {
        addTestMusicEntry();
        Cursor cursor = helper.queryMusic(new String[]{});
        int numRows = cursor.getCount();
        helper.deleteMusicEntry(1);
        cursor.close();
        cursor = helper.queryMusic(new String[]{});
        Assert.assertEquals(numRows-1,cursor.getCount());
        cursor.close();
    }

    public void testAddBookmarkEntry() throws Exception {
        Cursor cursor = helper.queryBookmarks(new String[]{"1"});
        int numRows = cursor.getCount();
        cursor.close();
        addTestBookmarkEntry();
        cursor = helper.queryBookmarks(new String[]{"1"});
        cursor.moveToNext();
        Assert.assertEquals(3,cursor.getColumnCount());
        Assert.assertEquals("1",cursor.getString(0));
        Assert.assertEquals("200",cursor.getString(1));
        Assert.assertEquals("Desc",cursor.getString(2));
        Assert.assertEquals(numRows+1,cursor.getCount());
        cursor.close();
    }

    public void testUpdateBookmarkEntry() throws Exception {
        addTestBookmarkEntry();
        Cursor cursor = helper.queryBookmarks(new String[]{"1"});
        int numRows = cursor.getCount();
        cursor.close();

        Bookmark old = new Bookmark(1,200,"Desc");
        //update time
        helper.updateBookmarkEntry(old,new Long(201),null);
        cursor = helper.queryBookmarks(new String[]{"1"});
        Assert.assertEquals(numRows,cursor.getCount());
        cursor.moveToNext();
        Assert.assertEquals("201",cursor.getString(1));
        cursor.close();
        //update description
        old = new Bookmark(1,201,"Desc");
        helper.updateBookmarkEntry(old,null,"New Desc");
        cursor = helper.queryBookmarks(new String[]{"1"});
        cursor.moveToNext();
        Assert.assertEquals("New Desc",cursor.getString(2));
        Assert.assertEquals(numRows,cursor.getCount());
        cursor.close();
    }

    public void testDeleteBookmarkEntry() throws Exception {
        addTestBookmarkEntry();
        Cursor cursor = helper.queryBookmarks(new String[]{"1"});
        int numRows = cursor.getCount();
        cursor.close();
        helper.deleteBookmarkEntry(new Bookmark(1, 200, "Desc"));
        cursor = helper.queryBookmarks(new String[]{"1"});
        Assert.assertEquals(numRows-1,cursor.getCount());
        cursor.close();
    }

    public void testDeleteAllBookmarksForSong() throws Exception {
        addTestBookmarkEntry();
        Bookmark bmA = new Bookmark(1, 300, "A");
        Bookmark bmB = new Bookmark(1, 400, "B");
        helper.addBookmarkEntry(bmA);
        helper.addBookmarkEntry(bmB);
        Cursor cursor = helper.queryBookmarks(new String[]{"1"});
        int numRows = cursor.getCount();
        cursor.close();
        helper.deleteAllBookmarksForSong(1);
        cursor = helper.queryBookmarks(new String[]{"1"});
        Assert.assertEquals(numRows-3,cursor.getCount());
        cursor.close();
    }

    public void testQueryMusic() throws Exception {
        addTestMusicEntry();        //1,"Title","Artist"
        helper.addMusicEntry(new Song(2,"The Nyan Cat","Cat Lady"));
        helper.addMusicEntry(new Song(3,"The Dog","Woof"));

        //query by title
        Cursor cursor = helper.queryMusic(new String[]{"th"});
        Assert.assertEquals(2,cursor.getCount());
        cursor.moveToNext();
        Assert.assertTrue(cursor.getString(1).equals("The Dog")); //alphabetical order
        cursor.moveToNext();
        Assert.assertTrue(cursor.getString(1).equals("The Nyan Cat"));
        cursor.close();
        //query by artist
        cursor = helper.queryMusic(new String[]{"woof"});
        cursor.moveToNext();
        Assert.assertTrue(cursor.getString(1).equals("The Dog"));
        cursor.close();
        //query by both
        cursor = helper.queryMusic(new String[]{"the","lady"});
        Assert.assertEquals(1,cursor.getCount());
        cursor.moveToNext();
        Assert.assertTrue(cursor.getString(1).equals("The Nyan Cat"));
        cursor.close();
    }

    public void testQueryBookmarks() throws Exception {
        addTestBookmarkEntry();
        helper.addBookmarkEntry(new Bookmark(1,1000,"First strain"));
        helper.addBookmarkEntry(new Bookmark(1,2000,"Second strain"));
        helper.addBookmarkEntry(new Bookmark(1,3000,"Trio"));
        helper.addBookmarkEntry(new Bookmark(1,4000,"Breakstrain"));
        helper.addBookmarkEntry(new Bookmark(1,5000,"Grandiose"));
        //query all bookmarks for song
        Cursor cursor = helper.queryBookmarks(new String[]{"1"});
        Assert.assertEquals(6,cursor.getCount());
        cursor.close();

        //query by description
        cursor = helper.queryBookmarks(new String[]{"1","strain","first"});
        Assert.assertEquals(1,cursor.getCount());
        cursor.close();
    }

    public void testQueryBookmarksByRange() throws Exception {
        helper.addBookmarkEntry(new Bookmark(1,1000,"First strain"));
        helper.addBookmarkEntry(new Bookmark(1,2000,"Second strain"));
        helper.addBookmarkEntry(new Bookmark(1,3000,"Trio"));
        helper.addBookmarkEntry(new Bookmark(1,4000,"Breakstrain"));
        helper.addBookmarkEntry(new Bookmark(1,5000,"Grandiose"));

        Cursor cursor = helper.queryBookmarksByRange(1,999,3000);
        Assert.assertEquals(3,cursor.getCount());
        cursor.close();
    }

    private void addTestMusicEntry() {
        Song song = new Song(1, "Title", "Artist");
        helper.addMusicEntry(song);
    }

    private void addTestBookmarkEntry() {
        Bookmark bm = new Bookmark(1, 200, "Desc");
        helper.addBookmarkEntry(bm);
    }
}