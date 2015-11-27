package com.example.dpivovar.runtracker.loaders;

import android.content.Context;
import android.database.Cursor;

import com.example.dpivovar.runtracker.services.RunManager;

/**
 * Created by dpivovar on 27.11.2015.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;

    public LocationListCursorLoader(Context c, long runId) {
        super(c);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }
}
