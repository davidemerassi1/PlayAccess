package com.example.sandboxtest;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.example.sandboxtest.database.AssociationsDb;

import top.niunaijun.blackbox.BlackBoxCore;
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
        BlackBoxCore.get().doCreate();

        database = Room.databaseBuilder(this, AssociationsDb.class, "associations")
                .fallbackToDestructiveMigration()
                .build();
    }

    public AssociationsDb getDatabase() {
        return database;
    }
}
