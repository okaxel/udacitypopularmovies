package hu.drorszagkriszaxel.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 *  Popular Movies App Stage 1 and 2
 *  --------------------------------
 *
 *  Copyright (c) Axel Ország-Krisz Dr. 2018.
 *
 *  This application and its parts are made for Google Developer Nanodegree Scholarship 2018.
 *  Thanks for al the knowledge and opportunity to Google and Udacity.
 *
 *  All of my code is licenced under GNU/GPL v3.
 *  Any other part has its own copyrights.
 *
 *  Drawable resources are from openclipart.org. Read more about this in the readme at GitHub.
 *
 *  @author     Axel Ország-Krisz Dr.
 *  @version    Stage 1 - first try
 */

public class DetailsActivity extends AppCompatActivity {


    // Variables to manage layout elements
    Toolbar mToolbar;

    /**
     * Just the usual onCreate method to initalize various things.
     *
     * @param savedInstanceState Essemtial parameter to work with
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mToolbar=findViewById(R.id.tolbar_details);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.key_intent_details));

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvOriginalTitle = findViewById(R.id.tv_original_title);
        TextView tvRelaseDate = findViewById(R.id.tv_value_release_date);
        TextView tvVoteAverage = findViewById(R.id.tv_value_vote_average);
        TextView tvOverview = findViewById(R.id.tv_overview);
        ImageView iPoster = findViewById(R.id.image_poster);

        tvTitle.setText(movie.getTitle());

        if (movie.getTitle().equals(movie.getOriginalTitle())) {
            tvOriginalTitle.setText("");
            tvOriginalTitle.setVisibility(View.INVISIBLE);
            tvOriginalTitle.setHeight(0);
        } else {
            tvOriginalTitle.setText(movie.getOriginalTitle());
        }

        tvRelaseDate.setText(movie.getReleaseDate());
        tvVoteAverage.setText(String.valueOf(movie.getVoteAverage()));
        tvOverview.setText(movie.getOverview());

        String loadPath = getString(R.string.tmdb_image_location_base)
                + getString(R.string.tmdb_image_resolution_path) + movie.getPosterPath();

        Picasso.get()
                .load(loadPath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(iPoster);
    }
}
