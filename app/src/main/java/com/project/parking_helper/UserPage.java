package com.project.parking_helper;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.project.parking_helper.database.Database;

import java.util.concurrent.Executor;

public class UserPage extends AppCompatActivity {

    ImageView backButton;
    private Database database;
    private boolean isInfoVisible = false;

    TextView userName, userEmail, userPhone, userVehicleNumber;
    ImageView showInfoButton;
    private boolean isBiometricSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);

        userName = findViewById(R.id.userPageName);
        userEmail = findViewById(R.id.userPageEmail);
        userPhone = findViewById(R.id.userPageContact);
        userVehicleNumber = findViewById(R.id.userPageVehicleNumber);
        showInfoButton = findViewById(R.id.userPageShowHideInfo);

        database = Database.getInstance(this);

        String email = database.appDao().getFirstRow().getEmail();
//        String fName = database.userDao().getUserAllData(email).getFirstName();
//        String lName = database.userDao().getUserAllData(email).getLastName();
//        String name = fName + " " + lName;
//        String vNumber = database.userDao().getUserAllData(email).getVehicleNumber();
//        String phone = database.userDao().getUserAllData(email).getPhoneNumber();

//        String hiddenEmail = email.charAt(0) + "*****" + email.substring(email.length() - 1);
//        String hiddenPhone = phone.charAt(0) + "*****" + phone.substring(phone.length() - 1);
//        String hiddenVNumber = vNumber.charAt(0) + "*****" + vNumber.substring(vNumber.length() - 1);

//        userName.setText(name);
//        userEmail.setText(hiddenEmail);
//        userPhone.setText(hiddenPhone);
//        userVehicleNumber.setText(hiddenVNumber);

        backButton = findViewById(R.id.userPageBack);

        backButton.setOnClickListener(v -> {
            finish();
        });

        showInfoButton.setOnClickListener(v -> {

            if (!this.isBiometricSuccess) {
                BiometricManager biometricManager = BiometricManager.from(this);
                if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
                    return;
                }
                else if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                    return;
                }

                Executor executor = ContextCompat.getMainExecutor(this);
                BiometricPrompt biometricPrompt = new BiometricPrompt(UserPage.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        isBiometricSuccess = true;
                        userEmail.setText(email);
//                        userPhone.setText(phone);
//                        userVehicleNumber.setText(vNumber);
                        isInfoVisible = true;
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        isBiometricSuccess = false;
                    }
                });

                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric Authentication")
                        .setSubtitle("Please authenticate to view your information")
                        .setNegativeButtonText("Cancel")
                        .build();

                biometricPrompt.authenticate(promptInfo);
            }

            if (this.isBiometricSuccess) {
                if (isInfoVisible) {
//                    userEmail.setText(hiddenEmail);
//                    userPhone.setText(hiddenPhone);
//                    userVehicleNumber.setText(hiddenVNumber);
                    isInfoVisible = false;
                } else {
                    userEmail.setText(email);
//                    userPhone.setText(phone);
//                    userVehicleNumber.setText(vNumber);
                    isInfoVisible = true;
                }
            }
        });

    }
}
