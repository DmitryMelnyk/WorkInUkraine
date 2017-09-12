package com.dmelnyk.workinukraine.services.alarm.di;

import android.content.Context;

import com.dmelnyk.workinukraine.data.repeating_search_service.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.data.repeating_search_service.RepeatingSearchRepository;
import com.squareup.sqlbrite2.BriteDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 9/5/17.
 */

@Module
public class RepeatingSearchModule {

    @Provides
    @RepeatingSearchScope
    IRepeatingSearchRepository providesIRepeatingSearchRepository(BriteDatabase db, Context context) {
        return new RepeatingSearchRepository(db, context);
    }


}
