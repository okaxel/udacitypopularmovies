package hu.drorszagkriszaxel.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Axel Ország-Krisz Dr.
 */

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {

    private Review[] mReviews;
    private LayoutInflater mInflater;

    /**
     * Simple constructor.
     *
     * @param context The context of the RecyclerView
     * @param reviews Data to show
     */
    ReviewRecyclerAdapter(Context context, Review[] reviews) {

        mInflater = LayoutInflater.from(context);
        mReviews = reviews;

    }

    /**
     * Creates ViewHolder.
     *
     * @param parent   Parent view
     * @param viewType Required but not used
     * @return         New and inflated ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.reviews_recycler,parent,false);
        return new ViewHolder(view);

    }

    /**
     * Binds data to a ViewHolder.
     *
     * @param holder   The ViewHolder to bind
     * @param position The position of the data in the array
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mAuthor.setText(mReviews[position].getAuthor());
        holder.mContent.setText(mReviews[position].getContent());

    }

    /**
     * Gets the count of data items.
     *
     * @return The amount of items
     */
    @Override
    public int getItemCount() {

        if (mReviews != null) {

            return mReviews.length;

        } else {

            return 0;

        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mAuthor;
        private TextView mContent;

        ViewHolder(View itemView) {

            super(itemView);

            mAuthor = itemView.findViewById(R.id.review_author);
            mContent = itemView.findViewById(R.id.review_content);

        }
    }
}
