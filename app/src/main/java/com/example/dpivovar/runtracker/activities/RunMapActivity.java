package com.example.dpivovar.runtracker.activities;

import android.support.v4.app.Fragment;

import com.example.dpivovar.runtracker.fragments.RunMapFragment;

/**
 * Created by dpivovar on 16.11.2015.
 */
public class RunMapActivity extends SingleFragmentActivity {
    public static final String EXTRA_RUN_ID = "com.example.dpivovar.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1) {
            return RunMapFragment.newInstance(runId);
        } else {
            return new RunMapFragment();
        }
    }
}
