package com.natnayr.popularmoviesapp;

import android.net.Uri;

/**
 * Created by Ryan on 3/8/16.
 */
public class Utilities {

    public static Uri buildPosterUri(String size, String image_path) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(image_path)
                .build();
        return builtUri;
    }

}
