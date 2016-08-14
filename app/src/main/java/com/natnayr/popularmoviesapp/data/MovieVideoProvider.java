package com.natnayr.popularmoviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Ryan on 6/8/16.
 */
public class MovieVideoProvider extends ContentProvider {

    private MovieVideoDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sMovieByIDQueryBuilder;
    private static final SQLiteQueryBuilder sVideosByMovieKeyQueryBuilder;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int VIDEO = 200;

    static{
        sMovieByIDQueryBuilder = new SQLiteQueryBuilder();
        sMovieByIDQueryBuilder.setTables(MovieVideoContract.MovieEntry.TABLE_NAME);

        sVideosByMovieKeyQueryBuilder = new SQLiteQueryBuilder();
        sVideosByMovieKeyQueryBuilder.setTables(MovieVideoContract.VideoEntry.TABLE_NAME);
    }

    //movie
    private static final String sMovieByIDSelection =
            MovieVideoContract.MovieEntry.TABLE_NAME +
                    "." + MovieVideoContract.MovieEntry._ID + " = ? ";

    //videos
    private static final String sVideoByMovieKeySelection =
            MovieVideoContract.VideoEntry.TABLE_NAME +
                    "." + MovieVideoContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ";


    private Cursor getMovieByKey(Uri uri, String[] projection){
        String movieKey = MovieVideoContract.getMovieKeyFromUri(uri);

        String[] selectionArgs = new String[]{ movieKey };
        String selection = sMovieByIDSelection;

        return sMovieByIDQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private Cursor getVideosByKey(Uri uri, String[] projection){
        String movieKey = MovieVideoContract.getMovieKeyFromUri(uri);

        String[] selectionArgs = new String[]{ movieKey };
        String selection = sVideoByMovieKeySelection;

        return sVideosByMovieKeyQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieVideoContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieVideoContract.PATH_MOVIE + "/#", MOVIE);
        matcher.addURI(authority, MovieVideoContract.PATH_VIDEO + "/#", VIDEO);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieVideoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case MOVIE:{
                retCursor = getMovieByKey(uri, projection);
                break;
            }

            case VIDEO:{
                retCursor = getVideosByKey(uri, projection);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match){
            case MOVIE:
                return MovieVideoContract.MovieEntry.CONTENT_ITEM_TYPE;
            case VIDEO:
                return MovieVideoContract.VideoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        //delete all rows
        if( selection == null ) selection = "1";

        switch(match){
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieVideoContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case VIDEO:
                rowsDeleted = db.delete(
                        MovieVideoContract.VideoEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


    // Method specifically to assist the testing framework in running smoothly.
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
