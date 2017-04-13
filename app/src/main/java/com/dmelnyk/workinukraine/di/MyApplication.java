package com.dmelnyk.workinukraine.di;

import android.app.Application;
import android.content.Context;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.di.component.ApplicationComponent;
import com.dmelnyk.workinukraine.di.component.DaggerApplicationComponent;
import com.dmelnyk.workinukraine.di.module.ApplicationModule;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by dmitry on 30.03.17.
 */


@ReportsCrashes(customReportContent = {
        ReportField.REPORT_ID,
        ReportField.APP_VERSION_CODE,
        ReportField.APP_VERSION_NAME,
        ReportField.PACKAGE_NAME,
        ReportField.PHONE_MODEL,
        ReportField.ANDROID_VERSION,
        ReportField.STACK_TRACE,
        ReportField.TOTAL_MEM_SIZE,
        ReportField.AVAILABLE_MEM_SIZE,
        ReportField.DISPLAY,
        ReportField.USER_APP_START_DATE,
        ReportField.USER_CRASH_DATE,
        ReportField.LOGCAT,
        ReportField.DEVICE_ID,
        ReportField.SHARED_PREFERENCES
},
        mailTo = "namylo87@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
//        resDialogTheme = R.style.AppTheme_Dialog,
        resDialogCommentPrompt = R.string.crash_dialog_comment_promt,
//                    resDialogEmailPrompt = R.string.crash_user_email_label,
        resDialogOkToast = R.string.crash_dialog_ok_toast
)

public class MyApplication extends Application {

    protected ApplicationComponent appComponent;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        appComponent.inject(this);
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
