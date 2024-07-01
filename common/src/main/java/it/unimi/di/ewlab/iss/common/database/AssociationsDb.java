package it.unimi.di.ewlab.iss.common.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Association.class}, version = 9)
@TypeConverters(ActionConverter.class)
public abstract class AssociationsDb extends RoomDatabase {
    public abstract AssociationDao getDao();
}
