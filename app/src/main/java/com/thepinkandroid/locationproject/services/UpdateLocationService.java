package com.thepinkandroid.locationproject.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.thepinkandroid.locationproject.custom.RunnableToast;

import java.util.concurrent.TimeUnit;

/**
 * Created by DAVID-WORK on 07/02/2016.
 */
public class UpdateLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    private static final String LOG_TAG = UpdateLocationService.class.getSimpleName();
    private Handler mHandler;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    // Stores parameters for requests to the FusedLocationProviderApi.
    protected LocationRequest mLocationRequest;

    // The desired interval for location updates [50ms] . Inexact. Updates may be more or less frequent.
    protected static final long UPDATE_INTERVAL_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(10);

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    protected static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = this;
        mHandler = new Handler();
        setLocationRequestObject();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Get the last location
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     */
    protected void setLocationRequestObject()
    {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        // Connected to Google Play services!
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null)
        {
            Log.d(LOG_TAG, "Lat: " + String.valueOf(lastLocation.getLatitude()));
            Log.d(LOG_TAG, "Lon: " + String.valueOf(lastLocation.getLongitude()));
            mHandler.post(new RunnableToast(mContext, "Got last location"));
        }

        // register to location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(LOG_TAG, "Lat: " + String.valueOf(location.getLatitude()));
        Log.d(LOG_TAG, "Lon: " + String.valueOf(location.getLongitude()));
        mHandler.post(new RunnableToast(mContext, "Location changed!: "
                + String.valueOf(location.getLatitude()) + ","
                + String.valueOf(location.getLongitude())));

        // TODO: Check if the the order is still in stated OTW
        // TODO: If yes, send update to server, and sleep/wait for 50 seconds
        // TODO: If no, break ans self stop
    }

}
