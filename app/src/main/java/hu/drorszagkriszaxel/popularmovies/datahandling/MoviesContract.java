package hu.drorszagkriszaxel.popularmovies.datahandling;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This is contract file to manage larger database.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "hu.drorszagkriszaxel.popularmovies.datahandling.MovieProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final String PATH_FAVOURITES = "favourites";

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_POPULARITY = "popularity";

    }

    public static final class FavouritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

}
