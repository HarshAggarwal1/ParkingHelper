package com.project.parking_helper;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class UserPage extends AppCompatActivity {

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);

        backButton = findViewById(R.id.userPageBack);

        backButton.setOnClickListener(v -> {
            finish();
        });

    }
}
