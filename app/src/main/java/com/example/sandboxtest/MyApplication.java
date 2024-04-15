package com.example.sandboxtest;

import android.app.Application;
import android.content.Context;

import com.fvbox.lib.FCore;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        FCore.get().init(this);
        FCore.get().setAllowSystemInteraction(true);
        FCore.get().setAutoPreloadApplication(true);
        FCore.get().setEnableLauncherView(true);
        if(FCore.isClient()) {
            return;
        }
        // do something...
        // Add the code you want to execute during initialization here
        // For example:
        // MyInitializationClass.init(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Other initialization code that you want to execute when the application is created
    }
}
