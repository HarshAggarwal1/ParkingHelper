package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsPage extends AppCompatActivity {
    LinearLayout profileSettings, deleteAccount, appSettings;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileSettings = findViewById(R.id.profile_settings);
        deleteAccount = findViewById(R.id.delete_settings);
        appSettings = findViewById(R.id.app_settings);
        backButton = findViewById(R.id.settings_back_button);

        profileSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsPage.this, UserProfileSettings.class);
            startActivity(intent);
        });

        deleteAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
        });

        appSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> {
            finish();
        });

    }
}
