package com.artxak.recommendations.model;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.artxak.recommendations.MainActivity;
import com.artxak.recommendations.R;
import com.artxak.recommendations.api.Etsy;
import com.artxak.recommendations.google.GoogleServicesHelper;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
        implements Callback<ActiveListings>, GoogleServicesHelper.GoogleServicesListener {

    private MainActivity mActivity;
    private LayoutInflater mLayoutInflater;
    private ActiveListings mActiveListings;

    private boolean mIsGooglePlayServicesAvailable;

    public static final int REQUEST_CODE_PLUS_ONE = 10;
    public static final int REQUEST_CODE_SHARE = 11;

    public ListingAdapter(MainActivity activity) {
        mActivity = activity;
        mLayoutInflater = LayoutInflater.from(activity);
        mIsGooglePlayServicesAvailable = false;
    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListingHolder(mLayoutInflater.inflate(R.layout.layout_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(ListingHolder holder, int position) {
        final Listing listing = mActiveListings.results[position];
        holder.mListingTitle.setText(listing.title);
        holder.mListingShopName.setText(listing.Shop.shop_name);
        holder.mListingPrice.setText(listing.price);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openListing = new Intent(Intent.ACTION_VIEW);
                openListing.setData(Uri.parse(listing.url));
                mActivity.startActivity(openListing);
            }
        });

        if (mIsGooglePlayServicesAvailable) {
            holder.mPlusOneButton.setVisibility(View.VISIBLE);
            holder.mPlusOneButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE);
            holder.mPlusOneButton.setAnnotation(PlusOneButton.ANNOTATION_NONE);

            holder.mShareListingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new PlusShare.Builder(mActivity)
                            .setType("text/plain")
                            .setText("Check out that item on Etsy: " + listing.title)
                            .setContentUrl(Uri.parse(listing.url))
                            .getIntent();

                    mActivity.startActivityForResult(intent, REQUEST_CODE_SHARE);
                }
            });
        } else {
            holder.mPlusOneButton.setVisibility(View.GONE);
            holder.mShareListingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Checkout this item on Etsy: " +
                        listing.title + " " + listing.url);
                    intent.setType("text/plain");

                    mActivity.startActivityForResult(
                            Intent.createChooser(intent, "Share"),
                            REQUEST_CODE_SHARE);

                }
            });
        }

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

    @Override
    public void onConnected() {

        if (getItemCount() == 0) {
            Etsy.getActiveListings(this);
        }

        mIsGooglePlayServicesAvailable = true;
        notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {

        if (getItemCount() == 0) {
            Etsy.getActiveListings(this);
        }

        mIsGooglePlayServicesAvailable = false;
        notifyDataSetChanged();  // Make sure we update the RecyclerView's items.
    }

    public class ListingHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mListingTitle;
        public TextView mListingShopName;
        public TextView mListingPrice;
        public PlusOneButton mPlusOneButton;
        public ImageView mShareListingButton;

        public ListingHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.listingImage);
            mListingTitle = (TextView) itemView.findViewById(R.id.listingTitle);
            mListingShopName = (TextView) itemView.findViewById(R.id.listingShopName);
            mListingPrice = (TextView) itemView.findViewById(R.id.listingPrice);
            mPlusOneButton = (PlusOneButton) itemView.findViewById(R.id.listingPlusOneButton);
            mShareListingButton = (ImageView) itemView.findViewById(R.id.listingShareButton);

        }
    }
}
