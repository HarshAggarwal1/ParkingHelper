package com.project.parking_helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.parking_helper.database.DataClass;
import com.project.parking_helper.database.Database;
import com.project.parking_helper.database.UserData;

public class RegisterPage extends AppCompatActivity {

    private ImageView backButton, showPasswordButton;
    private Button submitButton;
    private EditText fName, lName, email, password, mobileNumber, vehicleNumber;
    private Database database;
    private ProgressLoadingBar progressLoadingBar;
    private FirebaseAuth mAuth;

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
        mobileNumber = findViewById(R.id.registerEditTextContact);
        showPasswordButton = findViewById(R.id.registerButtonShowHidePassword);
        vehicleNumber = findViewById(R.id.registerEditTextVehicle);
        progressLoadingBar = new ProgressLoadingBar(this);

        database = Database.getInstance(this);

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


        String temp = String.valueOf(mobileNumber);
        String id = temp.substring(5, 10) + System.currentTimeMillis() + temp.substring(0, 5);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isComplete()) {
                    Toast.makeText(RegisterPage.this, "Error Occurred! Try Again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        sendEmailVerification(firebaseUser);
                        firebaseUser.sendEmailVerification();
                    }
                    DataClass dataClass = new DataClass(fName, lName, email, mobileNumber, vehicleNumber);
                    FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(id)).setValue(dataClass);
                    Toast.makeText(RegisterPage.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                    progressLoadingBar.dismissDialog();
                    finish();
                }
            }
        });
    }
    private void sendEmailVerification(@NonNull FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterPage.this, "Confirmation email sent. Please check your inbox or Spam.", Toast.LENGTH_SHORT).show();
                } else {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User account deleted.");
                            } else {
                                Log.d("TAG", "Failed to delete user account.");
                            }
                        }
                    });
                    Toast.makeText(RegisterPage.this, "Failed to send confirmation email. Please try to register again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
