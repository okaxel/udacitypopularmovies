package hu.drorszagkriszaxel.popularmovies;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This interface handles ReviewFromInternet AsyncTask's success.
 */

public interface GenericReviewListener {

    /**
     * Simple listener interface to catch AsyncTask's result.
     *
     * @param reviews Array of downloaded reviews
     */
    void GenericEventListener(Review[] reviews);

}
