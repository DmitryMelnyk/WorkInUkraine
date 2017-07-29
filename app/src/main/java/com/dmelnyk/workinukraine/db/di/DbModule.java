package com.dmelnyk.workinukraine.db.di;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.dmelnyk.workinukraine.db.DbOpenHelper;
import com.dmelnyk.workinukraine.model.search_service.ISearchServiceRepository;
import com.dmelnyk.workinukraine.model.search_service.SearchServiceRepository;
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
    ISearchServiceRepository provideSearchRepository(BriteDatabase database) {
        return new SearchServiceRepository(database);
    }
}
