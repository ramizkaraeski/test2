package com.delivery.nearby;

import androidx.multidex.MultiDexApplication;

import com.delivery.nearby.utils.Preferences;

public class AppClass extends MultiDexApplication {

    public static Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(this);
    }
}
