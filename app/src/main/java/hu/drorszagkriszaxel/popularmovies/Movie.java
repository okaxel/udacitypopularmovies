package hu.drorszagkriszaxel.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * Movie object creation.
 */
public class Movie implements Parcelable {

    // The names and order of the variables conform the output of the API.
    // The variable mTitle serves for stage 2. I plan to add localization.
    private Double mVoteAverage;
    private String mTitle;
    private String mPosterPath;
    private String mOriginalTitle;
    private String mOverview;
    private String mReleaseDate;


    /**
     *  Simple constructor.
     */
    public Movie() {
    }

    /**
     * Essential method to be parcelable.
     *
     * @param in Incoming Parcel
     */
    private Movie(Parcel in) {
        mVoteAverage = in.readDouble();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
    }

    /**
     * Gerenric creator.
     */
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * Just another generic method.
     *
     * @return Static return value
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Saves parcelable.
     *
     * @param parcel Parcel to save
     * @param i      Required but not used
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mOverview);
        parcel.writeString(mReleaseDate);
    }

     // Section for usual get-set methods.

    /**
     * Get the average of the votes.
     *
     * @return The average of the votes of this movie
     */
    public Double getVoteAverage() {
        return mVoteAverage;
    }

    /**
     * Get the title in local language. This will be used in stage 2 only.
     *
     * @return The title of the movie in local language
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the relative poster path. To spare some space the absolute path should be generated
     * runtime at the use.
     *
     * @return The relative path of the poster of the movie
     */
    public String getPosterPath() {
        return mPosterPath;
    }

    /**
     *
     * Get the original title. Only this will be used in Stage 1.
     *
     * @return Thr original title of the movie
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * Get the "snapshot" or overview.
     *
     * @return The overview of the movie
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * Get the release date.
     *
     * @return The release date of the movie
     */
    public String getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * Set the average of the votes.
     *
     * @param voteAverage The average of te votes of this movie
     */
    public void setVoteAverage(Double voteAverage) {
        mVoteAverage = voteAverage;
    }

    /**
     * Set the title in local language.
     *
     * @param title The title of the movie in local language
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Set the relative poster path. The API sends the relative path originally. No additional
     * processing is required.
     *
     * @param posterPath The relative poster path of the movie
     */
    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    /**
     * Set the original title.
     *
     * @param originalTitle The original title of the movie
     */
    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    /**
     * Set the "snapshot" or overview.
     *
     * @param overview The overview of the movie
     */
    public void setOverview(String overview) {
        mOverview = overview;
    }

    /**
     * Set the release date.
     *
     * @param releaseDate The release date of the movie
     */
    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }
}
