package com.project.parking_helper;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationView;
import com.project.parking_helper.database.Database;

public class DeleteAccountPage extends AppCompatActivity {
    ImageView backButton;
    Button deleteButton;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_delete_account);

        backButton = findViewById(R.id.delete_account_back_button);
        deleteButton = findViewById(R.id.settings_delete_account_button);

        database = Database.getInstance(this);

        backButton.setOnClickListener(v -> {
            finish();
        });

        deleteButton.setOnClickListener(v -> {
            String email = database.appDao().getFirstRow().getEmail();
            database.appDao().deleteAll();
//            database.userDao().deleteUser(email);
            finish();
        });
    }
}
