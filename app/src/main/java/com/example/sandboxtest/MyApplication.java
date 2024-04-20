package com.example.sandboxtest;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.example.sandboxtest.database.AssociationsDb;
import com.lody.virtual.client.core.VirtualCore;

public class MyApplication extends Application {
    private AssociationsDb database;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            VirtualCore.get().startup(base);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {
            @Override
            public void onMainProcess() {
                // Main process callback
            }

            @Override
            public void onVirtualProcess() {
                // Virtual App process callback
            }

            @Override
            public void onServerProcess() {
                // Server-side process callback
            }

            @Override
            public void onChildProcess() {
                // Other sub-process callback
            }
        });

        database = Room.databaseBuilder(this, AssociationsDb.class, "associations")
                .fallbackToDestructiveMigration()
                .build();
    }

    public AssociationsDb getDatabase() {
        return database;
    }
}
