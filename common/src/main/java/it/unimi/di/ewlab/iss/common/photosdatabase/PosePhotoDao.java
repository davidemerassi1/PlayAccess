package it.unimi.di.ewlab.iss.common.photosdatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.unimi.di.ewlab.iss.common.photosdatabase.PosePhoto;

@Dao
public interface PosePhotoDao {
        @Query("SELECT * FROM posePhoto")
        List<PosePhoto> getAll();

        @Delete
        void delete(PosePhoto posePhoto);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(PosePhoto posePhoto);

        @Query("SELECT * FROM posephoto WHERE poseName == :poseName")
        PosePhoto getByPoseName(String poseName);

        @Query("SELECT * FROM posephoto WHERE poseName IN (:labels)")
        List<PosePhoto> getByLabelsList(List<String> labels);

        @Query("DELETE FROM posephoto")
        void nukeTable();
}
