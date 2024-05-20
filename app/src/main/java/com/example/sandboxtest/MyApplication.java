package com.example.sandboxtest;
import android.app.Application;

import androidx.room.Room;
import com.example.sandboxtest.database.AssociationsDb;

import it.unimi.di.ewlab.iss.common.model.MainModel;

public class MyApplication extends Application {
    private AssociationsDb database;

    @Override
    public void onCreate() {
        super.onCreate();

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
