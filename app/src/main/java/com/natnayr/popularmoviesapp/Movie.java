package com.natnayr.popularmoviesapp;

/**
 * Created by Ryan on 5/7/16.
 */
public class Movie {
    String title;
    String release_date;
    String overview;
    String posterPath;
    String vote_average;

    public Movie(String title, String release_date,
                 String overview, String posterPath, String vote_average){
        this.title = title;
        this.release_date = release_date;
        this.overview = overview;
        this.posterPath = posterPath;
        this.vote_average = vote_average;
    }

}
