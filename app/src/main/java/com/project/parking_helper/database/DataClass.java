package com.project.parking_helper.database;

public class DataClass {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final String vehicleNumber;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public DataClass(String firstName, String lastName, String email, String phoneNumber, String vehicleNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
    }
}
