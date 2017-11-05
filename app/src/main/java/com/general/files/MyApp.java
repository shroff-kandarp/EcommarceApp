package com.general.files;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.ecommarceapp.R;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;

//import android.support.multidex.MultiDex;

/**
 * Created by Admin on 28-06-2016.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        setScreenOrientation();
//        MultiDex.install(this);
    }

    public void setScreenOrientation() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {

                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activity.setTitle(getResources().getString(R.string.app_name));

                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }


        });
    }
}
