package com.thepinkandroid.locationproject;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by DAVID-WORK on 06/02/2016.
 */
public abstract class GoogleApiActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    protected GoogleApiClient mGoogleApiClient;

    // Bool to track whether the app is already resolving an error
    protected boolean mResolvingError = false;

    // Request code to use when launching the resolution activity
    protected static final int REQUEST_RESOLVE_ERROR = 1001;

    // Unique tag for the error dialog fragment
    protected static final String DIALOG_ERROR = "dialog_error";

    protected static final String STATE_RESOLVING_ERROR = "resolving_error";

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void askForLocationPermission()
    {
        // Ask for location permissions
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        // permission was granted!
        createGoogleApiClientInstance();
    }

    protected void createGoogleApiClientInstance()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.

        if (mResolvingError)
        {
            // Already attempting to resolve an error.
            return;
        }
        else if (connectionResult.hasResolution())
        {
            try
            {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            catch (IntentSender.SendIntentException e)
            {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        }
        else
        {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted!
                    createGoogleApiClientInstance();
                }
                else
                {
                    // permission denied! Disable the functionality that depends on this permission.
                    Toast.makeText(this, getString(R.string.no_location_permission), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment
    {
        public ErrorDialogFragment()
        {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog)
        {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }

    /* Creates a dialog for an error message */
    protected void showErrorDialog(int errorCode)
    {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    protected void onDialogDismissed()
    {
        mResolvingError = false;
    }
}
