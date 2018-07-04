package hu.drorszagkriszaxel.popularmovies;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This interface handles TrailerFromInternet AsyncTask's success.
 */

public interface GenericTrailerListener {

    /**
     * Simple listener interface to catch AsyncTask's result.
     *
     * @param trailers Array of downloaded trailers
     */
    void GenericEventListener(Trailer[] trailers);

}
