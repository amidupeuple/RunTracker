package com.example.dpivovar.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by dpivovar on 06.11.2015.
 */
public class RunManager {
    private static final String TAG = "RunManager";

    public static final String PREFS_FILE = "runs";
    public static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";

    public static final String ACTION_LOCATION = "com.example.dpivovar.runtracker.ACTION_LOCATION";
    public static final String TEST_PROVIDER = "TEST_PROVIDER";

    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;



    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public static RunManager get(Context c) {
        Log.d(TAG, "+ enter get()");
        if (sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }

        return sRunManager;
    }

    public Run startNewRun() {
        //insert a run into the db
        Run run = insertRun();

        //start tracking the run
        startTrackingRun(run);

        return run;
    }

    public void startTrackingRun(Run run) {
        //keep the id
        mCurrentRunId = run.getId();

        //store it in shared preferences
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();

        //start location updates
        startLocationUpdates();
    }

    public void stopRun() {
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    private Run insertRun() {
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public RunDatabaseHelper.RunCursor queryRuns() {
        return mHelper.queryRuns();
    }

    public void insertLocation(Location loc) {
        if (mCurrentRunId != -1) {
            mHelper.insertLocation(mCurrentRunId, loc);
        } else {
            Log.e(TAG, "Location received wth no tracking run; ignoring.");
        }
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;

        return  PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        Log.d(TAG, "START location updates");
        String provider = LocationManager.GPS_PROVIDER;

        //If you have test provider and it's enabled, use it
        if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
            provider = TEST_PROVIDER;
        }
        Log.d(TAG, "Using provider: " + provider);

        //Get last known location and broadcast it if you have one
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            //Reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastlocation(lastKnown);
        }

        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    public void stopLocationUpdates() {
        Log.d(TAG, "STOP location updates");
        PendingIntent pi = getLocationPendingIntent(false);

        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    private void broadcastlocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public boolean isTrackingRun() {
        Log.d(TAG, "+ isTrackingRun()");
        PendingIntent tmp = getLocationPendingIntent(false);
        Log.d(TAG, "Pending intent is null: " + (tmp == null));
        return tmp != null;
    }

    public Run getRun(long id) {
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            run = cursor.getRun();
        }
        cursor.close();

        return run;
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentRunId;
    }

    public Location getLastLocationForRun(long runId) {
        Location location = null;
        RunDatabaseHelper.LocationCursor cursor = mHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            location = cursor.getLocation();
        }

        cursor.close();
        return location;
    }
}
