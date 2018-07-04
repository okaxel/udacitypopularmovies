package hu.drorszagkriszaxel.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.AppCompatSpinner;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

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
                    BaseAdapter baseAdapter = (BaseAdapter) mGridView.getAdapter();
                    baseAdapter.notifyDataSetChanged();

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
    public boolean isConnected() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;

        if ( manager != null ) info = manager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();

    }

    /**
     * Sets mGridView's adapter from TheMovieDB or from local database depending on various circumstances.
     */
    private void makeGridViewAdapter() {

        if (isConnected() && (mSortOrder==0 || mSortOrder==1)) {

            String[] sortOrder = getResources().getStringArray(R.array.sort_order_path);

            String apiString = getString(R.string.api_location_base)
                    + sortOrder[mSortOrder] + getString(R.string.api_add_parameter)
                    + TMDB_API_KEY;

            GenericMovieListener mListener = new GenericMovieListener() {
                @Override
                public void GenericEventListener(Movie[] movies) {

                    mGridView.setAdapter(new PosterAdapter(getApplicationContext(), movies));
                    BaseAdapter baseAdapter = (BaseAdapter) mGridView.getAdapter();
                    baseAdapter.notifyDataSetChanged();

                    int deletedRows = getContentResolver().delete(
                            Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,MoviesContract.PATH_MOVIES),
                            null, null);

                    ContentValues values = new ContentValues();

                    for (Movie oneMovie: movies) {

                        values.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID,oneMovie.getId());
                        values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,oneMovie.getVoteAverage());
                        values.put(MoviesContract.MovieEntry.COLUMN_TITLE,oneMovie.getTitle());
                        values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH,oneMovie.getPosterPath());
                        values.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,oneMovie.getOriginalTitle());
                        values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,oneMovie.getOverview());
                        values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,oneMovie.getReleaseDate());
                        values.put(MoviesContract.MovieEntry.COLUMN_POPULARITY,oneMovie.getPopularity());

                        Uri insertedRow = getContentResolver().insert(
                                Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,MoviesContract.PATH_MOVIES),
                                values);

                    }

                }
            };

            // Because no hard-coded strings are available, all the strings have to be given in the
            // constructor of the AsyncTask.
            String[] mStrings = new String[11];
            mStrings[0]=getString(R.string.app_name_and_version);
            mStrings[1]=getString(R.string.api_request_method);
            mStrings[2]=getString(R.string.tag_json_result);
            mStrings[3]=getResources().getString(R.string.tag_json_id);
            mStrings[4]=getString(R.string.tag_json_vote_average);
            mStrings[5]=getString(R.string.tag_json_title);
            mStrings[6]=getString(R.string.tag_json_poster_path);
            mStrings[7]=getString(R.string.tag_json_original_title);
            mStrings[8]=getString(R.string.tag_json_overview);
            mStrings[9]=getString(R.string.tag_json_release_date);
            mStrings[10]=getResources().getString(R.string.tag_json_popularity);

            MovieFromInternet mMovieFromInternet = new MovieFromInternet(mListener,apiString,mStrings);
            mMovieFromInternet.execute();

        } else {

            String sortOrder = null;
            switch (mSortOrder) {
                case 0: {
                    sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " ASC";
                    break;
                }
                case 1: {
                    sortOrder = MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " ASC";
                    break;
                }
                case 2: {
                    sortOrder = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";
                    break;
                }
            }

            String[] projection = {
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
                    MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                    MoviesContract.MovieEntry.COLUMN_TITLE,
                    MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
                    MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                    MoviesContract.MovieEntry.COLUMN_OVERVIEW,
                    MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                    MoviesContract.MovieEntry.COLUMN_POPULARITY
            };

            Cursor cursor = getContentResolver().query(
                    Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI,MoviesContract.PATH_MOVIES),
                    projection,null,null, sortOrder);

            if (cursor!=null) {

                if (cursor.getCount()>0) {

                    Movie[] movies = new Movie[cursor.getCount()];
                    int localCounter = 0;

                    while (cursor.moveToNext()) {

                        movies[localCounter] = new Movie();
                        movies[localCounter].setId(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID)));
                        movies[localCounter].setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                        movies[localCounter].setTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)));
                        movies[localCounter].setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)));
                        movies[localCounter].setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
                        movies[localCounter].setOverview(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)));
                        movies[localCounter].setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)));
                        movies[localCounter].setPopularity(cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POPULARITY)));

                        localCounter++;

                    }

                    if (mSortOrder==2) {

                        String[] favProjection = {
                                MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID
                        };
                        Cursor favCursor = getContentResolver().query(
                                Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, MoviesContract.PATH_FAVOURITES),
                                favProjection, null, null,
                                MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + " ASC");

                        if (favCursor != null) {
                            if (favCursor.getCount() > 0) {

                                Movie[] favMovies = new Movie[favCursor.getCount()];

                                int favCounter = 0;

                                while (favCursor.moveToNext()) {

                                    favMovies[favCounter] = new Movie();
                                    favMovies[favCounter] = getMovieById(movies,
                                            favCursor.getInt(favCursor.getColumnIndex(MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID)));
                                    favCounter++;

                                }

                                if (favCounter>0 ) {

                                    Movie[] finalListOfMovies = new Movie[favCounter];

                                    java.lang.System.arraycopy(favMovies,0,finalListOfMovies,0,favCounter);

                                    mGridView.setAdapter(new PosterAdapter(getApplicationContext(),finalListOfMovies));
                                    BaseAdapter baseAdapter = (BaseAdapter) mGridView.getAdapter();
                                    baseAdapter.notifyDataSetChanged();


                                } else {

                                    Toast.makeText(this, R.string.msg_favourites_special_error, Toast.LENGTH_LONG)
                                            .show();

                                }

                            } else {

                                Toast.makeText(this, R.string.msg_no_favourites_now, Toast.LENGTH_LONG)
                                        .show();

                            }
                            favCursor.close();
                        } else {

                            Toast.makeText(this, R.string.msg_no_favourites_yet, Toast.LENGTH_LONG)
                                    .show();

                        }

                    } else {

                        mGridView.setAdapter(new PosterAdapter(getApplicationContext(), movies));
                        BaseAdapter baseAdapter = (BaseAdapter) mGridView.getAdapter();
                        baseAdapter.notifyDataSetChanged();

                    }

                } else {

                    Toast.makeText(this,R.string.msg_no_movies_stored,Toast.LENGTH_LONG)
                            .show();

                }

                cursor.close();

            } else {

                Toast.makeText(this,R.string.msg_need_connection_first,Toast.LENGTH_LONG)
                        .show();

            }

        }

    }

    /**
     * Select favourite from the given array of movies.
     *
     * @param whereFrom The array, where all movies are stored
     * @param id        The id to search for
     * @return          The record that matches the given id
     */
    private Movie getMovieById(Movie[] whereFrom, int id) {

        Movie returnMovie = new Movie();

        for (Movie forMovie : whereFrom) {

            if (forMovie.getId()==id) {

                returnMovie = forMovie;
                break;

            }

        }

        return returnMovie;

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
