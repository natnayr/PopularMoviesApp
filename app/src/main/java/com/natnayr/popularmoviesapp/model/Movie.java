package com.natnayr.popularmoviesapp.model;

import android.net.Uri;

/**
 * Created by Ryan on 7/7/16.
 */
public class Movie {

    public final long id;
    public final String title;
    public final String poster_path;
    public final String overview;
    public final String release_date;
    public final String vote_average;

    public Movie(long id, String title, String overview, String posterpath,
                 String release_date, String vote_average){
        this.id = id;
        this.title = title;
        this.poster_path = posterpath;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_average = vote_average;
    }

    public Uri buildPosterUri(String size) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(poster_path)
                .build();

        return builtUri;
    }

}
