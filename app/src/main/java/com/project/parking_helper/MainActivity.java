package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    ImageView userIcon, cameraIconImage;
    CardView cameraButtonCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIcon = findViewById(R.id.main_user_icon);
        cameraButtonCard = findViewById(R.id.cardViewCamera);
        cameraIconImage = findViewById(R.id.main_camera_icon);

        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserPage.class);
            startActivity(intent);
        });
        
        cameraButtonCard.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//            startActivity(intent);

            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
        });

        cameraIconImage.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//            startActivity(intent);

            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
        });
    }

}
