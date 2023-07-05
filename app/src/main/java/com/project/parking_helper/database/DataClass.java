package com.project.parking_helper.database;

public class DataClass {
    private final String phoneNumber;
    private final String vehicleNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public DataClass(String phoneNumber, String vehicleNumber) {
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
    }
}
