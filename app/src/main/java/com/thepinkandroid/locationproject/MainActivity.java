package com.thepinkandroid.locationproject;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;

public class MainActivity extends GoogleApiActivity implements View.OnClickListener
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Location mLastLocation;
    private TextView mLatitudeValueTextView;
    private TextView mLongitudeValueTextView;
    private Button mGetLastLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        askForLocationPermission();

        setContentView(R.layout.activity_main);
        setViews();
    }

    private void setViews()
    {
        mGetLastLocationButton = (Button) findViewById(R.id.getLastLocationButton);
        mGetLastLocationButton.setOnClickListener(this);
        mLatitudeValueTextView = (TextView) findViewById(R.id.latitudeValueTextView);
        mLongitudeValueTextView = (TextView) findViewById(R.id.longitudeValueTextView);
    }


    @Override
    public void onConnected(Bundle bundle)
    {
        // Connected to Google Play services!
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            Log.d(LOG_TAG, "Lat: " + String.valueOf(mLastLocation.getLatitude()));
            Log.d(LOG_TAG, "Lon: " + String.valueOf(mLastLocation.getLongitude()));
            mLatitudeValueTextView.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeValueTextView.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.getLastLocationButton:
                if (!mResolvingError)
                {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }
}
