package com.example.dpivovar.runtracker;

import android.content.Context;
import android.util.Log;

/**
 * Created by dpivovar on 13.11.2015.
 */
public class RunLoader extends DataLoader<Run> {
    private static final String TAG = "RunLoader";
    private long mRunId;


    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        Log.d(TAG, "loadInBackground() in thread: " + Thread.currentThread().getName());
        return RunManager.get(getContext()).getRun(mRunId);
    }
}
