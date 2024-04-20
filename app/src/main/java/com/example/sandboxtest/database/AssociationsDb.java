package com.example.sandboxtest.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Association.class}, version = 2)
public abstract class AssociationsDb extends RoomDatabase {
    public abstract AssociationDao getDao();
}
