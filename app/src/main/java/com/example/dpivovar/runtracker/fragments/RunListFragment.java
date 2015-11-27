package com.example.dpivovar.runtracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dpivovar.runtracker.R;
import com.example.dpivovar.runtracker.objects.Run;
import com.example.dpivovar.runtracker.services.RunManager;
import com.example.dpivovar.runtracker.loaders.SQLiteCursorLoader;
import com.example.dpivovar.runtracker.activities.RunActivity;

import static com.example.dpivovar.runtracker.services.RunDatabaseHelper.*;

/**
 * Created by dpivovar on 11.11.2015.
 */
public class RunListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int REQUEST_NEW_RUN = 0;
    private static final String TAG = "RunListFragment";
    private RunCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "+ start onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_run :
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_NEW_RUN == requestCode) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), (RunCursor) data);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    private static class RunCursorAdapter extends CursorAdapter {
        private RunCursor mRunCursor;
        private long prevRunId = -1;

        public RunCursorAdapter(Context context, RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Run run = mRunCursor.getRun();

            TextView startDateTextView = (TextView) view;
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTextView.setText(cellText);

            SharedPreferences prefs = context.getSharedPreferences(RunManager.PREFS_FILE, Context.MODE_PRIVATE);
            long currentRunId = prefs.getLong(RunManager.PREF_CURRENT_RUN_ID, -1);
            //Log.d(TAG, "Current run id: " + currentRunId);

            if (run.getId() == prevRunId && run.getId() != currentRunId) {
                Log.d(TAG, "highlight current run");
                startDateTextView.setTextColor(Color.WHITE);
                prevRunId = -1;
            } else if (run.getId() == currentRunId) {
                Log.d(TAG, "highlight current run");
                startDateTextView.setTextColor(Color.GREEN);
                prevRunId = currentRunId;
            }
        }
    }

    private static class RunListCursorLoader extends SQLiteCursorLoader {
        public RunListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            return RunManager.get(getContext()).queryRuns();
        }


    }
}
