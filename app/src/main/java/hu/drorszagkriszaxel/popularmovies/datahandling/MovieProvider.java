package hu.drorszagkriszaxel.popularmovies.datahandling;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This class is a ConetProvider.
 */

public class MovieProvider extends ContentProvider {

    // Values to recognies different endpoints of the Content Provider.
    private static final int MATCH_MOVIES = 100;
    private static final int MATCH_MOVIE_WITH_ID = 101;
    private static final int MATCH_FAVOURITES = 400;
    private static final int MATCH_FAVOURITES_WITH_ID = 401;

    // The UriMatcher of the Cont
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    // The database to work with.
    MoviesDbHelper mMoviesDb;

    /**
     * Creates the UriMatcher for the Content Provider. It handles two cases.
     *
     * @return  The UriMatcher object.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,MoviesContract.PATH_MOVIES,MATCH_MOVIES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY,MoviesContract.PATH_MOVIES+"/#",MATCH_MOVIE_WITH_ID);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVOURITES, MATCH_FAVOURITES);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVOURITES+"/#",MATCH_FAVOURITES_WITH_ID);
        return uriMatcher;
    }


    /**
     * Not at all complicated standard onCreate method.
     *
     * @return  True on succees and on any other cases.
     */
    @Override
    public boolean onCreate() {
        mMoviesDb = new MoviesDbHelper(getContext());
        return true;
    }

    /**
     * Usual query method. "With Id" cases searching for movie id.
     *
     * @param uri      Uri to find out where and how to search.
     * @param strings  Projection
     * @param s        Selection
     * @param strings1 Selection arguments
     * @param s1       Sort order
     * @return         Cursor to work with
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase database = mMoviesDb.getReadableDatabase();
        Cursor cursor = null;

        switch (URI_MATCHER.match(uri)) {
            case MATCH_MOVIES: {
                cursor = database.query(MoviesContract.MovieEntry.TABLE_NAME, strings, s, strings1,
                        null, null, s1);
                break;
            }
            case MATCH_MOVIE_WITH_ID: {
                String id = uri.getLastPathSegment();
                cursor = database.query(MoviesContract.MovieEntry.TABLE_NAME, strings,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?", new String[] { id },
                        null, null, s1);
                break;
            }
            case MATCH_FAVOURITES: {
                cursor = database.query(MoviesContract.FavouritesEntry.TABLE_NAME, strings, s, strings1,
                        null, null, s1);
                break;
            }
            case MATCH_FAVOURITES_WITH_ID: {
                String id = uri.getLastPathSegment();
                cursor = database.query(MoviesContract.FavouritesEntry.TABLE_NAME, strings,
                        MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + "=?", new String[] { id },
                        null, null, s1);
                break;
            }
            default: throw new UnsupportedOperationException("Unsupported URI: "+uri);
        }
        if ( cursor != null ) { cursor.setNotificationUri(getContext().getContentResolver(),uri); }
        return cursor;
    }

    /**
     * Since I don't use it and the Content Provider isn't exported, I leave it empty.
     *
     * @param uri   The uri to examine.
     * @return      Nowadays it's definitely null.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Usual insert method.
     *
     * @param uri           Uri to find out where to insert.
     * @param contentValues Values to insert
     * @return              Uri to acess the inserted row directly
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database = mMoviesDb.getWritableDatabase();
        Uri returnUri = null;

        switch (URI_MATCHER.match(uri)) {
            case MATCH_MOVIES: {
                long id = database.insert(MoviesContract.MovieEntry.TABLE_NAME,null, contentValues);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    returnUri = MoviesContract.BASE_CONTENT_URI.buildUpon()
                            .appendPath(MoviesContract.PATH_MOVIES)
                            .appendPath(Long.toString(id))
                            .build();
                }
                break;
            }
            case MATCH_FAVOURITES: {
                long id = database.insert(MoviesContract.FavouritesEntry.TABLE_NAME,null, contentValues);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    returnUri = MoviesContract.BASE_CONTENT_URI.buildUpon()
                            .appendPath(MoviesContract.PATH_FAVOURITES)
                            .appendPath(Long.toString(id))
                            .build();
                }
                break;
            }
            default: throw new UnsupportedOperationException("Unsupported URI: "+uri);
        }
        return returnUri;
    }

    /**
     * Usual delete method.
     *
     * @param uri     Uri to find out where and how to delete.
     * @param s       Selection
     * @param strings Selection arguments
     * @return        Number of deleted rors
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mMoviesDb.getWritableDatabase();
        int returnInt = 0;

        switch (URI_MATCHER.match(uri)) {
            case MATCH_MOVIES: {
                returnInt = database.delete(MoviesContract.MovieEntry.TABLE_NAME,s,strings);
                break;
            }
            case MATCH_MOVIE_WITH_ID: {
                String id = uri.getLastPathSegment();
                returnInt = database.delete(MoviesContract.MovieEntry.TABLE_NAME,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?", new String[] { id });
                break;
            }
            case MATCH_FAVOURITES: {
                returnInt = database.delete(MoviesContract.FavouritesEntry.TABLE_NAME,s,strings);
                break;
            }
            case MATCH_FAVOURITES_WITH_ID: {
                String id = uri.getLastPathSegment();
                returnInt = database.delete(MoviesContract.FavouritesEntry.TABLE_NAME,
                        MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + "=?", new String[] { id });
                break;
            }
            default: throw new UnsupportedOperationException("Unsupported URI: "+uri);
        }
        return returnInt;
    }

    /**
     * Usual update method.
     *
     * @param uri           Uri to find out where and how to update.
     * @param contentValues Values
     * @param s             Selection
     * @param strings       Selection aguments
     * @return              Number of wors attended
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mMoviesDb.getWritableDatabase();
        int returnInt = 0;
        switch (URI_MATCHER.match(uri)) {
            case MATCH_MOVIES: {
                returnInt = database.update(MoviesContract.MovieEntry.TABLE_NAME,contentValues,s,strings);
                break;
            }
            case MATCH_MOVIE_WITH_ID: {
                String id = uri.getLastPathSegment();
                returnInt = database.update(MoviesContract.MovieEntry.TABLE_NAME,contentValues,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=" + id +
                                (!TextUtils.isEmpty(s) ? " AND (" + s + ")" : ""),strings);
                break;
            }
            case MATCH_FAVOURITES: {
                returnInt = database.update(MoviesContract.FavouritesEntry.TABLE_NAME,contentValues,s,strings);
                break;
            }
            case MATCH_FAVOURITES_WITH_ID: {
                String id = uri.getLastPathSegment();
                returnInt = database.update(MoviesContract.FavouritesEntry.TABLE_NAME,contentValues,
                        MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + "=" + id +
                                (!TextUtils.isEmpty(s) ? " AND (" + s + ")" : ""),strings);
                break;
            }
            default: throw new UnsupportedOperationException("Unsupported URI: "+uri);
        }
        return returnInt;
    }
}
