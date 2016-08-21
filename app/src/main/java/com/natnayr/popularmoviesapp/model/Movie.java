package com.natnayr.popularmoviesapp.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ryan on 7/7/16.
 */
public class Movie implements Parcelable {

    public static final String MOVIE_EXTRA = "com.natnayr.popularmoviesapp.MOVIE_EXTRA";
    public static final String TMDB_RESULTS = "results";
    public static final String TMDB_MOVIE_ID = "id";
    public static final String TMDB_POSTER_PATH = "poster_path";
    public static final String TMDB_ORIGINAL_TITLE = "original_title";
    public static final String TMDB_USER_RATING = "vote_average";


    public long movieid;
    public String title;
    public String poster_path;
    public double vote_average;

    public Movie(long movieid, String title, String poster_path, double vote_average){
        this.movieid = movieid;
        this.title = title;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
    }

    public Movie (Bundle bundle){
        this(
                bundle.getLong(TMDB_MOVIE_ID),
                bundle.getString(TMDB_ORIGINAL_TITLE),
                bundle.getString(TMDB_POSTER_PATH),
                bundle.getDouble(TMDB_USER_RATING)
        );
    }

    private Movie(Parcel parcel){
        this.movieid = parcel.readLong();
        this.title = parcel.readString();
        this.poster_path = parcel.readString();
        this.vote_average = parcel.readDouble();
    }


    public static Movie fromJson(JSONObject jsonObject) throws JSONException{
        return new Movie(
                jsonObject.getLong(TMDB_MOVIE_ID),
                jsonObject.getString(TMDB_ORIGINAL_TITLE),
                jsonObject.getString(TMDB_POSTER_PATH),
                jsonObject.getDouble(TMDB_USER_RATING)
        );
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putLong(TMDB_MOVIE_ID, movieid);
        bundle.putString(TMDB_ORIGINAL_TITLE, title);
        bundle.putString(TMDB_POSTER_PATH, poster_path);
        bundle.putDouble(TMDB_USER_RATING, vote_average);

        return bundle;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.movieid);
        parcel.writeString(this.title);
        parcel.writeString(this.poster_path);
        parcel.writeDouble(this.vote_average);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
