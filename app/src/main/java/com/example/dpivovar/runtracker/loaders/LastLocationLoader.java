package com.example.dpivovar.runtracker.loaders;

import android.content.Context;
import android.location.Location;

import com.example.dpivovar.runtracker.services.RunManager;

/**
 * Created by dpivovar on 16.11.2015.
 */
public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;

    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(mRunId);
    }

}
