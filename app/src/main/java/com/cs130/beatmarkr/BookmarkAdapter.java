package com.cs130.beatmarkr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by caitlin on 5/7/2015.
 */
public class BookmarkAdapter extends BaseAdapter {

    private ArrayList<Bookmark> bmList;
    private LayoutInflater bmInflater;

    public BookmarkAdapter(Context c, ArrayList<Bookmark> theBms) {
        bmList = theBms;
        bmInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return bmList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return bmList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Map to song layout
        LinearLayout bmLayout = (LinearLayout)bmInflater.inflate(R.layout.bookmark, parent, false);

        // Get title and artist views
        TextView descView = (TextView)bmLayout.findViewById(R.id.bm_desc);
        TextView timeView = (TextView)bmLayout.findViewById(R.id.bm_time);

        // Get song using position
        Bookmark currBm = bmList.get(position);

        // Get title and artist strings
        descView.setText(currBm.getDescription());
        timeView.setText(currBm.getSeekTimeString());

        // Set position as tag
        bmLayout.setTag(position);

        return bmLayout;
    }
}