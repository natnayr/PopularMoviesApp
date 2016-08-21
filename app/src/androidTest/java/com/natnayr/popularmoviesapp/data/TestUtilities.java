package com.natnayr.popularmoviesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import com.natnayr.popularmoviesapp.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Ryan on 11/8/16.
 */
public class TestUtilities extends AndroidTestCase {

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createStarTrekMovieValues(){

        final long TEST_MOVIE_KEY = 188927;
        final long TEST_DATE = 1419033600L;

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

    public static ContentValues createStarTrekVideosValues1(long testMovieID){

        ContentValues videosValues = new ContentValues();
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_MOVIE_ID, testMovieID);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_NAME, "Official Trailer");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_KEY, "XRVD32rnzOw");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_SIZE, 1080);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_SITE, "YouTube");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_VIDEO_TYPE, "Trailer");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_TMDB_VIDEO_ID, "571bf094c3a368525f006b86");

        return videosValues;
    }

    public static ContentValues createStarTrekVideosValues2(long testMovieID){

        ContentValues videosValues = new ContentValues();
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_MOVIE_ID, testMovieID);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_NAME, "Star Trek Beyond - Official Trailer");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_KEY, "dCyv5xKIqlw");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_SIZE, 720);
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_SITE, "YouTube");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_VIDEO_TYPE, "Trailer");
        videosValues.put(MovieVideoContract.VideoEntry.COLUMN_TMDB_VIDEO_ID, "575a65a5c3a36841880002d9");

        return videosValues;
    }

    public static long insertMovieValues(Context context){
        MovieVideoDbHelper dbHelper = new MovieVideoDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createStarTrekMovieValues();

        long movieRowId = db.insert(MovieVideoContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
