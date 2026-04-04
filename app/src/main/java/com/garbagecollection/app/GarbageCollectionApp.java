package com.garbagecollection.app;

import android.app.Application;
import com.garbagecollection.app.util.AppLanguageManager;

public class GarbageCollectionApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppLanguageManager.applySavedLocale(this);
    }
}
