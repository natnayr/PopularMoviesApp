package com.natnayr.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.natnayr.popularmoviesapp.data.MovieVideoContract.MovieEntry;
import com.natnayr.popularmoviesapp.data.MovieVideoContract.VideoEntry;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase(){
        mContext.deleteDatabase(MovieVideoDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
        super.setUp();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieVideoContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieVideoContract.VideoEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieVideoDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieVideoDbHelper(
                this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());


        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without both movie and videos entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieEntry._ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieEntry.COLUMN_RUNTIME);
        movieColumnHashSet.add(MovieEntry.COLUMN_IMDB_ID);
        movieColumnHashSet.add(MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        movieColumnHashSet.add(MovieEntry.COLUMN_IS_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + VideoEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> videoColumnHashSet = new HashSet<String>();
        videoColumnHashSet.add(VideoEntry.COLUMN_NAME);
        videoColumnHashSet.add(VideoEntry.COLUMN_MOVIE_ID);
        videoColumnHashSet.add(VideoEntry.COLUMN_KEY);
        videoColumnHashSet.add(VideoEntry.COLUMN_SIZE);
        videoColumnHashSet.add(VideoEntry.COLUMN_SITE);
        videoColumnHashSet.add(VideoEntry.COLUMN_VIDEO_TYPE);
        videoColumnHashSet.add(VideoEntry.COLUMN_TMDB_VIDEO_ID);


        columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            videoColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required video entry columns",
                videoColumnHashSet.isEmpty());

        db.close();
    }

    public void testMovieTable(){
        insertMovie();
    }

    public void testVideoTable(){
        long movieRowId = insertMovie();

        assertFalse("Error: Movie not inserted correctly", movieRowId == -1);

        MovieVideoDbHelper dbHelper = new MovieVideoDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues videoValues = TestUtilities.createStarTrekVideosValues1(movieRowId);

        long videoRowId = db.insert(VideoEntry.TABLE_NAME, null, videoValues);

        Cursor videoCursor  = db.query(
                VideoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );


        assertTrue(videoRowId != -1);

        assertTrue( "Error: No Records returned from location query", videoCursor.moveToFirst() );

        videoCursor.close();
        db.close();
    }

    public long insertMovie(){
        MovieVideoDbHelper dbHelper = new MovieVideoDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createStarTrekMovieValues();

        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);
        assertTrue(movieRowId != -1);

        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from movie query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Error", cursor, testValues);

        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return movieRowId;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
