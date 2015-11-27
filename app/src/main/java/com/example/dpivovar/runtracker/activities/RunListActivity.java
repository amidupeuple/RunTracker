package com.example.dpivovar.runtracker.activities;

import android.support.v4.app.Fragment;

import com.example.dpivovar.runtracker.fragments.RunListFragment;

/**
 * Created by dpivovar on 11.11.2015.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
