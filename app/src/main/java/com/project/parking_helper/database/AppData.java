package com.project.parking_helper.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "AppData")
public class AppData {

    public AppData(long id, String email, String password, String fName, String lName, String phone, String vehicle) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.vehicle = vehicle;
    }

    @Ignore
    public AppData(String email, String password, String fName, String lName, String phone, String vehicle) {
        this.email = email;
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.vehicle = vehicle;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFName() {
        return fName;
    }

    public String getLName() {
        return lName;
    }

    public String getPhone() {
        return phone;
    }

    public String getVehicle() {
        return vehicle;
    }

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "Email")
    private String email;

    @ColumnInfo(name = "Password")
    private String password;

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    @ColumnInfo(name = "First Name")
    private String fName;

    @ColumnInfo(name = "Last Name")
    private String lName;

    @ColumnInfo(name = "Phone Number")
    private String phone;

    @ColumnInfo(name = "Vehicle Number")
    private String vehicle;
}
