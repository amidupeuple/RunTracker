package com.example.dpivovar.runtracker;

import android.support.v4.app.Fragment;

/**
 * Created by dpivovar on 06.11.2015.
 */
public class RunActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunFragment();
    }
}
