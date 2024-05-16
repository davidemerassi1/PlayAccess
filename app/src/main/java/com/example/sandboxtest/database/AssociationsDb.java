package com.example.sandboxtest.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Association.class}, version = 8)
@TypeConverters(ActionConverter.class)
public abstract class AssociationsDb extends RoomDatabase {
    public abstract AssociationDao getDao();
}
