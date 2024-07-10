package com.example.sandboxtest;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MainModel.getInstance(this).initFolders(this);
    }
}
