package com.project.parking_helper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.parking_helper.database.Database;

public class UserProfileSettings extends AppCompatActivity {

    ImageView backButton;
    Database database;
    ImageView editEmail, editPassword, editPhone, editVehicleNumber;
    EditText emailET, passwordET, phoneET, vehicleNumberET;
    Button saveButton;

    private boolean isEmailEditable = false, isPasswordEditable = false, isPhoneEditable = false, isVehicleNumberEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);

        editEmail = findViewById(R.id.profile_settings_email_edit);
        editPassword = findViewById(R.id.profile_settings_password_edit);
        editPhone = findViewById(R.id.profile_settings_phone_edit);
        editVehicleNumber = findViewById(R.id.profile_settings_vehicle_edit);

        emailET = findViewById(R.id.profile_settings_email_text);
        passwordET = findViewById(R.id.profile_settings_password_text);
        phoneET = findViewById(R.id.profile_settings_phone_text);
        vehicleNumberET = findViewById(R.id.profile_settings_vehicle_text);

        saveButton = findViewById(R.id.profile_settings_save);

        database = Database.getInstance(this);

        String email = database.appDao().getFirstRow().getEmail();
        String password = database.userDao().getUserAllData(email).getPassword();
        String phone = database.userDao().getUserAllData(email).getPhoneNumber();
        String vehicleNumber = database.userDao().getUserAllData(email).getVehicleNumber();

        this.emailET.setText(email);
        this.passwordET.setText(password);
        this.phoneET.setText(phone);
        this.vehicleNumberET.setText(vehicleNumber);

        editEmail.setOnClickListener(v -> {
            if (!isEmailEditable) {
                this.emailET.setEnabled(true);
                editEmail.setImageResource(R.drawable.pencil_icon_green);
                this.passwordET.setEnabled(false);
                editPassword.setImageResource(R.drawable.pencil_icon);
                this.phoneET.setEnabled(false);
                editPhone.setImageResource(R.drawable.pencil_icon);
                this.vehicleNumberET.setEnabled(false);
                editVehicleNumber.setImageResource(R.drawable.pencil_icon);
                isEmailEditable = true;
            } else {
                this.emailET.setEnabled(false);
                editEmail.setImageResource(R.drawable.pencil_icon);
                isEmailEditable = false;
            }
        });

        editPassword.setOnClickListener(v -> {
            if (!isPasswordEditable) {
                this.passwordET.setEnabled(true);
                editPassword.setImageResource(R.drawable.pencil_icon_green);
                this.emailET.setEnabled(false);
                editEmail.setImageResource(R.drawable.pencil_icon);
                this.phoneET.setEnabled(false);
                editPhone.setImageResource(R.drawable.pencil_icon);
                this.vehicleNumberET.setEnabled(false);
                editVehicleNumber.setImageResource(R.drawable.pencil_icon);
                isPasswordEditable = true;
            } else {
                this.passwordET.setEnabled(false);
                editPassword.setImageResource(R.drawable.pencil_icon);
                isPasswordEditable = false;
            }
        });

        editPhone.setOnClickListener(v -> {
            if (!isPhoneEditable) {
                this.phoneET.setEnabled(true);
                editPhone.setImageResource(R.drawable.pencil_icon_green);
                this.emailET.setEnabled(false);
                editEmail.setImageResource(R.drawable.pencil_icon);
                this.passwordET.setEnabled(false);
                editPassword.setImageResource(R.drawable.pencil_icon);
                this.vehicleNumberET.setEnabled(false);
                editVehicleNumber.setImageResource(R.drawable.pencil_icon);
                isPhoneEditable = true;
            } else {
                this.phoneET.setEnabled(false);
                editPhone.setImageResource(R.drawable.pencil_icon);
                isPhoneEditable = false;
            }
        });

        editVehicleNumber.setOnClickListener(v -> {
            if (!isVehicleNumberEditable) {
                this.vehicleNumberET.setEnabled(true);
                editVehicleNumber.setImageResource(R.drawable.pencil_icon_green);
                this.emailET.setEnabled(false);
                editEmail.setImageResource(R.drawable.pencil_icon);
                this.passwordET.setEnabled(false);
                editPassword.setImageResource(R.drawable.pencil_icon);
                this.phoneET.setEnabled(false);
                editPhone.setImageResource(R.drawable.pencil_icon);
                isVehicleNumberEditable = true;
            } else {
                this.vehicleNumberET.setEnabled(false);
                editVehicleNumber.setImageResource(R.drawable.pencil_icon);
                isVehicleNumberEditable = false;
            }
        });

        saveButton.setOnClickListener(v -> {

            if (emailET.getText().toString().isEmpty()) {
                emailET.setError("Please enter new email");
                emailET.requestFocus();
                return;
            }
            else if (!(emailET.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+") || emailET.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"))) {
                emailET.setError("Please enter a valid email");
                emailET.requestFocus();
                return;
            }
            else if (passwordET.getText().toString().isEmpty()) {
                passwordET.setError("Please enter new password");
                passwordET.requestFocus();
                return;
            }
            else if (passwordET.getText().toString().length() < 8) {
                passwordET.setError("Password must be at least 8 characters");
                passwordET.requestFocus();
                return;
            }
            else if (vehicleNumberET.getText().toString().isEmpty()) {
                vehicleNumberET.setError("Please enter new vehicle number");
                vehicleNumberET.requestFocus();
            }
            else if (phoneET.getText().toString().isEmpty()) {
                phoneET.setError("Please enter new phone number");
                phoneET.requestFocus();
                return;
            }
            else if (phoneET.getText().toString().length() != 10) {
                phoneET.setError("Please enter a valid phone number");
                phoneET.requestFocus();
                return;
            }

            String emailText = this.emailET.getText().toString();
            String passwordText = this.passwordET.getText().toString();
            String phoneText = this.phoneET.getText().toString();
            String vehicleNumberText = this.vehicleNumberET.getText().toString();

            String currentEmail = database.appDao().getFirstRow().getEmail();
            String currentPassword = database.userDao().getUserAllData(currentEmail).getPassword();

            database.userDao().updateEmail(currentEmail, emailText);
            database.userDao().updatePassword(emailText, passwordText);
            database.userDao().updatePhoneNumber(emailText, phoneText);
            database.userDao().updateVehicleNumber(emailText, vehicleNumberText);

            this.emailET.setEnabled(false);
            this.passwordET.setEnabled(false);
            this.phoneET.setEnabled(false);
            this.vehicleNumberET.setEnabled(false);

            if (!currentEmail.equals(emailText) || !currentPassword.equals(passwordText)) {
                Toast.makeText(this, "Please login again as Email or Password changed", Toast.LENGTH_SHORT).show();
                database.appDao().deleteAll();
            }

            finish();
        });

        backButton = findViewById(R.id.profile_settings_back_button);

        backButton.setOnClickListener(v -> finish());
    }
}
