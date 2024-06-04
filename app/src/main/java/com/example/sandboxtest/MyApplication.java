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
        String sandboxPackageName = SandboxVerifier.getSandboxPackageName(this);
        MainModel.getInstance().setSandboxPackageName(sandboxPackageName);
        MainModel.getInstance().setSandboxName(getSandboxName(sandboxPackageName));
    }

    private String getSandboxName(String packageName) {
        if (packageName == null) {
            return null;
        }
        PackageManager packageManager = getPackageManager();
        String appName = "your sandbox";
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            appName = packageManager.getApplicationLabel(applicationInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }
}
