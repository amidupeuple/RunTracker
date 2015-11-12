package com.example.dpivovar.runtracker;

import android.support.v4.app.Fragment;

/**
 * Created by dpivovar on 11.11.2015.
 */
public class RunListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
