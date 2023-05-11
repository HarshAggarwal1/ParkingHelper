package com.project.parking_helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    ImageView userIcon, cameraIconImage, navigationMenu;
    MyDrawer drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = new MyDrawer();

        drawerLayout.setNavigationDrawer();

        userIcon = findViewById(R.id.main_user_icon);
        cameraIconImage = findViewById(R.id.main_camera_icon);
        navigationMenu = findViewById(R.id.main_menu_bar);

        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserPage.class);
            startActivity(intent);
        });

        cameraIconImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraPage.class);
            startActivity(intent);
        });

        navigationMenu.setOnClickListener(v -> drawerLayout.drawerLayout.openDrawer(GravityCompat.START));
    }

    class MyDrawer implements DrawerLayout.DrawerListener {
        DrawerLayout drawerLayout;
        MyDrawer() {
            this.drawerLayout = findViewById(R.id.mainDrawerLayout);
            drawerLayout.addDrawerListener(this);
        }

        private void setNavigationDrawer() {
            NavigationView navigationView = findViewById(R.id.mainNavMenu);
            navigationView.setNavigationItemSelectedListener(item -> {
                // Handle navigation view item clicks here.
                int id = item.getItemId();
                Intent intent;
                if (id == R.id.navMenuLogin) {
                    System.out.println("Login Clicked");
                    intent = new Intent(MainActivity.this, LoginPage.class);
                    startActivity(intent);
                } else if (id == R.id.navMenuChat) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navMenuSettings) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navMenuLogout) {
                    finish();
                }
                drawerLayout.closeDrawers();
                return true;
            });
        }

        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            if (slideOffset == 0) {
                navigationMenu.setElevation(drawerView.getElevation() + 1);
                cameraIconImage.setElevation(drawerView.getElevation() + 1);
            }
            else {
                navigationMenu.setElevation(0);
                cameraIconImage.setElevation(0);
            }
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            navigationMenu.setElevation(0);
            cameraIconImage.setElevation(0);
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            navigationMenu.setElevation(drawerView.getElevation() + 1);
            cameraIconImage.setElevation(drawerView.getElevation() + 1);

        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    }


}
