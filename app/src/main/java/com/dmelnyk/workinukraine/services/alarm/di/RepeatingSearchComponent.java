package com.dmelnyk.workinukraine.services.alarm.di;

import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.alarm.AlarmClockUtil;
import com.dmelnyk.workinukraine.services.alarm.AlarmReceiver;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by d264 on 9/5/17.
 */

@Component(modules = {DbModule.class, RepeatingSearchModule.class})
@Singleton
public interface RepeatingSearchComponent {
    void inject(AlarmReceiver receiver);
    void inject(AlarmClockUtil alarmClockUtil);
}
