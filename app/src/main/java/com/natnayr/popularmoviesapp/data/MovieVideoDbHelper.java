package com.natnayr.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.natnayr.popularmoviesapp.data.MovieVideoContract.MovieEntry;
import com.natnayr.popularmoviesapp.data.MovieVideoContract.VideoEntry;

/**
 * Created by Ryan on 3/8/16.
 */
public class MovieVideoDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieVideoDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "movies.db";

    public MovieVideoDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                //will be movie_id
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +

                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_RUNTIME + " INTEGER, " +
                MovieEntry.COLUMN_IMDB_ID + " TEXT, " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +

                //is_favourite checkbox
                MovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0 NOT NULL);";


        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                //reference to movie entry
                VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                VideoEntry.COLUMN_NAME + " TEXT, " +
                VideoEntry.COLUMN_KEY + " TEXT UNIQUE, " +
                VideoEntry.COLUMN_SIZE + " INTEGER, " +
                VideoEntry.COLUMN_SITE + " TEXT, " +
                VideoEntry.COLUMN_VIDEO_TYPE + " TEXT, " +

                //hex video id by TMDB
                VideoEntry.COLUMN_TMDB_VIDEO_ID + " TEXT UNIQUE, " +

                // Set up the movie column as a foreign key to video table.
                " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "), " +

                " PRIMARY KEY ( " + VideoEntry.COLUMN_MOVIE_ID + ", " + VideoEntry.COLUMN_KEY + " )); ";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
