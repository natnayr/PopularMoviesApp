package com.natnayr.popularmoviesapp.data;

import android.content.ContentValues;
import android.test.AndroidTestCase;

/**
 * Created by Ryan on 11/8/16.
 */
public class TestUtilities extends AndroidTestCase {

    static final long TEST_MOVIE_KEY = 188927;
    static final long TEST_DATE = 1419033600L;

    static ContentValues createMovieValues(){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieVideoContract.MovieEntry._ID, TEST_MOVIE_KEY);
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Star Trek Beyond");
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_POSTER_PATH, "\\/AoT2YrJUJlg5vKE3iMOLvHlTd3m.jpg");
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_BACKDROP_PATH, "\\/gWl5pN2FplE709aVtA4lakwsE6t.jpg");
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_VOTE_AVERAGE, 6.2);
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_OVERVIEW, "The USS Enterprise crew explores the furthest " +
                "reaches of uncharted space, where they encounter a mysterious new enemy who puts them and everything " +
                "the Federation stands for to the test.");
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_RUNTIME, 120);
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_IMDB_ID, "tt2660888");
        movieValues.put(MovieVideoContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "en");

        return movieValues;
    }



    static ContentValues createVideosValues1(){

        String TMDB_VIDEO_KEY = "571bf094c3a368525f006b86";
        String YOUTUBE_VIDEO_KEY = "XRVD32rnzOw";

        ContentValues videosValues = new ContentValues();
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_MOVIE_ID, TEST_MOVIE_KEY);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_NAME, "Official Trailer");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_KEY, YOUTUBE_VIDEO_KEY);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_SIZE, 1080);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_SITE, "YouTube");
        videosValues.put(MovieVideoContract.VideoEntry.CONTENT_TYPE, "Trailer");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_TMDB_VIDEO_ID, TMDB_VIDEO_KEY);

        return videosValues;
    }
}
