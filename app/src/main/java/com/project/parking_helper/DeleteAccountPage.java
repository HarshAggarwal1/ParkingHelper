package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // also delete record from realtime database
                database.appDao().deleteAll();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                mDatabase.child(user.getUid()).removeValue();
                user.delete();
                Intent i = new Intent(DeleteAccountPage.this, LoginPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }
}
