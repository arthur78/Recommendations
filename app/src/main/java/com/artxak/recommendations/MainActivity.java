package com.artxak.recommendations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.artxak.recommendations.api.Etsy;
import com.artxak.recommendations.model.ActiveListings;
import com.artxak.recommendations.model.ListingAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String STATE_ACTIVE_LISTINGS = "state_active_listings";

    private RecyclerView mRecyclerView;
    private View mProgressBar;
    private TextView mErrorView;
    private ListingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);
        mErrorView = (TextView) findViewById(R.id.errorView);

        // Setup RecyclerView.
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ListingAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            showLoading();
            Etsy.getActiveListings(mAdapter);
        } else {
            if (savedInstanceState.containsKey(STATE_ACTIVE_LISTINGS)) {
                mAdapter.success((ActiveListings) savedInstanceState.getParcelable(STATE_ACTIVE_LISTINGS), null);
            } else {
                showLoading();
                Etsy.getActiveListings(mAdapter);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ActiveListings activeListings = mAdapter.getActiveListings();
        if (activeListings != null) {
            outState.putParcelable(STATE_ACTIVE_LISTINGS, activeListings);
        }
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    public void showList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    public void showError() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }
}
