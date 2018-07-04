package hu.drorszagkriszaxel.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Axel Ország-Krisz Dr.
 */

public class MovieFromInternet extends AsyncTask<String,Void,Movie[]> {

    // Variables to store constructor's parameters
    private GenericMovieListener mListener;
    private String mApiPath;
    private String[] mNoHardStriings;

    /**
     * Simple constructor
     *
     * @param listener       Holds the activity's listener instance
     * @param ApiPath        Access path of TheMovieDb API, this way no sort order is needed
     * @param noHardStriings To avoid any hard-coded string, the needed strings are sent this way
     */
    MovieFromInternet(GenericMovieListener listener, String ApiPath, String[] noHardStriings) {
        mListener = listener;
        mApiPath = ApiPath;
        mNoHardStriings = noHardStriings;
    }

    /**
     * Constructs the Movie array for the GridView's PosterAdapter.
     *
     * Some if varibale != null added to avoid NullPointerExceptions.
     *
     * @param strings Required but not used
     * @return Movie array for the GridView PosterAdapter on null in case of failure
     */
    @Override
    protected Movie[] doInBackground(String... strings) {

        // Initializing variables forward to catch exceptions and handle scope at the same time.
        URL apiUrl = null;
        String jsonString = null;

        try {
            apiUrl = new URL(mApiPath);
        } catch (MalformedURLException e) {
            Log.e(mNoHardStriings[0],e.getLocalizedMessage());
        }
        if (apiUrl != null) {

            // Some other catch and scope related initialization here.
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod(mNoHardStriings[1]);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String oneLine;
                    while ((oneLine = reader.readLine()) != null) {
                        stringBuilder.append(oneLine)
                                .append("\n");
                    }
                    if (stringBuilder.length() > 0) {
                        jsonString = stringBuilder.toString();
                    }
                }
            } catch (IOException e) {
                Log.e(mNoHardStriings[0],e.getLocalizedMessage());
            } finally {
                if (connection != null) connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(mNoHardStriings[0],e.getLocalizedMessage());
                    }
                }
            }
        }
        if (jsonString != null) {
            try {
                return jsonToMovies(jsonString);
            } catch (JSONException e) {
                Log.e(mNoHardStriings[0],e.getLocalizedMessage(),e);
                e.printStackTrace();
            }
        }

        return null; // This return point isn't the best place at all.

    }

    /**
     * Loads the EventListener from MainActivity to handle the result in the main thread.
     *
     * @param movies The generated array of movies to create adapter
     */
    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        mListener.GenericEventListener(movies);
    }

    /**
     * Converts the JSON string into movies object.
     *
     * @param json JSON string of TheMovieDb API
     * @return Array of Movie objects
     * @throws JSONException In case of error it trows an exception
     */
    private Movie[] jsonToMovies(String json) throws JSONException {

        JSONObject moviesObject = new JSONObject(json);
        JSONArray moviesArray = moviesObject.getJSONArray(mNoHardStriings[2]);
        Movie[] movies = new Movie[moviesArray.length()];

        for (int i=0; i<moviesArray.length(); i++) {

            movies[i] = new Movie();
            JSONObject oneMovie = moviesArray.getJSONObject(i);
            movies[i].setId(oneMovie.getInt(mNoHardStriings[3]));
            movies[i].setVoteAverage(oneMovie.getDouble(mNoHardStriings[4]));
            movies[i].setTitle(oneMovie.getString(mNoHardStriings[5]));
            movies[i].setPosterPath(oneMovie.getString(mNoHardStriings[6]));
            movies[i].setOriginalTitle(oneMovie.getString(mNoHardStriings[7]));
            movies[i].setOverview(oneMovie.getString(mNoHardStriings[8]));
            movies[i].setReleaseDate(oneMovie.getString(mNoHardStriings[9]));
            movies[i].setPopularity(oneMovie.getDouble(mNoHardStriings[10]));

        }

        return movies;
    }

}
