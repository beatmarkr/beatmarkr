package com.cs130.beatmarkr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs130.beatmarkr.Bookmark;
import com.cs130.beatmarkr.R;

import java.util.ArrayList;

/**
 * Created by Justin on 5/22/15.
 */
public class SetLoopAdapter extends BaseAdapter {
    private ArrayList<Bookmark> bmList;
    private LayoutInflater bmInflater;
    private Bookmark bm;

    public SetLoopAdapter(Context c, ArrayList<Bookmark> theBms, Bookmark checkBm) {
        bmList = theBms;
        bmInflater = LayoutInflater.from(c);
        bm = checkBm;
    }

    @Override
    public int getCount() {
        return bmList.size();
    }

    @Override
    public Object getItem(int position) {
        return bmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Map to song layout
        LinearLayout bmLayout = (LinearLayout)bmInflater.inflate(R.layout.bookmark_loop, parent, false);

        // Get title and artist views
        TextView descView = (TextView)bmLayout.findViewById(R.id.bm_desc);
        TextView timeView = (TextView)bmLayout.findViewById(R.id.bm_time);

        // Get song using position
        Bookmark currBm = bmList.get(position);

        // Get title and artist strings
        descView.setText(currBm.getDescription());
        timeView.setText(currBm.getSeekTimeString());

        if (currBm.equals(bm)) {
            bmLayout.findViewById(R.id.loop_item).setBackgroundColor(0xFF0EA4C6);
        }
        else {
            bmLayout.findViewById(R.id.loop_item).setBackgroundColor(0x000EA4C6);
        }

        return bmLayout;
    }
}
