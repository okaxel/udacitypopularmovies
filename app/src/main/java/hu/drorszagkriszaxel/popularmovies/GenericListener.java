package hu.drorszagkriszaxel.popularmovies;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This interface handles ApiHandler AsyncTask's success.
 */

public interface GenericListener {

    /**
     * Simple listener interface to catch AsyncTask's result
     *
     * @param movies Array of downloaded movies
     */
    public void GenericEventListener(Movie[] movies);

}
