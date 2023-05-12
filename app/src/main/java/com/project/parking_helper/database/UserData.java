package com.project.parking_helper.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserData")
public class UserData {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "First Name")
    private String firstName;

    @ColumnInfo(name = "Last Name")
    private String lastName;

    @ColumnInfo(name = "Email")
    private String email;

    @ColumnInfo(name = "Password")
    private String password;

    @ColumnInfo(name = "Country Code")
    private String countryCode;

    @ColumnInfo(name = "Phone Number")
    private String phoneNumber;

    @ColumnInfo(name = "Vehicle Type")
    private String vehicleType;

    @ColumnInfo(name = "Vehicle Number")
    private String vehicleNumber;

}
