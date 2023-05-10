package com.project.parking_helper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage extends AppCompatActivity {

    ImageView backButton;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backButton = findViewById(R.id.registerBack);
        submitButton = findViewById(R.id.registerButtonSubmit);

        backButton.setOnClickListener(v -> {
            finish();
        });

        submitButton.setOnClickListener(v -> {
            finish();
        });

    }
}
