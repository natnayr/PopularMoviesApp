package com.natnayr.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.natnayr.popularmoviesapp.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by Ryan on 7/7/16.
 */
public class DetailActivity extends AppCompatActivity{

    private final static String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(Movie.MOVIE_EXTRA)){

            Movie movie = new Movie(intent.getBundleExtra(Movie.MOVIE_EXTRA));
            ((TextView)findViewById(R.id.movie_title)).setText(movie.title);
            ((TextView)findViewById(R.id.movie_rating)).setText(Double.toString(movie.vote_average) + "/10");
            ((TextView)findViewById(R.id.movie_release_date)).setText(movie.release_date);
            ((TextView)findViewById(R.id.movie_overview)).setText(movie.overview);

            Uri posterUri = movie.buildPosterUri(getString(R.string.api_poster_default_size));
            Picasso.with(this)
                    .load(posterUri)
                    .into((ImageView)findViewById(R.id.movie_poster));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
