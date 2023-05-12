package com.project.parking_helper.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppData")
public class AppData {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "Email")
    private String email;

    @ColumnInfo(name = "Password")
    private String password;
}
