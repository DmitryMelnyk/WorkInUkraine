package com.dmelnyk.workinukraine.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.db.JobDbSchema.JobTable.Columns;
import com.dmelnyk.workinukraine.helpers.Job;

import java.util.ArrayList;

/**
 * Created by dmitry on 26.01.17.
 */

// Class for writing / extracting data to / from db
public class JobPool {
    private static final String TAG = "GT.JobPool";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public JobPool(Context context) {
        Log.d(TAG, "created new JobPool");
        mContext = context.getApplicationContext();
        mDatabase = new DBHelper(mContext)
                .getWritableDatabase();
    }

    public void addJobs(ArrayList<Job> jobs, String table) {
        if (jobs != null) {
               for (Job job : jobs) {
                   addJob(table, job);
               }
        }
    }

    /**
     * @param table
     * @param job
     * @return true when job has been successfully added and false if table contains job
     */
    public boolean addJob(String table, Job job) {
        boolean isJobInDb = isJobInTable(table, job);
        if (isJobInDb) {
            return false;
        }
        mDatabase.insert(table,
                null, getContentValue(job));

        return true;
    }

    private boolean isJobInTable(String table, Job job) {
        Cursor cursor = mDatabase.query(
                table,
                null, // select all Columns
                Columns.URL + "=?",
                new String[]{job.getUrlCode()},
                null, //groupBy
                null, //having
                null); //orderBy

        JobCursorWrapper jobCursor = new JobCursorWrapper(cursor);
        return (jobCursor != null && cursor.moveToFirst()) ? true : false;

    }

    @NonNull
    public ArrayList<Job> getJobs(String table) {
        Log.d(TAG, "start getJobs()");
        Cursor cursor = mDatabase.query(
                table,
                null, //select all columns
                null, //whereClause
                null, //whereArgs
                null, //groupBy
                null, //having
                null); //orderBy

        JobCursorWrapper jobCursor = new JobCursorWrapper(cursor);
        ArrayList<Job> jobs = new ArrayList<>();

        if (jobCursor != null && cursor.moveToFirst()) {
            while (!jobCursor.isAfterLast()) {
                jobs.add(jobCursor.getJob());
//                Log.d(TAG, "extract from db:" + table + "job: " + jobCursor.getJob());
                jobCursor.moveToNext();
            }
        }
        jobCursor.close();
        Log.d(TAG, String.format("TABLE = %s Extracted number of jobs from = %d", table, jobs.size()));
        return jobs;
    }

    public Bundle getAllJobs() {
        Bundle args = new Bundle();
        for (String table : JobDbSchema.JobTable.NAMES) {
            ArrayList<Job> jobs = getJobs(table);
            if (jobs.size() > 0) {
                args.putParcelableArrayList(table, jobs);
            }
        }

        return args;
    }

    public void writeAllJobs(Bundle args) {
        clearDb();
        Log.d(TAG, "Calling writeAllJobs(Bundle)");
        for (String tableName : JobDbSchema.JobTable.NAMES) {
            ArrayList<Job> jobs = args.getParcelableArrayList(tableName);
            addJobs(jobs, tableName);
        }
    }

    public void clearDb() {
        Log.d(TAG, "clearing db");
        for (String table : JobDbSchema.JobTable.NAMES) {
            clearTable(table);
        }
    }

    public void clearTable(String table) {
        try {
            mDatabase.delete(
                    table,
                    null,
                    null);
        } catch (Exception e) {
            Log.e(TAG, "clearing db error: ", e);
        }
    }

    private static ContentValues getContentValue(Job job) {
        ContentValues content = new ContentValues();
        content.put(Columns.TITLE, job.getTitle());
        content.put(Columns.DATE, job.getDate());
        content.put(Columns.URL, job.getUrlCode());

        return content;
    }

    public void closeDb() {
        Log.d(TAG, "DataBase has been closed!");
        mDatabase.close();
    }


    /**
     * Removes job from MENU_TYPE_FAVORITE table
     * @param job
     */
    public void removeJobFromFavorite(Job job) {
//        Log.d(TAG, "removing from MENU_TYPE_FAVORITE:" + job);
        try {
            mDatabase.delete(
                    JobDbSchema.JobTable.FAVORITE,
                    Columns.URL + "=?",
                    new String[]{job.getUrlCode()}
            );
        } catch (Exception e) {
            Log.e(TAG, "clearing item error: ", e);
        }
    }

    public boolean containsJob(String table, Job job) {
        ArrayList jobs = getJobs(table);
        return jobs.contains(job);
    }
}
