package com.cs130.beatmarkr;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class BookmarksActivity extends Activity {

    private ArrayList<Bookmark> bmList;
    private BookmarkAdapter bmAdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        // Dummy bookmarks
        ListView bmListView = (ListView)findViewById(R.id.bm_list);
        bmList = new ArrayList<Bookmark>();

        bmList.add(new Bookmark(0,0,"Start"));
        bmList.add(new Bookmark(0,0,"End"));

        bmAdt = new BookmarkAdapter(this, bmList);
        bmListView.setAdapter(bmAdt);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editBookmark(View view) {
        DialogFragment editDialog = new EditDialog();
        editDialog.show(getFragmentManager(), "edit");
    }

    public void play(View view) {
        // TODO: play song
    }

    public void pause(View view) {
        // TODO: pause song
    }

    public void forward(View view) {
        // TODO: fast forward song
    }

    public void rewind(View view) {
        // TODO: rewind song
    }

    public void newBookm(View view) {
        // TODO: add new bookmark
    }
}
