package com.project.parking_helper;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class UserProfileSettings extends AppCompatActivity {

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);

        backButton = findViewById(R.id.profile_settings_back_button);

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
