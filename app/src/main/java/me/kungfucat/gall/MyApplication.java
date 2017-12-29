package me.kungfucat.gall;

import android.app.Application;

import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * Created by harsh on 12/27/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        CaocConfig.Builder
//                .create()
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
//                .showErrorDetails(true)
//                .apply();
    }
}
