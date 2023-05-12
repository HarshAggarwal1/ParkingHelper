package com.project.parking_helper.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDAO {
    @Insert
    void insert(UserData data);

    // insert only name and email
    @Query("INSERT INTO UserData (`First Name`, `Last Name`, email, password) VALUES (:firstName, :lastName, :email, :password)")
    void insert(String firstName, String lastName, String email, String password);

    // insert remaining data of entered email
    @Query("UPDATE UserData SET `Country Code` = :countryCode, `Phone Number` = :phoneNumber, `Vehicle Type` = :vehicleType, `Vehicle Number` = :vehicleNumber WHERE email = :email")
    void insert(String email, String countryCode, String phoneNumber, String vehicleType, String vehicleNumber);

    // Query to check if email exists in database
    @Query("SELECT * FROM UserData WHERE email = :email")
    UserData checkEmail(String email);

    // return the password for the entered email
    @Query("SELECT password FROM UserData WHERE email = :email")
    String getPassword(String email);

    // Query to get all data from database for the entered email
    @Query("SELECT * FROM UserData WHERE email = :email")
    UserData getUserAllData(String email);

    // Query to update the vehicle number in database for the entered email
    @Query("UPDATE UserData SET `Vehicle Number` = :vehicleNumber WHERE email = :email")
    void updateVehicleNumber(String email, String vehicleNumber);

    // Query to update the vehicle type in database for the entered email
    @Query("UPDATE UserData SET `Vehicle Type` = :vehicleType WHERE email = :email")
    void updateVehicleType(String email, String vehicleType);

    // Query to update the phone number in database for the entered email
    @Query("UPDATE UserData SET `Phone Number` = :phoneNumber WHERE email = :email")
    void updatePhoneNumber(String email, String phoneNumber);

    // Query to update the country code in database for the entered email
    @Query("UPDATE UserData SET `Country Code` = :countryCode WHERE email = :email")
    void updateCountryCode(String email, String countryCode);

    // Query to update the password in database for the entered email
    @Query("UPDATE UserData SET password = :password WHERE email = :email")
    void updatePassword(String email, String password);

    // Query to update the last name in database for the entered email
    @Query("UPDATE UserData SET `Last Name` = :lastName WHERE email = :email")
    void updateLastName(String email, String lastName);

    // Query to update the first name in database for the entered email
    @Query("UPDATE UserData SET `First Name` = :firstName WHERE email = :email")
    void updateFirstName(String email, String firstName);

    // Query to delete the user from database for the entered email
    @Query("DELETE FROM UserData WHERE email = :email")
    void deleteUser(String email);
}
