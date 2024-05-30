package com.example.sandboxtest;
import android.app.Application;
import android.util.Log;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MainModel.getInstance(this).initFolders(this);
        MainModel.getInstance().loadScreenGestures();
        MainModel.getInstance().setSandboxPackageName(SandboxVerifier.getSandboxPackageName(this));
    }
}
