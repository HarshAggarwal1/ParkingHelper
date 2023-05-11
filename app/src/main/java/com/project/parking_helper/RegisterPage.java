package com.project.parking_helper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage extends AppCompatActivity {

    ImageView backButton, showPasswordButton;
    Button submitButton;
    EditText fName, lName, email, password, countryCode, mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backButton = findViewById(R.id.registerBack);
        submitButton = findViewById(R.id.registerButtonSubmit);
        fName = findViewById(R.id.registerEditTextFirstName);
        lName = findViewById(R.id.registerEditTextLastName);
        email = findViewById(R.id.registerEditTextEmail);
        password = findViewById(R.id.registerEditTextPassword);
        countryCode = findViewById(R.id.registerEditTextCountryCode);
        mobileNumber = findViewById(R.id.registerEditTextContact);
        showPasswordButton = findViewById(R.id.registerButtonShowHidePassword);

        showPasswordButton.setOnClickListener(v -> {
            if (password.getInputType() == 129) {
                password.setInputType(1);
            } else {
                password.setInputType(129);
            }
        });

        backButton.setOnClickListener(v -> {
            finish();
        });

        submitButton.setOnClickListener(v -> {

            if (fName.getText().toString().isEmpty()) {
                fName.setError("Please enter your first name");
                fName.requestFocus();
                return;
            }
            else if (!fName.getText().toString().matches("[a-zA-Z]+")) {
                fName.setError("Please enter a valid first name");
                fName.requestFocus();
                return;
            }
            else if (lName.getText().toString().isEmpty()) {
                lName.setError("Please enter your last name");
                lName.requestFocus();
                return;
            }
            else if (!lName.getText().toString().matches("[a-zA-Z]+")) {
                lName.setError("Please enter a valid last name");
                lName.requestFocus();
                return;
            }
            else if (email.getText().toString().isEmpty()) {
                email.setError("Please enter your email");
                email.requestFocus();
                return;
            }
            else if (!email.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")) {
                email.setError("Please enter a valid email");
                email.requestFocus();
                return;
            }
            else if (password.getText().toString().isEmpty()) {
                password.setError("Please enter your password");
                password.requestFocus();
                return;
            }
            else if (password.getText().toString().length() < 8) {
                password.setError("Password must be at least 8 characters long");
                password.requestFocus();
                return;
            }
            else if (countryCode.getText().toString().isEmpty()) {
                countryCode.setError("Please enter your country code");
                countryCode.requestFocus();
                return;
            }
            else if (!countryCode.getText().toString().matches("\\d+")) {
                 countryCode.setError("Please enter a valid country code");
                 countryCode.requestFocus();
                 return;
            }
            else if (mobileNumber.getText().toString().isEmpty()) {
                mobileNumber.setError("Please enter your mobile number");
                mobileNumber.requestFocus();
                return;
            }
            else if (mobileNumber.getText().toString().length() != 10) {
                mobileNumber.setError("Please enter a valid mobile number");
                mobileNumber.requestFocus();
                return;
            }
            String firstName = fName.getText().toString();
            String lastName = lName.getText().toString();
            String emailId = email.getText().toString();
            String pass = password.getText().toString();
            String cCode = countryCode.getText().toString();
            String mNumber = mobileNumber.getText().toString();

            Toast.makeText(this, "You are Registered Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
}
