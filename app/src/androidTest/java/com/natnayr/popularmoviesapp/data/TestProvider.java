package com.natnayr.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.natnayr.popularmoviesapp.data.MovieVideoContract.MovieEntry;
import com.natnayr.popularmoviesapp.data.MovieVideoContract.VideoEntry;

/**
 * Created by Ryan on 7/8/16.
 */
public class TestProvider extends AndroidTestCase{

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieVideoProvider.class.getName());

        try{
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: Provider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieVideoContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieVideoContract.CONTENT_AUTHORITY);

        } catch(PackageManager.NameNotFoundException e){
            assertTrue("Error: this class not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType(){

        long testMovie = 297761;

        String type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieUri(testMovie));

        assertEquals("Error: MovieEntry CONTENT_URI with movie key should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(
                VideoEntry.buildVideoUri(testMovie));

        assertEquals("Error: VideoEntry CONTENT_URI with movie key should return MovieEntry.CONTENT_TYPE",
                VideoEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testMovieAndVideoQueries(){
        ContentValues movieValues = TestUtilities.createStarTrekMovieValues();
        Uri moviesUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues);

        Log.d(LOG_TAG, "TEST moviesUri:" + moviesUri);

        long movieRowId = ContentUris.parseId(moviesUri);

        assertTrue("ERROR: Unable to insert into db Movie values", movieRowId != -1);

        Cursor movieCursor = mContext.getContentResolver().query(
                        MovieEntry.buildMovieUri(movieRowId), null, null, null, null);

        assertEquals("Error: Movie data table records not deleted", 1, movieCursor.getCount());

        movieCursor.moveToFirst();

        ContentValues videoValues1 = TestUtilities.createStarTrekVideosValues1(movieRowId);
        String videoKey1 = videoValues1.getAsString(VideoEntry.COLUMN_KEY);
        Uri videoUri1 = mContext.getContentResolver()
                .insert(VideoEntry.CONTENT_URI, videoValues1);

        long videoRowId = ContentUris.parseId(videoUri1);

        Log.d(LOG_TAG, "TEST: 1st videoRowId:"+videoRowId);
        assertTrue("ERROR: Unable to insert into db VIDEO values", videoRowId != -1);

        ContentValues videoValues2 = TestUtilities.createStarTrekVideosValues2(movieRowId);
        String videoKey2 = videoValues2.getAsString(VideoEntry.COLUMN_KEY);
        Uri videoUri2 = mContext.getContentResolver()
                .insert(VideoEntry.CONTENT_URI, videoValues2);

        Log.d(LOG_TAG, "TEST videoURI2:" + videoUri2);

        videoRowId = ContentUris.parseId(videoUri2);

        Log.d(LOG_TAG, "TEST: 2nd videoRowId:"+videoRowId);
        assertTrue("ERROR: Unable to insert into db VIDEO values", videoRowId != -1);


        movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicMovieVideoQuery", movieCursor
                , movieValues);
        movieCursor.close();

        Cursor videoCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                VideoEntry.COLUMN_KEY + " = ?",
                new String[]{ videoKey1 },
                null
        );
        TestUtilities.validateCursor("testBasicMovieVideoQuery", videoCursor, videoValues1);

        videoCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                VideoEntry.COLUMN_KEY + " = ?",
                new String[]{ videoKey2 },
                null
        );
        TestUtilities.validateCursor("testBasicMovieVideoQuery", videoCursor, videoValues2);

        //Now Testing if Uri matches for multiple video fetch VIDEO_BY_MOVIE
        videoCursor = mContext.getContentResolver().query(
                VideoEntry.buildVideoUri(movieRowId), null, null, null, null);

        assertEquals(videoCursor.getCount(), 2);

        videoCursor.close();
    }


    public void testUpdateMovie(){
        //Insert into db movie values
        ContentValues movieValues = TestUtilities.createStarTrekMovieValues();
        Uri movieUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues);

        long movieId = ContentUris.parseId(movieUri);
        assertTrue(movieId != -1);

        ContentValues updatedValues = new ContentValues(movieValues);
        updatedValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Crocodile Movie");
        updatedValues.put(MovieEntry._ID, movieId);


        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID + " = ?",
                new String[]{ Long.toString(movieId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();
        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        //check in db values corresponde to updated
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry._ID + " = " + movieId,
                null,
                null
        );
        assertTrue(cursor.getCount() == 1);
        cursor.moveToFirst();
        TestUtilities.validateCurrentRecord("testUpdatedMovie. Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testUpdateVideo(){
        //insert movie first to get ID
        ContentValues movieValues = TestUtilities.createStarTrekMovieValues();
        Uri movieUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues);
        long movieId = ContentUris.parseId(movieUri);
        assertTrue(movieId != -1);

        //now insert video
        ContentValues videoValues = TestUtilities.createStarTrekVideosValues2(movieId);
        String videoKey = videoValues.getAsString(VideoEntry.COLUMN_KEY);
        Uri videoUri = mContext.getContentResolver()
                .insert(VideoEntry.CONTENT_URI, videoValues);
        long videoId = ContentUris.parseId(videoUri);
        assertTrue(videoId != -1);

        ContentValues updatedValues = new ContentValues(videoValues);
        updatedValues.put(VideoEntry.COLUMN_NAME, "Crocodile Nature Video");
        updatedValues.put(VideoEntry.COLUMN_MOVIE_ID, movieId);
        updatedValues.put(VideoEntry.COLUMN_KEY, videoKey);

        Cursor videoCursor = mContext.getContentResolver().query(VideoEntry.CONTENT_URI, null, null, null, null);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        videoCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                VideoEntry.CONTENT_URI,
                updatedValues,
                VideoEntry.COLUMN_MOVIE_ID + " = ? AND " + VideoEntry.COLUMN_KEY + " = ?",
                new String[]{ Long.toString(movieId), videoKey }
        );

        assertEquals(count, 1);
        tco.waitForNotificationOrFail();
        videoCursor.unregisterContentObserver(tco);
        videoCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                VideoEntry.COLUMN_MOVIE_ID + " = ? AND " + VideoEntry.COLUMN_KEY + " = ?",
                new String[]{ Long.toString(movieId), videoKey },
                null
        );
        assertTrue(cursor.getCount() == 1);
        cursor.moveToFirst();
        TestUtilities.validateCurrentRecord("testUpdateVideo. Error validating video entry update",
                cursor, updatedValues);
        cursor.close();
    }


    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                VideoEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver()
                .query(
                        MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
        assertEquals("Error: Movie data table records not deleted", 0, cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver()
                .query(
                        VideoEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

        assertEquals("Error: Video data table records not deleted", 0, cursor.getCount());
        cursor.close();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
