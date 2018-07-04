package hu.drorszagkriszaxel.popularmovies;

import android.os.AsyncTask;
import android.util.Log;
import android.util.MutableLong;

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

public class ReviewFromInternet extends AsyncTask<String, Void, Review[]> {

    // Variables to store constructor's parameters
    private GenericReviewListener mListener;
    private String mApiPath;
    private String[] mNoHardStriings;

    /**
     * Simple constructor.
     *
     * @param listener      Holds the activity's listener instance
     * @param apiPath       Access path of TheMovieDb API, this way no movie Id is needed
     * @param noHardStrings To avoid any hard-coded string, the needed strings are sent this way
     */
    ReviewFromInternet(GenericReviewListener listener, String apiPath, String[] noHardStrings) {

        mListener = listener;
        mApiPath = apiPath;
        mNoHardStriings = noHardStrings;

    }

    /**
     * AsyncTask's core functionality is here.
     *
     * @param strings Default values
     * @return        The downloaded array of reviews
     */
    @Override
    protected Review[] doInBackground(String... strings) {

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

                    return jsonToReviews(jsonString);

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
     * @param reviews The result, array of reviews
     */
    @Override
    protected void onPostExecute(Review[] reviews) {

        super.onPostExecute(reviews);
        mListener.GenericEventListener(reviews);

    }

    /**
     * Converts the JSON string into reviews object.
     *
     * @param json  JSON string of TheMovieDb API
     * @return      Array of Review objects
     * @throws JSONException In case of error it trows an exception
     */
    private Review[] jsonToReviews(String json) throws JSONException {

        JSONObject reviewsObject = new JSONObject(json);
        JSONArray reviewsArray = reviewsObject.getJSONArray(mNoHardStriings[2]);
        Review[] reviews = new Review[reviewsArray.length()];

        for (int i=0 ; i<reviewsArray.length() ; i++) {

            JSONObject oneReview = reviewsArray.getJSONObject(i);
            reviews[i] = new Review(oneReview.getString(mNoHardStriings[3]),
                    oneReview.getString(mNoHardStriings[4]));

        }

        return reviews;

    }

}
