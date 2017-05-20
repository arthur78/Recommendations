package com.artxak.recommendations.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.artxak.recommendations.MainActivity;
import com.artxak.recommendations.R;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
        implements Callback<ActiveListings> {

    private MainActivity mActivity;
    private LayoutInflater mLayoutInflater;
    private ActiveListings mActiveListings;

    public ListingAdapter(MainActivity activity) {
        mActivity = activity;
        mLayoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListingHolder(mLayoutInflater.inflate(R.layout.layout_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(ListingHolder holder, int position) {
        Listing listing = mActiveListings.results[position];
        holder.mListingTitle.setText(listing.title);
        holder.mListingShopName.setText(listing.Shop.shop_name);
        holder.mListingPrice.setText(listing.price);

        Picasso.with(holder.mImageView.getContext())
                .load(listing.Images[0].url_570xN)
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        if (mActiveListings == null || mActiveListings.results == null)
            return 0;
        return mActiveListings.results.length;
    }


    @Override
    public void success(ActiveListings activeListings, Response response) {
        mActiveListings = activeListings;
        notifyDataSetChanged();
        mActivity.showList();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        mActivity.showError();
    }

    public ActiveListings getActiveListings() {
        return mActiveListings;
    }

    public class ListingHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mListingTitle;
        public TextView mListingShopName;
        public TextView mListingPrice;

        public ListingHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.listingImage);
            mListingTitle = (TextView) itemView.findViewById(R.id.listingTitle);
            mListingShopName = (TextView) itemView.findViewById(R.id.listingShopName);
            mListingPrice = (TextView) itemView.findViewById(R.id.listingPrice);
        }
    }
}