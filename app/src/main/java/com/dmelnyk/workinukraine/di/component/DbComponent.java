package com.dmelnyk.workinukraine.di.component;

import com.dmelnyk.workinukraine.helpers.CardViewAdapter;
import com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivityPresenter;
import com.dmelnyk.workinukraine.mvp.activity_search.SearchActivityPresenter;
import com.dmelnyk.workinukraine.mvp.activity_splash.SplashActivityPresenter;
import com.dmelnyk.workinukraine.mvp.activity_tabs.TabsActivityPresenter;
import com.dmelnyk.workinukraine.di.PerActivity;
import com.dmelnyk.workinukraine.di.module.DbModule;
import com.dmelnyk.workinukraine.mvp.activity_webview.WebActivityPresenter;
import com.dmelnyk.workinukraine.mvp.dialog_delete.DialogDeletePresenter;
import com.dmelnyk.workinukraine.services.GetDataIntentService;
import com.dmelnyk.workinukraine.services.WakeLockBroadcastReceiver;

import dagger.Component;

/**
 * Created by dmitry on 30.03.17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = DbModule.class)
public interface DbComponent {
    void inject(GetDataIntentService service);

    void inject(WakeLockBroadcastReceiver receiver);

    void inject(BaseActivityPresenter presenter);

    void inject(SearchActivityPresenter presenter);

    void inject(TabsActivityPresenter presenter);

    void inject(DialogDeletePresenter presenter);

    void inject(SplashActivityPresenter presenter);

    void inject(CardViewAdapter context);

    void inject(WebActivityPresenter presenter);
}
