package hu.drorszagkriszaxel.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * Created by Axel Ország-Krisz Dr.
 */

public class TrailerRecyclerAdapter extends RecyclerView.Adapter<TrailerRecyclerAdapter.ViewHolder> {

    private Context mContetx;
    private Trailer[] mTrailers;
    private LayoutInflater mInflater;

    /**
     * Simple constructor.
     *
     * @param context  The context of the RecyclerView
     * @param trailers Data to show
     */
    TrailerRecyclerAdapter(Context context, Trailer[] trailers) {

        mContetx = context;
        mInflater = LayoutInflater.from(context);
        mTrailers = trailers;

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

        View view = mInflater.inflate(R.layout.trailer_recycler,parent,false);
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

        TrailerClickListener clickListener = new TrailerClickListener(mTrailers[position].getKey());
        holder.mTitle.setOnClickListener(clickListener);
        holder.mTitle.setText(mTrailers[position].getTitle());

    }

    /**
     * Gets the count of data items.
     *
     * @return he amount of items
     */
    @Override
    public int getItemCount() {

        if (mTrailers != null) {

            return mTrailers.length;

        } else {

            return 0;

        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;

        ViewHolder(View itemView) {

            super(itemView);

            mTitle = itemView.findViewById(R.id.trailer_title);

        }

    }

    class TrailerClickListener implements View.OnClickListener {

        private String mYuotubeKey;

        TrailerClickListener(String yuotubeKey) {

            this.mYuotubeKey = yuotubeKey;

        }

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mContetx.getString(R.string.youtube_base_link)+mYuotubeKey));
            mContetx.startActivity(intent);

        }
    }

}
