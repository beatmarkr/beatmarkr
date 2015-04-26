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
 * Created by Alina on 4/26/2015.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInflater;

    public SongAdapter(Context c, ArrayList<Song> theSongs) {
        songs = theSongs;
        songInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Map to song layout
        LinearLayout songLayout = (LinearLayout)songInflater.inflate(R.layout.song, parent, false);

        // Get title and artist views
        TextView songView = (TextView)songLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);

        // Get song using position
        Song currSong = songs.get(position);

        // Get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());

        // Set position as tag
        songLayout.setTag(position);

        return songLayout;
    }
}