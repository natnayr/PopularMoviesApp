package com.natnayr.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
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

        Log.v(LOG_TAG, "Test: " + MovieVideoContract.MovieEntry.CONTENT_URI);

        long testMovie = 297761;

        String type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieUri(testMovie));

        assertEquals("Error: MovieEntry CONTENT_URI with movie key should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);


        type = mContext.getContentResolver().getType(
                VideoEntry.buildVideoWithMovieKey(testMovie));

        assertEquals("Error: VideoEntry CONTENT_URI with movie key should return MovieEntry.CONTENT_TYPE",
                VideoEntry.CONTENT_TYPE, type);

    }

    public void testBasicMovieQuery(){

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

}
