package com.natnayr.popularmoviesapp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Ryan on 17/8/16.
 */
public class TestUriMatcher extends AndroidTestCase{

    private static final long MOVIE_ID = 188927;

    private static final Uri TEST_MOVIE_ITEM = MovieVideoContract.MovieEntry.buildMovieUri(MOVIE_ID);
    private static final Uri TEST_MOVIE_ALL = MovieVideoContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_VIDEO_BY_MOVIE = MovieVideoContract.VideoEntry.buildVideoUri(MOVIE_ID);
    private static final Uri TEST_VIDEO_ALL = MovieVideoContract.VideoEntry.CONTENT_URI;

    public void testUriMatcher(){
        UriMatcher testMatcher = MovieVideoProvider.buildUriMatcher();

        assertEquals("Error: uri MOVIE_ITEM matched incorrectly",
                testMatcher.match(TEST_MOVIE_ITEM), MovieVideoProvider.MOVIE_ITEM);
        assertEquals("Error: uri MOVIE matched incorrectly",
                testMatcher.match(TEST_MOVIE_ALL), MovieVideoProvider.MOVIE);
        assertEquals("Error: uri VIDEO_BY_MOVIE matched incorrectly",
                testMatcher.match(TEST_VIDEO_BY_MOVIE), MovieVideoProvider.VIDEO_BY_MOVIE);
        assertEquals("Error: uri VIDEO matched incorrectly",
                testMatcher.match(TEST_VIDEO_ALL), MovieVideoProvider.VIDEO);

    }

}
