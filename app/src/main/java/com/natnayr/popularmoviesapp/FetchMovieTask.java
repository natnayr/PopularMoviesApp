package com.natnayr.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.natnayr.popularmoviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ryan on 2/8/16.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Collection<Movie>>{

    private final static String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private FragmentCallback mFragmentCallback;

    public FetchMovieTask(FragmentCallback fragmentCallback) {
        this.mFragmentCallback = fragmentCallback;
    }

    private Collection<Movie> getMovieDataFromJson(String moviesJsonStr)
        throws JSONException {

        JSONObject rawMoviesObject = new JSONObject(moviesJsonStr);
        JSONArray movieArray = rawMoviesObject.getJSONArray(Movie.TMDB_RESULTS);

        ArrayList<Movie> moviesArr = new ArrayList<Movie>();

        for(int i=0; i<movieArray.length(); i++) {
            JSONObject entry = movieArray.getJSONObject(i);
            moviesArr.add(Movie.fromJson(entry));
        }

        return moviesArr;

    }

    @Override
    protected Collection<Movie> doInBackground(String... params) {

        //current page is needed
        //TODO: add order by POPULAR or RATING to param
        if(params.length == 0)
            return null;

        String pageNum = params[0];
        //TODO: pageNum 1-1000, show alert intent and break if >1000

        Log.v(LOG_TAG, "TEST: PageNum:" + pageNum);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;

        //TODO: setChoice of popular/top_rate/upcoming
        String choice = "popular";

        try {
            final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String PAGE_PARAM = "page";
            final String APIKEY_PARAM = "api_key";

            Uri buildUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                    .appendEncodedPath(choice)
                    .appendQueryParameter(PAGE_PARAM, pageNum)
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
                return null; //failed empty buffer
            }

            moviesJsonStr = buffer.toString();

        }catch(IOException e) {
            Log.e(LOG_TAG, "Error", e);
            //If not parsed, then catch and exit
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

            if(reader != null){
                try{
                    reader.close();
                }catch(final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try{
            return getMovieDataFromJson(moviesJsonStr);
        }catch(JSONException e){
            Log.e(LOG_TAG, "JSON Error: ", e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Collection<Movie> movies) {
        mFragmentCallback.onTaskDone(movies);
    }
}
