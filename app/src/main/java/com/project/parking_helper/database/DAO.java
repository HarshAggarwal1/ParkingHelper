package com.project.parking_helper.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DAO {
    @Insert
    void insert(MyData data);

    // insert only name and email
    @Query("INSERT INTO MyData (`First Name`, `Last Name`, email, password, `Country Code`, `Phone Number`, `Vehicle Type`, `Vehicle Number`) VALUES (:firstName, :lastName, :email, :password, :countryCode, :phoneNumber, :vehicleType, :vehicleNumber)")

    // Query to check if email exists in database
    @Query("SELECT * FROM MyData WHERE email = :email")
    MyData checkEmail(String email);

    // Query to check if password entered is same as password in database for the entered email
    @Query("SELECT * FROM MyData WHERE email = :email AND password = :password")
    MyData checkPassword(String email, String password);

    // Query to get all data from database for the entered email
    @Query("SELECT * FROM MyData WHERE email = :email")
    MyData getUserAllData(String email);

// Query to update the vehicle number in database for the entered email
    @Query("UPDATE MyData SET `Vehicle Number` = :vehicleNumber WHERE email = :email")
    void updateVehicleNumber(String email, String vehicleNumber);

    // Query to update the vehicle type in database for the entered email
    @Query("UPDATE MyData SET `Vehicle Type` = :vehicleType WHERE email = :email")
    void updateVehicleType(String email, String vehicleType);

    // Query to update the phone number in database for the entered email
    @Query("UPDATE MyData SET `Phone Number` = :phoneNumber WHERE email = :email")
    void updatePhoneNumber(String email, String phoneNumber);

    // Query to update the country code in database for the entered email
    @Query("UPDATE MyData SET `Country Code` = :countryCode WHERE email = :email")
    void updateCountryCode(String email, String countryCode);

    // Query to update the password in database for the entered email
    @Query("UPDATE MyData SET password = :password WHERE email = :email")
    void updatePassword(String email, String password);

    // Query to update the last name in database for the entered email
    @Query("UPDATE MyData SET `Last Name` = :lastName WHERE email = :email")
    void updateLastName(String email, String lastName);

    // Query to update the first name in database for the entered email
    @Query("UPDATE MyData SET `First Name` = :firstName WHERE email = :email")
    void updateFirstName(String email, String firstName);

    // Query to delete the user from database for the entered email
    @Query("DELETE FROM MyData WHERE email = :email")
    void deleteUser(String email);
}
