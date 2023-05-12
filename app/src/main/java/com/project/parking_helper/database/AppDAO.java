package com.project.parking_helper.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AppDAO {
    // Insert the data into the database
    @Insert
    void insert(AppData appData);

    // get the first row
    @Query("SELECT * FROM AppData LIMIT 1")
    AppData getFirstRow();

    // delete all data from the database
    @Query("DELETE FROM AppData")
    void deleteAll();
}
