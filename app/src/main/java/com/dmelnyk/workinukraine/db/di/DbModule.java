package com.dmelnyk.workinukraine.db.di;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.dmelnyk.workinukraine.db.DbOpenHelper;
import com.dmelnyk.workinukraine.services.search.repository.ISearchServiceRepository;
import com.dmelnyk.workinukraine.services.search.repository.SearchServiceRepository;
import com.dmelnyk.workinukraine.utils.SharedPrefFilterUtil;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by d264 on 7/21/17.
 */

@Module
public class DbModule {
    private static final String TAG = "DATABASE";

    private final Context application;

    public DbModule(Context context) {
        this.application = context.getApplicationContext();
    }

    @Provides @Singleton Context provideApplication() {
        return application;
    }

    @Provides @Singleton
    SQLiteOpenHelper provideSqLiteOpenHelper(Context context) {
        return new DbOpenHelper(context);
    }

    @Provides @Singleton
    SqlBrite provideSqlBrite() {
        return new SqlBrite.Builder()
                .logger(new SqlBrite.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.tag(TAG).v(message);
                    }
                })
                .build();
    }

    @Provides @Singleton
    BriteDatabase provideBriteDatabase(SqlBrite sqlBrite, SQLiteOpenHelper openHelper) {
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(openHelper, Schedulers.io());
        return db;
    }

    @Provides @Singleton
    SharedPrefUtil providesSharedPrefUtil(Context context) {
        return new SharedPrefUtil(context);
    }

    @Provides @Singleton
    ISearchServiceRepository provideSearchRepository(BriteDatabase database,
                                                     SharedPrefUtil sharedPrefUtil,
                                                     SharedPrefFilterUtil filterUtil) {
        return new SearchServiceRepository(database, sharedPrefUtil, filterUtil);
    }
}
