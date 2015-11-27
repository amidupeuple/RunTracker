package com.example.dpivovar.runtracker.receivers;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.dpivovar.runtracker.services.RunManager;

/**
 * Created by dpivovar on 11.11.2015.
 */
public class TrackingLocationReceiver extends LocationReceiver {
    private static final String TAG = "TrackingLocReceiver";

    @Override
    protected void onLocationReceived(Context context, Location location) {
        Log.d(TAG, "save location in db");
        RunManager.get(context).insertLocation(location);
    }
}
