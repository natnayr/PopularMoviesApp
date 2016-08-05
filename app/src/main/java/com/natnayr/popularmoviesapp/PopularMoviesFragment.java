package com.natnayr.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.natnayr.popularmoviesapp.model.Movie;

import java.util.Collection;

/**
 * Created by Ryan on 4/7/16.
 */
public class PopularMoviesFragment extends Fragment{

    private static final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();

    private ImageAdapter mImages;
    private int mPagesLoaded = 0; //TODO: store in saved instance, ForecastFragment
    private boolean mIsLoading = false; //TODO: store in saved instance, ForecastFragment
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int MAX_PAGES = 100;

    public PopularMoviesFragment(){
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mImages = new ImageAdapter(getActivity());

        initGridView(view);

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        startLoading();

        return view;
    }

    private void initGridView(View view){

        GridView gridView = (GridView) view.findViewById(R.id.gridview_movies);

        if(gridView == null){
            return;
        }

        gridView.setAdapter(mImages);

        //Set on-touch-listener to grid item.
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long l) {

                ImageAdapter adapter = (ImageAdapter) parent.getAdapter();
                Movie movie = adapter.getItem(position);

                if(movie == null){
                    return;
                }

                //Intent fires up activity and passes Bundle of info..
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Movie.MOVIE_EXTRA, movie.toBundle());
                getActivity().startActivity(intent);

                mPosition = position;
            }
        });


        gridView.setOnScrollListener(
                new AbsListView.OnScrollListener(){

                    @Override
                    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItem, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItem;
                        if(totalItemCount == lastInScreen){
                            startLoading();

                            Log.v(LOG_TAG, "TEST: first:" + firstVisibleItem + ", visible:" + visibleItem + ", total:" + totalItemCount);
                        }
                    }

                    //for implenting funtionality during scrolling/idle
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {

                    }
                }
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void startLoading(){

        if(mIsLoading){
            return;
        }

        if(mPagesLoaded >= MAX_PAGES) {
            return;
        }

        mIsLoading = true;

        FetchMovieTask fmt = new FetchMovieTask(new FragmentCallback() {
            @Override
            public void onTaskDone(Collection<Movie> movies) {
                mPagesLoaded++;
                stopLoading();
                if(movies != null) {
                    mImages.addAll(movies);
                }


            }
        });

        fmt.execute(Integer.toString(mPagesLoaded + 1));

        Toast.makeText(getContext(), R.string.loading_text,
                Toast.LENGTH_SHORT).show();
    }

    public void stopLoading(){
        if(!mIsLoading){
            return;
        }

        mIsLoading = false;
    }



}
