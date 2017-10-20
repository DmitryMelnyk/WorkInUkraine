package com.dmelnyk.workinukraine.services.periodic_search.di;

import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.services.periodic_search.AlarmClockUtil;
import com.dmelnyk.workinukraine.services.periodic_search.AlarmReceiver;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by d264 on 9/5/17.
 */

@Component(
        modules = {DbModule.class, RepeatingSearchModule.class}) //,
//        dependencies = SettingsComponent.class)

@Singleton
public interface RepeatingSearchComponent {
    void inject(AlarmReceiver receiver);
    void inject(AlarmClockUtil alarmClockUtil);
    void inject(RepeatingSearchJob repeatingSearchJob);
}
