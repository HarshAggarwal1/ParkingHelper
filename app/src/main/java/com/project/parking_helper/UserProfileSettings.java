package com.project.parking_helper;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.parking_helper.database.Database;

public class UserProfileSettings extends AppCompatActivity {

    ImageView backButton;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);

        database = Database.getInstance(this);

        backButton = findViewById(R.id.profile_settings_back_button);

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
