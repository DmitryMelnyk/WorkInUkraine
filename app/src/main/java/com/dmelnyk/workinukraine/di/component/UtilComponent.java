package com.dmelnyk.workinukraine.di.component;

import com.dmelnyk.workinukraine.di.PerActivity;
import com.dmelnyk.workinukraine.di.module.UtilModule;
import com.dmelnyk.workinukraine.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.parsing.ParserWorkUa;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by dmitry on 28.03.17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {UtilModule.class})
public interface UtilComponent {

    void inject(ParserJobsUa parser);
    void inject(ParserHeadHunters parser);
    void inject(ParserRabotaUa parser);
    void inject(ParserWorkUa parser);
    void inject(ParserWorkNewInfo parser);
}
