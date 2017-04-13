package com.dmelnyk.workinukraine.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.dmelnyk.workinukraine.db.JobDbSchema.JobTable.Columns;
import com.dmelnyk.workinukraine.helpers.Job;

/**
 * Created by dmitry on 26.01.17.
 */

public class JobCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public JobCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Job getJob() {
        String title = getString(getColumnIndex(Columns.TITLE));
        String date = getString(getColumnIndex(Columns.DATE));
        String url = getString(getColumnIndex(Columns.URL));

        Job job = new Job(title, date, url);

        return job;
    }
}
