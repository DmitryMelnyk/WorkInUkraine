package com.dmelnyk.workinukraine.services.alarm.di;

import android.content.Context;

import com.dmelnyk.workinukraine.data.repeating_search_service.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.data.repeating_search_service.RepeatingSearchRepository;
import com.dmelnyk.workinukraine.data.settings.ISettingsRepository;
import com.dmelnyk.workinukraine.data.settings.SettingsRepository;
import com.dmelnyk.workinukraine.services.alarm.AlarmClockUtil;
import com.squareup.sqlbrite2.BriteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 9/5/17.
 */

@Module
public class RepeatingSearchModule {

    @Provides
    @RepeatingSearchScope
    AlarmClockUtil proAlarmClockUtil(Context context) {
        return new AlarmClockUtil(context);
    }

    @Provides
    @RepeatingSearchScope
    IRepeatingSearchRepository providesIRepeatingSearchRepository(
            BriteDatabase db, Context context, SettingsRepository settingsRepo) {
        return new RepeatingSearchRepository(db, context, settingsRepo);
    }
}
