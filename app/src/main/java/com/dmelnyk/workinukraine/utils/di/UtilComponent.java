package com.dmelnyk.workinukraine.utils.di;

import com.dmelnyk.workinukraine.application.ApplicationComponent;
import com.dmelnyk.workinukraine.application.ApplicationScope;
import com.dmelnyk.workinukraine.utils.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.utils.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.utils.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.utils.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.utils.parsing.ParserWorkUa;

import dagger.Component;

/**
 * Created by dmitry on 28.03.17.
 */

@ApplicationScope
@Component(dependencies = ApplicationComponent.class,
        modules = {UtilModule.class})
public interface UtilComponent {

    void inject(ParserJobsUa parser);
    void inject(ParserHeadHunters parser);
    void inject(ParserRabotaUa parser);
    void inject(ParserWorkUa parser);
    void inject(ParserWorkNewInfo parser);
}
