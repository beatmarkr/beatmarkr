package com.cs130.beatmarkr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alina on 4/26/2015.
 */
public class SongAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Song> songs;
    private LayoutInflater songInflater;
    private ArrayList<Song> filteredSongs;
    private SongFilter songFilter;

    public SongAdapter(Context c, ArrayList<Song> theSongs) {
        songs = theSongs;
        songInflater = LayoutInflater.from(c);
        filteredSongs = theSongs;
        getFilter();
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return filteredSongs.get(arg0);
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

    @Override
    public Filter getFilter() {
        if (songFilter == null) {
            songFilter = new SongFilter();
        }

        return songFilter;
    }

    private class SongFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint!=null && constraint.length()>0) {
                ArrayList<Song> temp = new ArrayList<Song>();

                for(Song song : songs) {
                    if(song.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        temp.add(song);
                    }
                    else if(song.getArtist().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        temp.add(song);
                    }
                }

                results.count = temp.size();
                results.values = temp;
            }
            else {
                results.count = songs.size();
                results.values = songs;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredSongs = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }
    }
}