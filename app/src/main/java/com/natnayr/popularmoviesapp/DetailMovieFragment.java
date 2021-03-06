package com.natnayr.popularmoviesapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.natnayr.popularmoviesapp.data.MovieVideoContract.MovieEntry;
import com.natnayr.popularmoviesapp.model.Movie;

/**
 * Created by Ryan on 7/7/16.
 */
public class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailMovieFragment.class.getSimpleName();
    static final String DETAIL_MOVIE_BUNDLE = "MOVIE_BUNDLE";

    private Movie mParsedMovie;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RUNTIME,
            MovieEntry.COLUMN_IMDB_ID,
            MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieEntry.COLUMN_IS_FAVORITE
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_ORIGINAL_TITLE = 1;
    public static final int COL_MOVIE_RELEASE_DATE = 2;
    public static final int COL_MOVIE_POSTER_PATH = 3;
    public static final int COL_MOVIE_BACKDROP_PATH = 4;
    public static final int COL_MOVIE_VOTE_AVERAGE = 5;
    public static final int COL_MOVIE_OVERVIEW = 6;
    public static final int COL_MOVIE_RUNTIME = 7;
    public static final int COL_MOVIE_IMDB_ID = 8;
    public static final int COL_MOVIE_ORIGINAL_LANGUAGE = 9;
    public static final int COL_MOVIE_IS_FAVORITE = 10;

    private TextView mTitleView;
    private ImageView mPosterView;
    private TextView mRunTimeView;
    private TextView mMovieRatingView;
    private TextView mReleaseDateView;
    private TextView mOverviewView;

    public DetailMovieFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguements = getArguments();
        if(arguements != null && arguements.containsKey(Movie.MOVIE_EXTRA)){
            mParsedMovie = (Movie) arguements.getParcelable(Movie.MOVIE_EXTRA);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mTitleView  = (TextView) rootView.findViewById(R.id.movie_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_poster);
        mRunTimeView = (TextView) rootView.findViewById(R.id.movie_runtime);
        mMovieRatingView = (TextView) rootView.findViewById(R.id.movie_rating);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.movie_release_date);
        mOverviewView = (TextView) rootView.findViewById(R.id.movie_overview);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mParsedMovie != null ){
            //Checks if data exists, retreives and insert if not
            if(!Utilities.checkIfMovieRecordExists(getActivity(), mParsedMovie.movieid)) {
                //insert new record in via FetchDetailMovieTask
                FetchDetailsTask fdt = new FetchDetailsTask(getActivity());
                long movieid = mParsedMovie.movieid;
                fdt.execute(movieid);
            }

            return new CursorLoader(
                    getActivity(),
                    MovieEntry.buildMovieUri(mParsedMovie.movieid),
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            String title = data.getString(COL_MOVIE_ORIGINAL_TITLE);
            mTitleView.setText(title);

            long runtime = data.getLong(COL_MOVIE_RUNTIME);
            mRunTimeView.setText(Long.toString(runtime));

            double rating = data.getDouble(COL_MOVIE_VOTE_AVERAGE);
            mMovieRatingView.setText(Double.toString(rating));

            long dateInMills = data.getLong(COL_MOVIE_RELEASE_DATE);
            mReleaseDateView.setText(Long.toString(dateInMills));

//            Uri posterUri = movie.buildPosterUri(getString(R.string.api_poster_default_size));
//            Picasso.with(this)
//                    .load(posterUri)
//                    .into((ImageView)findViewById(R.id.movie_poster));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
