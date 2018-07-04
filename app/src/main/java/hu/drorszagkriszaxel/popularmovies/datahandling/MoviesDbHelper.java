package hu.drorszagkriszaxel.popularmovies.datahandling;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This is an SQLiteOpenHelper class to help ContentProvider
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    // The name of the database.
    private static final String DATABASE_NAME = "popularmovies.db";

    // WARNING! This should be increased if database structure changes.
    private static final int DATABASE_VERSION = 1;

    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Simple creator.
     *
     * @param sqLiteDatabase The database
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME +
                " ( " + MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL );";

        final String SQL_FAVOURITES_TABLE = "CREATE TABLE " + MoviesContract.FavouritesEntry.TABLE_NAME +
                " ( " + MoviesContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.FavouritesEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry._ID + ") );";

        sqLiteDatabase.execSQL(SQL_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_FAVOURITES_TABLE);

    }

    /**
     * Simple upgrade method.
     *
     * @param sqLiteDatabase Database.
     * @param i              Old version
     * @param i1             New version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavouritesEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }
}
