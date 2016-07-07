package com.natnayr.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
import java.util.Arrays;

/**
 * Created by Ryan on 4/7/16.
 */
public class MainActivityFragment extends Fragment{

    private ImageAdapter mImages;

    public MainActivityFragment(){
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mImages = new ImageAdapter(getActivity());


        GridView gridView = (GridView) view.findViewById(R.id.gridview_movies);
        if(gridView == null){
            return null;
        }
        gridView.setAdapter(mImages);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovies("1");
    }

    public void fetchMovies(String page){
        FetchMovieTask fmt = new FetchMovieTask();
        fmt.execute(page);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException{

            final String TMDB_RESULT = "results";

            final String TMDB_ID = "id";
            final String TMDB_TITLE = "title";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_VOTE_AVG = "vote_average";



            JSONObject moviesJsonObj = new JSONObject(movieJsonStr);
            JSONArray resultArray = moviesJsonObj.getJSONArray(TMDB_RESULT);

            Movie[] moviesArr = new Movie[resultArray.length()];

            for(int i=0; i<resultArray.length(); i++){
                JSONObject entry = resultArray.getJSONObject(i);
                moviesArr[i] = new Movie(entry.getLong(TMDB_ID), entry.getString(TMDB_TITLE), entry.getString(TMDB_OVERVIEW),
                        entry.getString(TMDB_POSTER_PATH), entry.getString(TMDB_RELEASE_DATE), entry.getString(TMDB_VOTE_AVG));

            }

            return moviesArr;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {

                final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APIKEY_PARAM = "api_key";
                final String PAGE_PARAM = "page";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, "popularity.desc")
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MyTheMovieDataBaseAPIKey)
                        .appendQueryParameter(PAGE_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }

                //append to buffer read-in lines
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                //return empty
                if(buffer.length() == 0){
                    return null;
                }
                moviesJsonStr = buffer.toString();

                Log.v(LOG_TAG, moviesJsonStr);

            }catch(IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }

                if(reader != null) {
                    try {
                        reader.close();
                    }catch(final IOException e){
                        Log.e(LOG_TAG, "Error closing stream ", e);
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
        protected void onPostExecute(Movie[] movies) {
            if(movies != null){
                mImages.addAll(Arrays.asList(movies));
            }
        }
    }

}
