package com.natnayr.popularmoviesapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.natnayr.popularmoviesapp.data.MovieVideoContract.MovieEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

/**
 * Created by Ryan on 24/8/16.
 */
public class FetchDetailsTask extends AsyncTask<Long, Void, Void>{

    private static final String LOG_TAG = FetchDetailsTask.class.getSimpleName();
    private FragmentCallback mFragmentCallback;
    private Context mContext;

    public FetchDetailsTask(Context context){
        mContext = context;
    }


    private void getMovieDataFromJson(String detailsJsonStr, long movidId)
            throws JSONException{

        final String TMDB_TITLE = "original_title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_BACKDROP_PATH = "backdrop_path";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_RUNTIME = "runtime";
        final String TMDB_IMDB_ID = "imdb_id";
        final String TMDB_ORIGINAL_LANGUAGE = "original_language";

        JSONObject detailsJson = new JSONObject(detailsJsonStr);

        String originalTitle = detailsJson.getString(TMDB_TITLE);
        String posterPath = detailsJson.getString(TMDB_POSTER_PATH);
        String backdropPath = detailsJson.getString(TMDB_BACKDROP_PATH);
        double voteAverage = detailsJson.getDouble(TMDB_VOTE_AVERAGE);
        String overview = detailsJson.getString(TMDB_OVERVIEW);
        String releaseDate = detailsJson.getString(TMDB_RELEASE_DATE);
        int runtime = detailsJson.getInt(TMDB_RUNTIME);
        String imdbId = detailsJson.getString(TMDB_IMDB_ID);
        String originalLanguage = detailsJson.getString(TMDB_ORIGINAL_LANGUAGE);

        //change release date to long
        long milliesDate = -1;
        try{
            milliesDate = Utilities.formatDateToMills(releaseDate);
            if(milliesDate == -1){
                Log.e(LOG_TAG, "Error: JSON release date is -1.");
            }
        }catch (ParseException e){
            Log.e(LOG_TAG, "Error: Parse of date error.", e);
        }

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry._ID, movidId);
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, milliesDate);
        movieValues.put(MovieEntry.COLUMN_RUNTIME, runtime);
        movieValues.put(MovieEntry.COLUMN_IMDB_ID, imdbId);
        movieValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);


        if(movidId != -1) {
            Uri returnUri = mContext.getContentResolver().insert(
                    MovieEntry.CONTENT_URI, movieValues);

            if(movidId != ContentUris.parseId(returnUri)) {
                Log.e(LOG_TAG, "Error: Movie ID does not match [" + ContentUris.parseId(returnUri) + "]");
            }
        }
    }

    @Override
    protected Void doInBackground(Long... longs) {
        long movieid = -1;
        if(longs.length == 0){
            return null;
        }

        Log.v(LOG_TAG, "TEST: MovidID " + longs[0]);
         movieid = longs[0];

        if(movieid == -1){
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String detailsJsonStr = null;

        try{
            final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APIKEY_PARAM = "api_key";

            Uri buildUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                    .appendPath(Long.toString(movieid))
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.MyTheMovieDataBaseAPIKey)
                    .build();

            URL url = new URL(buildUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line+"\n");
            }

            if(buffer.length() == 0){
                return null; //empty buffer, empty result
            }

            detailsJsonStr = buffer.toString();
            getMovieDataFromJson(detailsJsonStr, movieid);

        }catch(IOException e){
            Log.e(LOG_TAG, "Error IO", e);
            e.printStackTrace();
        }catch(JSONException e){
            Log.e(LOG_TAG, "Error JSON: ", e);
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

            if(reader != null){
                try{
                    reader.close();
                }catch (final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
