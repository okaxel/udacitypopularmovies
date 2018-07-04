package hu.drorszagkriszaxel.popularmovies;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * Trailer object creation.
 */

public class Trailer {

    private String mKey;
    private String mTitle;
    private String mSite;

    /**
     * Default constructor.
     */
    Trailer() {

    }

    /**
     * Constructor with parameters.
     *
     * @param key   Key of the trailer
     * @param title Title of the trailer
     * @param site  The site of the trailer (useful for key)
     */
    Trailer(String key, String title, String site) {

        this.mKey = key;
        this.mTitle = title;
        this.mSite = site;

    }

    /**
     * Gets trailer's key.
     *
     * @return The key
     */
    public String getKey() {

        return mKey;

    }

    /**
     * Gets trailer's title
     *
     * @return The title
     */
    public String getTitle() {

        return mTitle;

    }

    /**
     * Gets the site of the trailer.
     *
     * @return The name of the site
     */
    public String getSite() {

        return mSite;

    }

    /**
     * Sets the trailer's key.
     *
     * @param key The key
     */
    public void setKey(String key) {

        this.mKey = key;

    }

    /**
     * Sets the trailer's title.
     *
     * @param title The title
     */
    public void setTitle(String title) {

        this.mTitle = title;

    }

    /**
     * Sets the trailer's site.
     *
     * @param site The name of the site
     */
    public void setSite(String site) {

        this.mSite = site;

    }
}
