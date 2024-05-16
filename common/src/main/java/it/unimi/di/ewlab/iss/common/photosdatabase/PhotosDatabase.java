package it.unimi.di.ewlab.iss.common.photosdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PosePhoto.class}, version = 1)
public abstract class PhotosDatabase extends RoomDatabase {
    public abstract PosePhotoDao posePhotoDao();
}