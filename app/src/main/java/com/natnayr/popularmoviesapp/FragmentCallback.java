package com.natnayr.popularmoviesapp;

import com.natnayr.popularmoviesapp.model.Movie;

import java.util.Collection;

/**
 * Created by Ryan on 4/8/16.
 */
public interface FragmentCallback {

    //overide callback functionality when Asynctask ends
    public void onTaskDone(Collection<Movie> movies);

}
