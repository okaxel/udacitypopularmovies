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

public class TrailerFromInternet extends AsyncTask<String, Void, Trailer[]> {

    // Variables to store constructor's parameters
    private GenericTrailerListener mListener;
    private String mApiPath;
    private String[] mNoHardStriings;

    /**
     * Simple constructor.
     *
     * @param listener       Holds the activity's listener instance
     * @param apiPath        Access path of TheMovieDb API, this way no movie Id is needed
     * @param noHardStriings To avoid any hard-coded string, the needed strings are sent this way
     */
    TrailerFromInternet(GenericTrailerListener listener, String apiPath, String[] noHardStriings) {

        mListener = listener;
        mApiPath = apiPath;
        mNoHardStriings = noHardStriings;

    }

    /**
     * AsyncTask's core functionality is here.
     *
     * @param strings Default values
     * @return        Array of downloaded trailers
     */
    @Override
    protected Trailer[] doInBackground(String... strings) {

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

            if (jsonString != null) {

                try {

                    return jsonToTrailers(jsonString);

                } catch (JSONException e) {

                    Log.e(mNoHardStriings[0],e.getLocalizedMessage(),e);
                    e.printStackTrace();

                }

            }

        }

        return null; // This return point isn't the best place at all.

    }

    /**
     * Loads the EventListener from DetailsActivity to handle the result in the main thread.
     *
     * @param trailers The result array of trailers
     */
    @Override
    protected void onPostExecute(Trailer[] trailers) {

        super.onPostExecute(trailers);
        mListener.GenericEventListener(trailers);

    }

    /**
     * Converts the JSON string into trailers object.
     *
     * @param json JSON string of TheMovieDb API
     * @return     Array of Trailer objects
     * @throws JSONException In case of error it trows an exception
     */
    private Trailer[] jsonToTrailers(String json) throws JSONException {

        JSONObject trailersObject = new JSONObject(json);
        JSONArray trailersArray = trailersObject.getJSONArray(mNoHardStriings[2]);
        Trailer[] trailers = new Trailer[trailersArray.length()];

        for (int i=0 ; i<trailersArray.length() ; i++) {

            JSONObject oneTrailer = trailersArray.getJSONObject(i);
            trailers[i] = new Trailer(oneTrailer.getString(mNoHardStriings[3]),oneTrailer.getString(mNoHardStriings[4]),
                    oneTrailer.getString(mNoHardStriings[5]));

        }

        return trailers;

    }

}
