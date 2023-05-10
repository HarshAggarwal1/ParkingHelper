package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    ImageView backButton, googleSignInButton;
    Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backButton = findViewById(R.id.loginBack);
        loginButton = findViewById(R.id.loginButtonLogin);
        registerButton = findViewById(R.id.loginButtonRegister);
        googleSignInButton = findViewById(R.id.loginButtonGoogleSignIn);

        backButton.setOnClickListener(v -> {
            finish();
        });

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        });

        googleSignInButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterPage.class);
            startActivity(intent);
        });
    }
}
