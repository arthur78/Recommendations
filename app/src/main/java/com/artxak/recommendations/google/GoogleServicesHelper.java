package com.artxak.recommendations.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleServicesHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_AVAILABILITY = -101;
    private static final int REQUEST_CODE_RESOLUTION = -100;

    public interface GoogleServicesListener {
        public void onConnected();
        public void onDisconnected();
    }

//    private

    private GoogleServicesListener mListener;
    private Activity mActivity;
    private GoogleApiClient mApiClient;

    public GoogleServicesHelper(Activity activity, GoogleServicesListener listener) {
        mActivity = activity;
        mListener = listener;

        mApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void connect() {
        if (isGooglePlayServicesAvailable()) {
            mApiClient.connect();
        } else {
            mListener.onDisconnected();
        }
    }

    public void disconnect() {
        if (isGooglePlayServicesAvailable()) {
            mApiClient.disconnect();
        } else {
            mListener.onDisconnected();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int availability = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        switch (availability) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
                GooglePlayServicesUtil.getErrorDialog(availability,
                        mActivity, REQUEST_CODE_AVAILABILITY)
                    .show();
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mListener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mListener.onDisconnected();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                connect();
            }
        } else {
            mListener.onDisconnected();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE_AVAILABILITY || requestCode == REQUEST_CODE_RESOLUTION) {
            if (resultCode == Activity.RESULT_OK) {
                connect();
            } else {
                mListener.onDisconnected();
            }
        }
    }

}
