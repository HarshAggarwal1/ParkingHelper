package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.project.parking_helper.database.DataClass;

import java.util.Objects;

public class RegisterPage extends AppCompatActivity {

    private EditText fName, lName, email, password, mobileNumber, vehicleNumber;
//    private Database database;
    private ProgressLoadingBar progressLoadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageView backButton = findViewById(R.id.registerBack);
        Button submitButton = findViewById(R.id.registerButtonSubmit);
        fName = findViewById(R.id.registerEditTextFirstName);
        lName = findViewById(R.id.registerEditTextLastName);
        email = findViewById(R.id.registerEditTextEmail);
        password = findViewById(R.id.registerEditTextPassword);
        mobileNumber = findViewById(R.id.registerEditTextContact);
        ImageView showPasswordButton = findViewById(R.id.registerButtonShowHidePassword);
        vehicleNumber = findViewById(R.id.registerEditTextVehicle);
        progressLoadingBar = new ProgressLoadingBar(this);

//        database = Database.getInstance(this);

        showPasswordButton.setOnClickListener(v -> {
            if (password.getInputType() == 129) {
                password.setInputType(1);
            } else {
                password.setInputType(129);
            }
        });

        backButton.setOnClickListener(v -> finish());

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
            else if (!(email.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+") || email.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"))) {
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
            else if (mobileNumber.getText().toString().isEmpty()) {
                mobileNumber.setError("Please enter your mobile number");
                mobileNumber.requestFocus();
                return;
            }
            else if (mobileNumber.getText().toString().length() != 10) {
                mobileNumber.setError("Please enter a valid mobile number");
                mobileNumber.requestFocus();
                return;
            } else if (vehicleNumber.getText().toString().isEmpty()) {
                vehicleNumber.setError("Please enter your vehicle number");
                vehicleNumber.requestFocus();
                return;
            }

            progressLoadingBar.startLoadingDialog();

            // initiate dialog box to show layout in it

            String firstName = fName.getText().toString();
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
            String lastName = lName.getText().toString();
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
            String emailId = email.getText().toString();
            String pass = password.getText().toString();
            String mNumber = mobileNumber.getText().toString();
            String vNumber = vehicleNumber.getText().toString();

            registerUser(firstName, lastName, emailId, pass, mNumber, vNumber);


//            if (database.userDao().checkEmail(emailId) != null) {
//                Toast.makeText(this, "User already Registered!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            try {
//                database.userDao().insert( new UserData(firstName, lastName, emailId, pass, mNumber, vNumber));
//
//                Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//            catch (Exception e) {
//                Toast.makeText(this, "Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
//            }

        });

    }
    public void registerUser(String fName, String lName, String email, String password, String mobileNumber, String vehicleNumber) {
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterPage.this, task -> {
            if (!task.isSuccessful()) {
                try {
                    throw Objects.requireNonNull(task.getException());
                }
                catch (FirebaseAuthUserCollisionException existEmail) {
                    Toast.makeText(RegisterPage.this, "Email already exists", Toast.LENGTH_SHORT).show();
                }
                catch (FirebaseAuthWeakPasswordException weakPassword) {
                    Toast.makeText(RegisterPage.this, "Weak Password", Toast.LENGTH_SHORT).show();
                }
                catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                    Toast.makeText(RegisterPage.this, "Malformed Email", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(RegisterPage.this, "Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fName + " " + lName).build();
                if (firebaseUser != null) {
                    firebaseUser.updateProfile(profileUpdates);
                    DataClass dataClass = new DataClass(mobileNumber, vehicleNumber);
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).setValue(dataClass).addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Toast.makeText(RegisterPage.this, "Error Occurred while registration! Try Again.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            firebaseUser.sendEmailVerification();
                        }
                    });
                }
            }
            progressLoadingBar.dismissDialog();
            Intent intent = new Intent(RegisterPage.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
