package com.natnayr.popularmoviesapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie>{

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.grid_item_movies, parent, false);
        }

        //ImageView poster = (ImageView) convertView.findViewById(R.id.image_view);
        //poster.setImageResource();

        TextView textView = (TextView) convertView.findViewById(R.id.list_item_text);
        textView.setText(movie.title);

        return convertView;
    }
}
