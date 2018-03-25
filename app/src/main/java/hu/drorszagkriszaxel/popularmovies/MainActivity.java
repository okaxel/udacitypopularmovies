package hu.drorszagkriszaxel.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.AppCompatSpinner;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

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
public class MainActivity extends AppCompatActivity {

    // Reference to the hidden API key
    private static final String TMDB_API_KEY = BuildConfig.TMDB_API_KEY;

    // Variable to manage sort order.
    int mSortOrder = 0; // Initalized with 0 if no value is saved

    // Variables to manage layout elements
    Toolbar mToolbar;
    AppCompatSpinner mSpinner;
    GridView mGridView;

    /**
     * Just the usual onCreate method to initalize various things.
     *
     * @param savedInstanceState Essential parameter to work with
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To save some space let's use Toolbar instead of Actionbar
        mToolbar = findViewById(R.id.tolbar_main);

        // Set spinner and things about sort order.
        mSpinner = findViewById(R.id.spinner_sort);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sort_order_values));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(sortListener);
        recallSortOrder();
        mSpinner.setSelection(mSortOrder);

        // Make GridView accessible and useful.
        mGridView = findViewById(R.id.grid_movies);
        mGridView.setOnItemClickListener(movieClickHandler);

        // If movies are locally avaliable, load it to GridView.
        if (savedInstanceState != null) {
            if ( savedInstanceState.containsKey(getString(R.string.key_parcelable_array))) {
                Parcelable[] parcelables = savedInstanceState
                        .getParcelableArray(getString(R.string.key_parcelable_array));
                if (parcelables != null) {
                    int moviesCount = parcelables.length;
                    Movie[] movies = new Movie[moviesCount];
                    for (int i=0; i < moviesCount; i++) {
                        movies[i] = (Movie) parcelables[i];
                    }
                    mGridView.setAdapter(new PosterAdapter(this, movies));

                }
            }
        }

        // If GridView still don't have movies loaded, let's use internet
        if (mGridView.getAdapter() == null ) makeGridViewAdapter();

    }

    /**
     * Save state of instances.
     *
     * @param outState The Bundle itself
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.key_sort_order),mSortOrder);
        outState.putInt(getString(R.string.key_gridview_position),mGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    /**
     * Restore state of instances.
     *
     * @param savedInstanceState The Bundle itself
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(getString(R.string.key_sort_order)))
            mSortOrder = savedInstanceState.getInt(getString(R.string.key_sort_order));

        if (savedInstanceState.containsKey(getString(R.string.key_gridview_position)))
            mGridView.smoothScrollToPosition(savedInstanceState.getInt(getString(R.string.key_gridview_position)));
    }

    /**
     * Some stuff in case if Activity is forgotten but no onCreate is called.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSpinner.setSelection(mSortOrder);
    }

    // This section is for TMDB. Unfortunately it don't worth to put it into a separate class,
    // because of other dependencies like Movie object however it could be another solution to
    // separate this content.

    /**
     * Checks if there is network or not. This is needed to avoid AsyncTask arrors.
     *
     * This code is from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * The solution recommended officially produces IDE warning about potential NullPointerException.
     * Finally I found a way to solve this issue. :-)
     *
     * @return     TRUE if there there is network and device is connection or connected
     */
    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;

        if ( manager != null ) info = manager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * Sets mGridView's adapter from TheMovieDB.
     */
    private void makeGridViewAdapter() {
        if (isConnected()) {

            String[] sortOrder = getResources().getStringArray(R.array.sort_order_path);

            String apiString = getString(R.string.api_location_base)
                    + sortOrder[mSortOrder] + getString(R.string.api_add_parameter)
                    + TMDB_API_KEY;

            GenericListener mListener = new GenericListener() {
                @Override
                public void GenericEventListener(Movie[] movies) {
                    mGridView.setAdapter(new PosterAdapter(getApplicationContext(), movies));
                }
            };

            // Because no hard-coded stinrgs are available, all the strings have to be given in the
            // sontructur of the AsyncTask.
            String[] mStrings = new String[9];
            mStrings[0]=getString(R.string.app_name_and_version);
            mStrings[1]=getString(R.string.api_request_method);
            mStrings[2]=getString(R.string.tag_json_result);
            mStrings[3]=getString(R.string.tag_json_vote_average);
            mStrings[4]=getString(R.string.tag_json_title);
            mStrings[5]=getString(R.string.tag_json_poster_path);
            mStrings[6]=getString(R.string.tag_json_original_title);
            mStrings[7]=getString(R.string.tag_json_overview);
            mStrings[8]=getString(R.string.tag_json_release_date);

            ApiHandler mApiHandler = new ApiHandler(mListener,apiString,mStrings);
            mApiHandler.execute();

        } else {
            Toast.makeText(this,R.string.msg_need_connection,Toast.LENGTH_LONG)
                    .show();
        }
    }

    // Sort order handling

    /**
     * Gets the sort order value from SharedPreferences.
     */
    private void recallSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSortOrder = preferences.getInt(getString(R.string.key_sort_order),0);
    }

    /**
     * Saves the sort order value to SharedPreferences.
     */
    private void saveSortOrder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getString(R.string.key_sort_order),mSortOrder);
        editor.apply();
    }

    // In-class objects hereafter.

    /**
     * This object handlles the sort order selection.
     */
    private final AppCompatSpinner.OnItemSelectedListener sortListener = new AppCompatSpinner.OnItemSelectedListener() {

        /**
         * Sets and saves the selected sort order.
         *
         * @param adapterView This parameter holds the parent
         * @param view        Required but not used
         * @param i           This parameter holds the position
         * @param l           Required but not used
         */
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mSortOrder = i;
            saveSortOrder();
            makeGridViewAdapter();
        }

        /**
         * Does nothing.
         *
         * @param adapterView This parameter holds the parent
         */
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    /**
     * This object handles movie selection and gives the control to Description Activity.
     */
    private final GridView.OnItemClickListener movieClickHandler = new GridView.OnItemClickListener() {

        /**
         * This method handles the click and gives the control to Description Activity.
         *
         * @param adapterView This parameter holds the parent
         * @param view        Required but not used
         * @param i           This parameter holds the position
         * @param l           Required but not used
         */
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Movie movie = (Movie) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
            intent.putExtra(getResources().getString(R.string.key_intent_details),movie);
            startActivity(intent);

        }
    };

}
