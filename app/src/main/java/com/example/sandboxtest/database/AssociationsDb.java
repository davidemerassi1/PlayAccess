package com.example.sandboxtest.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Association.class}, version = 5)
public abstract class AssociationsDb extends RoomDatabase {
    public abstract AssociationDao getDao();
}
