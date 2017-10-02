package com.dmelnyk.workinukraine.services.alarm.di;

import android.content.Context;

import com.dmelnyk.workinukraine.services.alarm.repo.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.services.alarm.repo.RepeatingSearchRepository;
import com.dmelnyk.workinukraine.ui.settings.repository.SettingsRepository;
import com.dmelnyk.workinukraine.services.alarm.AlarmClockUtil;
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
    AlarmClockUtil proAlarmClockUtil(Context context) {
        return new AlarmClockUtil(context);
    }

    @Provides
    @RepeatingSearchScope
    IRepeatingSearchRepository providesIRepeatingSearchRepository(
            BriteDatabase db, SettingsRepository settingsRepo) {
        return new RepeatingSearchRepository(db, settingsRepo);
    }
}
