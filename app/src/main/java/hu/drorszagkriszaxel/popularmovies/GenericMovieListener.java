package hu.drorszagkriszaxel.popularmovies;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This interface handles MovieFromInternet AsyncTask's success.
 */

public interface GenericMovieListener {

    /**
     * Simple listener interface to catch AsyncTask's result.
     *
     * @param movies Array of downloaded movies
     */
    void GenericEventListener(Movie[] movies);

}
