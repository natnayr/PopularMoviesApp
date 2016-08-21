package com.natnayr.popularmoviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Ryan on 3/8/16.
 */
public class MovieVideoContract {

    //name of entire content provider
    public static final String CONTENT_AUTHORITY = "com.natnayr.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        //Get Single Movie details
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        //key for tmdb post image
        public static final String COLUMN_POSTER_PATH = "poster_path";

        //key for tmdb backdrop image, used in overlay in details
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_RUNTIME = "runtime";

        //IMDB rating to show
        public static final String COLUMN_IMDB_ID = "imdb_key";

        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";

        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }



    }

    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        //Get list of videos available
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "video";

        //Column pointing to foreign key in movie table
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //Title on online video
        public static final String COLUMN_NAME = "video_name";

        public static final String COLUMN_KEY = "video_key";

        public static final String COLUMN_SIZE = "video_size";

        //Youtube, etc
        public static final String COLUMN_SITE = "video_site";

        public static final String COLUMN_VIDEO_TYPE = "video_type";

        //TMDB video hex
        public static final String COLUMN_TMDB_VIDEO_ID = "tmdb_key";

        public static Uri buildVideoUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static String getMovieKeyFromUri(Uri uri){
        return uri.getPathSegments().get(1);
    }
}
