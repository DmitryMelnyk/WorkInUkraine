package com.dmelnyk.workinukraine.services.periodic_search.di;

import android.content.Context;

import com.dmelnyk.workinukraine.services.periodic_search.repo.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.services.periodic_search.repo.RepeatingSearchRepository;
import com.dmelnyk.workinukraine.ui.settings.repository.SettingsRepository;
import com.dmelnyk.workinukraine.services.periodic_search.AlarmClockUtil;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
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
            BriteDatabase db, SettingsRepository settingsRepo, SharedPrefUtil sharedPrefUtil) {
        return new RepeatingSearchRepository(db, settingsRepo, sharedPrefUtil);
    }
}
