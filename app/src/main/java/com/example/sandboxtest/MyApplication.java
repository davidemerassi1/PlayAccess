package com.example.sandboxtest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.room.Room;

import com.example.sandboxtest.actionsConfigurator.OverlayView;
import com.example.sandboxtest.database.AssociationsDb;

import java.util.HashMap;
import java.util.Map;

import it.unimi.di.ewlab.iss.common.model.MainModel;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback;
import top.niunaijun.blackbox.app.configuration.ClientConfiguration;

public class MyApplication extends Application {
    private AssociationsDb database;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            BlackBoxCore.get().doAttachBaseContext(base, new ClientConfiguration() {
                @Override
                public String getHostPackageName() {
                    return base.getPackageName();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlackBoxCore.get().addAppLifecycleCallback(new AppLifecycleCallback() {
            int runningActivities = 0;
            @Override
            public void afterApplicationOnCreate(String packageName, String processName, Application application, int userId) {
                super.afterApplicationOnCreate(packageName, processName, application, userId);
                Intent intent = new Intent("com.example.sandboxtest.ACTION_CREATE_OVERLAY");
                intent.setPackage(packageName);
                sendBroadcast(intent);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                runningActivities++;
                if (runningActivities == 1) {
                    Intent intent = new Intent("com.example.sandboxtest.ACTION_SHOW_OVERLAY");
                    intent.setPackage(activity.getPackageName());
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                super.onActivityPaused(activity);
                runningActivities--;
                if (runningActivities == 0) {
                    Intent intent = new Intent("com.example.sandboxtest.ACTION_HIDE_OVERLAY");
                    intent.setPackage(activity.getPackageName());
                    activity.sendBroadcast(intent);
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                super.onActivityDestroyed(activity);
                if (runningActivities == 0) {
                    Intent intent = new Intent("com.example.sandboxtest.ACTION_DESTROY_OVERLAY");
                    intent.setPackage(activity.getPackageName());
                    sendBroadcast(intent);
                }
            }
        });

        BlackBoxCore.get().doCreate();


        database = Room.databaseBuilder(this, AssociationsDb.class, "associations")
                .fallbackToDestructiveMigration()
                .build();

        MainModel.getInstance(this).initFolders(this);
        MainModel.getInstance().loadScreenGestures();
    }

    public AssociationsDb getDatabase() {
        return database;
    }
}
