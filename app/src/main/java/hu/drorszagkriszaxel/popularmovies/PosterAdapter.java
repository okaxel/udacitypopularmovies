package hu.drorszagkriszaxel.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.sql.Struct;

/**
 * Created by Axel Ország-Krisz Dr.
 *
 * This class contains the Adapter for the GridView.
 */

public class PosterAdapter extends BaseAdapter {

    private Context mContext;
    private Movie[] mMovies;
    private String mPosterPath;

    /**
     * Class consturctor.
     *
     * @param mContext The Context of the Application
     * @param mMovies  The array of the movies
     */
    PosterAdapter(Context mContext, Movie[] mMovies) {
        this.mContext = mContext;
        this.mMovies = mMovies;

        mPosterPath = mContext.getString(R.string.tmdb_image_location_base)
                + mContext.getString(R.string.tmdb_image_resolution_path);
    }


    /**
     * Gets the number of movies.
     *
     * @return Returns -1 if no movies present, or the count of movies if there are movies
     */
    @Override
    public int getCount() {
        if (mMovies == null || mMovies.length == 0) {
            return -1;
        }
        return mMovies.length;
    }

    /**
     * Gets the item specified in the argument.
     *
     * @param i The position of the desired item
     * @return  The item itself or null if no item present
     */
    @Override
    public Object getItem(int i) {
        if (mMovies == null || mMovies.length == 0) {
            return null;
        }
        return mMovies[i];
    }

    /**
     * Gets the Id of the item, which is in the position given in the parameter.
     *
     * @param i The Id of the desired item
     * @return  The Id of the desired item
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Gets ImageView to use in the Adapter.
     *
     * @param i         The position of the element in the Adapter
     * @param view      The target View or null of the View not yet exists
     * @param viewGroup Required but not used
     * @return          The ImageView to show
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) view;
        }
        String loadPath = mPosterPath + mMovies[i].getPosterPath();
        Picasso.get()
                .load(loadPath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(imageView);
        return imageView;
    }
}
