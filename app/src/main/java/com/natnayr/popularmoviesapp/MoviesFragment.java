package com.natnayr.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ryan on 4/7/16.
 */

public class MoviesFragment extends Fragment{

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private ImageAdapter mImages;
    private int mPagesLoaded = 0;
    private boolean mIsLoading = false;

    private static final String LAST_PAGE_KEY = "last_page";
    private static final String MOVIE_LIST_KEY = "movie_list";
    private static final int MAX_PAGES = 100;

    public MoviesFragment(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mImages = new ImageAdapter(getActivity());

        initGridView(view);

        if(savedInstanceState != null){
            //restore ImageAdapter of images
            if(savedInstanceState.containsKey(MOVIE_LIST_KEY)) {
                ArrayList<Movie> movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
                mImages.addAll(movieList);
            }
            if(savedInstanceState.containsKey(LAST_PAGE_KEY)){
                mPagesLoaded = savedInstanceState.getInt(LAST_PAGE_KEY);
            }
        }else{
            startLoading();
        }


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
                    Log.e(LOG_TAG, "Error: movie selected is null");
                    return;
                }

                //Intent fires up activity and passes Bundle of info..
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Movie.MOVIE_EXTRA, movie);
                getActivity().startActivity(intent);
            }
        });

        gridView.setOnScrollListener(
                new AbsListView.OnScrollListener(){

                    @Override
                    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItem, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItem;
                        //hack, everytime activity refreshes, this gets called
                        if(totalItemCount == lastInScreen && mPagesLoaded > 0){
                            startLoading();
                        }
                    }

                    //for implenting funtionality during scrolling/idle
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) { }
                }
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //store onto saved bundle entrie Movie arraylist
        ArrayList<Movie> loadedMovies = mImages.getValues();
        outState.putParcelableArrayList(MOVIE_LIST_KEY, loadedMovies);

        //store page number
        outState.putInt(LAST_PAGE_KEY, mPagesLoaded);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            public void onTaskDone(Object object) {
                Collection<Movie> movies = (Collection<Movie>) object;
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
