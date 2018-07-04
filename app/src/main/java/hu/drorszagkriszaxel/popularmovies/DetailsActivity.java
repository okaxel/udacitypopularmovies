package hu.drorszagkriszaxel.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import hu.drorszagkriszaxel.popularmovies.datahandling.MoviesContract;

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

    // Reference to the hidden API key
    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;

    // Variables to manage layout elements
    Toolbar mToolbar;
    ImageButton mImageButton;

    // Variables to mnaga the selected movie and its favourite state
    boolean isFavourite = false;
    Movie movie;

    // Our best friend, the Context
    Context mContext;

    /**
     * Just the usual onCreate method to initalize various things.
     *
     * @param savedInstanceState Essemtial parameter to work with
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mContext = getApplicationContext();

        mToolbar=findViewById(R.id.tolbar_details);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mImageButton=findViewById(R.id.imagebutton_favourite_change);
        mImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                isFavourite = ! view.isSelected();
                view.setSelected(isFavourite);
                saveFavouriteState();
                setImageButtonState();

            }
        });

        Intent intent = getIntent();
        movie = intent.getParcelableExtra(getString(R.string.key_intent_details));

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

        getFavouriteState();
        setImageButtonState();

        loadTrailersReviews();

    }

    /**
     * This function checks the favorite selection state of the movie. It doesn't have input and
     * output variables because it uses the concerning activity-level variables.
     */
    private void getFavouriteState() {

        isFavourite = false;

        String[] projection = {
                MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID
        };

        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,
                MoviesContract.PATH_FAVOURITES + "/" + Integer.toString(movie.getId())),
                null, null, null, null);
        if (cursor!=null) {

            if (cursor.getCount()>0) {

                isFavourite = true;

            }

            cursor.close();
        }

    }

    /**
     * Saves the favourite selection state of the actual movie via ContentProvider.
     */
    private void saveFavouriteState() {

        if (isFavourite) {

            ContentValues values = new ContentValues();
            values.put(MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID,movie.getId());
            Uri insertedRow = getContentResolver().insert(Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,
                    MoviesContract.PATH_FAVOURITES),values);

        } else {

            int deletedRows = getContentResolver().delete(Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,
                    MoviesContract.PATH_FAVOURITES + "/" + Integer.toString(movie.getId())),
                    null,null);

        }

    }

    /**
     * This function sets the sate of the ImageButton depending on the favourite selection state of
     * the actual movie.
     */
    private void setImageButtonState() {
        if (isFavourite) {
            mImageButton.setImageResource(R.drawable.star_pressed);
        } else {
            mImageButton.setImageResource(R.drawable.star_default);
        }
    }

    /**
     * Determines the connection status of the device. Exactly tha same as in MainActivity.
     *
     * @return TRUE if connected, FALSE if not.
     */
    private boolean isConnected() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;

        if ( manager != null ) info = manager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();

    }

    /**
     * This function takse care about the trailers and reviews. It might contain too many lines but
     * the similarity of the structure of the calls of te two AsyncTasks gives a reason to it.
     */
    private void loadTrailersReviews() {

        TextView mTrailerStatus = findViewById(R.id.status_trailers);
        TextView mReviewStatus = findViewById(R.id.status_reviews);

        if (isConnected()) {

            mTrailerStatus.setText(R.string.status_downloading_trailers);
            mTrailerStatus.setVisibility(View.VISIBLE);
            mReviewStatus.setText(R.string.status_downloading_reviews);
            mReviewStatus.setVisibility(View.VISIBLE);

            final String trailerApi = getString(R.string.api_location_base)
                    + Integer.toString(movie.getId())+getString(R.string.api_trailers_segment)
                    + getString(R.string.api_add_parameter) + TMDB_API_KEY;
            String reviewApi = getString(R.string.api_location_base)
                    + Integer.toString(movie.getId()) + getString(R.string.api_reviews_segment)
                    + getString(R.string.api_add_parameter) + TMDB_API_KEY;

            GenericTrailerListener trailerListener = new GenericTrailerListener() {

                /**
                 * Transfers downloaded data to the main thread, in fact to the adequate Views.
                 *
                 * @param trailers Array of downloaded trailers
                 */
                @Override
                public void GenericEventListener(Trailer[] trailers) {

                    TextView mTrailerStatus = findViewById(R.id.status_trailers);

                    if (trailers.length > 0) {

                        mTrailerStatus.setVisibility(View.INVISIBLE);
                        mTrailerStatus.setHeight(0);

                        RecyclerView recyclerView = findViewById(R.id.rv_trailers);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                        TrailerRecyclerAdapter adapter = new TrailerRecyclerAdapter(mContext,trailers);
                        recyclerView.setAdapter(adapter);

                    } else {

                        mTrailerStatus.setText(R.string.status_no_trailers);

                    }

                }

            };
            GenericReviewListener reviewListener = new GenericReviewListener() {

                /**
                 * Transfers downloaded data to the main thread, in fact to the adequate Views.
                 *
                 * @param reviews Array of downloaded reviews
                 */
                @Override
                public void GenericEventListener(Review[] reviews) {

                    TextView mReviewStatus = findViewById(R.id.status_reviews);

                    if (reviews.length > 0) {

                        mReviewStatus.setVisibility(View.INVISIBLE);
                        mReviewStatus.setHeight(0);

                        RecyclerView recyclerView = findViewById(R.id.rv_reviews);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

                        ReviewRecyclerAdapter adapter = new ReviewRecyclerAdapter(mContext,reviews);
                        recyclerView.setAdapter(adapter);

                    } else {

                        mReviewStatus.setText(R.string.status_no_reviews);

                    }

                }

            };

            // Because no hard-coded strings are available, all the strings have to be given in the
            // constructor of the AsyncTask.
            String[] trailerStrings = new String[6];
            trailerStrings[0] = getString(R.string.app_name_and_version);
            trailerStrings[1] = getString(R.string.api_request_method);
            trailerStrings[2] = getString(R.string.tag_json_result);
            trailerStrings[3] = getString(R.string.tag_json_trailer_key);
            trailerStrings[4] = getString(R.string.tag_json_trailer_title);
            trailerStrings[5] = getString(R.string.tag_json_trailer_site);
            String[] reviewStrings = new String[5];
            reviewStrings[0]=getString(R.string.app_name_and_version);
            reviewStrings[1]=getString(R.string.api_request_method);
            reviewStrings[2]=getString(R.string.tag_json_result);
            reviewStrings[3]=getString(R.string.tag_json_review_author);
            reviewStrings[4]=getString(R.string.tag_json_review_content);

            TrailerFromInternet mTrailerFromInternet = new TrailerFromInternet(trailerListener,trailerApi,trailerStrings);
            mTrailerFromInternet.execute();
            ReviewFromInternet mReviewFromInternet = new ReviewFromInternet(reviewListener,reviewApi,reviewStrings);
            mReviewFromInternet.execute();

        } else {

            mTrailerStatus.setText(R.string.status_connection_needed);
            mTrailerStatus.setVisibility(View.VISIBLE);
            mReviewStatus.setText(R.string.status_connection_needed);
            mReviewStatus.setVisibility(View.VISIBLE);

        }

    }

}
