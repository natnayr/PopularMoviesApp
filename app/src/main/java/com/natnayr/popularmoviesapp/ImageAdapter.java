package com.natnayr.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.natnayr.popularmoviesapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private final ArrayList<Movie> mMovies;

    private static final String LOG_TAG =
            ImageAdapter.class.getSimpleName();

    public ImageAdapter(Context c) {
        mContext = c;
        mMovies = new ArrayList<Movie>();
    }

    public void addAll(Collection<Movie> all){
        mMovies.addAll(all);
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getValues(){
        return mMovies;
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        if(position < 0 || position >= mMovies.size()) {
            return null;
        }
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        Movie movie = mMovies.get(position);
        if(movie == null){
            return -1L;
        }
        return movie.movieid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);
        if(movie == null) {
            return null;
        }

        ImageView imageView;

        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else{
            imageView = (ImageView) convertView;
        }

        Uri builtUri = Utilities.buildPosterUri(mContext.getString(R.string.api_poster_default_size), movie.poster_path);

        Log.v(LOG_TAG, "url: " + builtUri.toString());


        Picasso.with(mContext)
                .load(builtUri)
                .into(imageView);


        return imageView;
    }
}
