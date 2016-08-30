package com.natnayr.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.natnayr.popularmoviesapp.data.MovieVideoContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ryan on 3/8/16.
 */
public class Utilities {

    public static final String LOG_TAG = Utilities.class.getSimpleName();
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Uri buildPosterUri(String size, String image_path) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(image_path)
                .build();
        return builtUri;
    }

    public static long formatDateToMills(String jsonDate) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(Utilities.DATE_FORMAT);
        Date date = sdf.parse(jsonDate);
        return date.getTime();
    }


    public static String formatMillsToDate(long mills){
        return null;
    }

    public static boolean checkIfMovieRecordExists(Context context, long moviId){
        final int COL_MOVIE_ID = 0;

        Cursor cursor = context.getContentResolver().query(
                MovieVideoContract.MovieEntry.buildMovieUri(moviId),
                null,null, null, null
        );
        Log.v(LOG_TAG, "TEST checkIfMovieRecordExists is called");

        if(cursor.moveToFirst()){
            Log.v(LOG_TAG, "TEST: checkIfMovieRecordExists movieId retreived "
                    + cursor.getLong(COL_MOVIE_ID));
            return (cursor.getLong(COL_MOVIE_ID) == moviId);
        }

        return false;
    }
}
