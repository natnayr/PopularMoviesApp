package com.natnayr.popularmoviesapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.natnayr.popularmoviesapp.data.MovieVideoContract.MovieEntry;
import com.natnayr.popularmoviesapp.data.MovieVideoContract.VideoEntry;

/**
 * Created by Ryan on 6/8/16.
 */
public class MovieVideoProvider extends ContentProvider {

    private static final String LOG_TAG = MovieVideoProvider.class.getSimpleName();

    private MovieVideoDbHelper mOpenHelper;
    private static final SQLiteQueryBuilder sMovieByIDQueryBuilder;
    private static final SQLiteQueryBuilder sVideosByMovieKeyQueryBuilder;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int MOVIE_ITEM = 101;
    static final int VIDEO = 200;
    static final int VIDEO_BY_MOVIE = 201;

    static{
        sMovieByIDQueryBuilder = new SQLiteQueryBuilder();
        sMovieByIDQueryBuilder.setTables(MovieVideoContract.MovieEntry.TABLE_NAME);

        sVideosByMovieKeyQueryBuilder = new SQLiteQueryBuilder();
        sVideosByMovieKeyQueryBuilder.setTables(MovieVideoContract.VideoEntry.TABLE_NAME);
    }

    //movie by _id in movie table
    private static final String sMovieByIDSelection =
            MovieVideoContract.MovieEntry.TABLE_NAME +
                    "." + MovieVideoContract.MovieEntry._ID + " = ? ";

    //videos by movie_id in video table
    private static final String sVideoByMovieKeySelection =
            MovieVideoContract.VideoEntry.TABLE_NAME +
                    "." + MovieVideoContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ";




    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieVideoContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieVideoContract.PATH_MOVIE + "/#", MOVIE_ITEM);
        matcher.addURI(authority, MovieVideoContract.PATH_VIDEO + "/#", VIDEO_BY_MOVIE);
        matcher.addURI(authority, MovieVideoContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieVideoContract.PATH_VIDEO, VIDEO);

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
            case MOVIE_ITEM:{
                String movieKey = MovieVideoContract.getMovieKeyFromUri(uri);

                retCursor = sMovieByIDQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sMovieByIDSelection,
                        new String[]{ movieKey },
                        null,
                        null,
                        null
                );

                break;
            }
            case VIDEO_BY_MOVIE:{
                String movieKey = MovieVideoContract.getMovieKeyFromUri(uri);

                retCursor = sVideosByMovieKeyQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sVideoByMovieKeySelection,
                        new String[]{ movieKey },
                        null,
                        null,
                        null
                );
                break;
            }
            case MOVIE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEO:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
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
                return MovieVideoContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM:
                return MovieVideoContract.MovieEntry.CONTENT_ITEM_TYPE;
            case VIDEO:
                return MovieVideoContract.VideoEntry.CONTENT_TYPE;
            case VIDEO_BY_MOVIE:
                return MovieVideoContract.VideoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case MOVIE:{
                long _id = db.insert(MovieVideoContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieVideoContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEO:{
                values.toString();
                long _id = db.insert(MovieVideoContract.VideoEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieVideoContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
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

    private void normalizeDate(ContentValues values){
        if(values.containsKey(MovieEntry.COLUMN_RELEASE_DATE)){
            long date = values.getAsLong(MovieEntry.COLUMN_RELEASE_DATE);
//            values.put();
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch(match){
            case MOVIE:
                rowsUpdated = db.update(
                        MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case VIDEO:
                rowsUpdated = db.update(
                        VideoEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
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
