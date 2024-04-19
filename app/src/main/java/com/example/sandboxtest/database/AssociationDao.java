package com.example.sandboxtest.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AssociationDao {
    @Insert
    void insert(Association association);

    @Query("SELECT * FROM Association WHERE applicationPackage = :applicationPackage")
    Association[] getAssociations(String applicationPackage);

    @Query("DELETE FROM Association WHERE applicationPackage = :applicationPackage")
    void deleteAssociations(String applicationPackage);
}
