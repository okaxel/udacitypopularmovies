package hu.drorszagkriszaxel.popularmovies;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * Review object creation.
 */

public class Review {

    private String mAuthor;
    private String mContent;

    /**
     * Default constructor.
     */
    Review() {

    }

    /**
     * Constructor with parameters.
     *
     * @param author  The author's nick
     * @param content The text of the review
     */
    Review(String author, String content) {

        this.mAuthor = author;
        this.mContent = content;

    }

    /**
     * Gets the nick of the author.
     *
     * @return  The author's nick
     */
    public String getAuthor() {

        return mAuthor;

    }

    /**
     * Gets the content of the review.
     *
     * @return  The text
     */
    public String getContent() {

        return mContent;

    }

    /**
     * Sets the nick of the author of the review.
     *
     * @param author The author's nick
     */
    public void setAuthor(String author) {

        this.mAuthor = author;

    }

    /**
     * Sets the text of the review.
     * @param content The text
     */
    public void setContent(String content) {

        this.mContent = content;

    }
}
