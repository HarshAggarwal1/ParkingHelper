package com.project.parking_helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.ConnectionRequest;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.project.parking_helper.database.Database;

public class MainActivity extends AppCompatActivity{

    private final int requestCode = 1;
    ImageView userIcon, cameraIconImage, navigationMenu;
    MyDrawer drawerLayout;
    CardView callerCard;
    ImageView callerCardUserPic, callerCardCall, callerCardAddPersonOrChat, callerCardCancel;
    TextView callerCardUserName;

    private static String generatedCode = "";
    private View layout;

    private Database database;
    private boolean callPermissionGranted = true;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == this.requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Call Permission Accepted");
                }
            }
            else {
                Log.d("MainActivity", "Call Permission Denied");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.mainLayout);
        drawerLayout = new MyDrawer();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            callPermissionGranted = false;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        if (callPermissionGranted) {
            Log.d("MainActivity", "Call Permission Already Granted");
        }
        else {
            Log.d("MainActivity", "Call Permission Not Granted");
            return;
        }

        drawerLayout.setNavigationDrawer();

        userIcon = findViewById(R.id.main_user_icon);
        cameraIconImage = findViewById(R.id.main_camera_icon);
        navigationMenu = findViewById(R.id.main_menu_bar);
        callerCard = findViewById(R.id.main_caller_card);
        callerCardUserPic = findViewById(R.id.caller_card_pic);
        callerCardCall = findViewById(R.id.caller_card_phone_icon);
        callerCardAddPersonOrChat = findViewById(R.id.caller_card_chat_icon);
        callerCardCancel = findViewById(R.id.caller_card_cancel_icon);
        callerCardUserName = findViewById(R.id.caller_card_name);

        database = Database.getInstance(this);

        callerCard.setVisibility(View.GONE);


        userIcon.setOnClickListener(v -> {
            if (database.appDao().count() == 0) {
                Toast.makeText(this, "Please Login First!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, UserPage.class);
            startActivity(intent);
        });

        cameraIconImage.setOnClickListener(v -> {
            if (database.appDao().count() == 0) {
                Toast.makeText(this, "Please Login First!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, CameraPage.class);
            startActivity(intent);
        });

        navigationMenu.setOnClickListener(v -> drawerLayout.drawerLayout.openDrawer(GravityCompat.START));

        callerCardCall.setOnClickListener(v -> {

            String phoneNumber = getGeneratedCode();

            Uri uri = Uri.parse("tel:" + phoneNumber);
            TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
            if (telecomManager != null) {
                    telecomManager.placeCall(uri,null);
            }
        });

        callerCardAddPersonOrChat.setOnClickListener(v -> {
            Toast.makeText(this, "Add Person or Chat Button Clicked", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, AddPersonOrChatPage.class);
//            startActivity(intent);
        });

        callerCardCancel.setOnClickListener(v -> {
            setGeneratedCode("");
            callerCard.setVisibility(View.GONE);
//            Intent intent = new Intent(MainActivity.this, CancelPage.class);
//            startActivity(intent);
        });
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
                }
                else if (id == R.id.navMenuCallLog) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.navMenuChat) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.navMenuSettings) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.navMenuLogout) {
                    database.appDao().deleteAll();
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.navMenuLogin).setVisible(true);
                    menu.findItem(R.id.navMenuLogout).setVisible(false);
                }
                drawerLayout.closeDrawers();
                return true;
            });
        }

        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            if (slideOffset == 0) {
                layout.setAlpha(1);
                layout.setElevation(drawerView.getElevation() + 1);
                callerCard.setElevation(drawerView.getElevation() + 1);
                userIcon.setElevation(drawerView.getElevation() + 1);
                navigationMenu.setElevation(drawerView.getElevation() + 1);
                cameraIconImage.setElevation(drawerView.getElevation() + 1);
            }
            else {
                layout.setElevation(0);
                layout.setAlpha(1 - (slideOffset / 2));
                callerCard.setElevation(22 - (slideOffset * 22));
                userIcon.setElevation(0);
                navigationMenu.setElevation(0);
                cameraIconImage.setElevation(22 - (slideOffset * 22));
            }
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            layout.setAlpha(0.5f);
            layout.setElevation(0);
            callerCard.setElevation(0);
            userIcon.setElevation(0);
            navigationMenu.setElevation(0);
            cameraIconImage.setElevation(0);
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            layout.setAlpha(1);
            layout.setElevation(drawerView.getElevation() + 1);
            callerCard.setElevation(drawerView.getElevation() + 1);
            userIcon.setElevation(drawerView.getElevation() + 1);
            navigationMenu.setElevation(drawerView.getElevation() + 1);
            cameraIconImage.setElevation(drawerView.getElevation() + 1);

        }

        @Override
        public void onDrawerStateChanged(int newState) {}
    }

    private String getGeneratedCode() {
        return MainActivity.generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        MainActivity.generatedCode = generatedCode;
    }

    private void toastGeneratedCode() {
        String message = getGeneratedCode();
        if (message.equals("")) {
            return;
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout = findViewById(R.id.mainLayout);
        if (database.appDao().count() == 0) {
            NavigationView navigationView = findViewById(R.id.mainNavMenu);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.navMenuLogin).setVisible(true);
            menu.findItem(R.id.navMenuLogout).setVisible(false);
        }
        else {
            NavigationView navigationView = findViewById(R.id.mainNavMenu);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.navMenuLogin).setVisible(false);
            menu.findItem(R.id.navMenuLogout).setVisible(true);
        }
        layout.setAlpha(1);
        if (!getGeneratedCode().equals("")) {
            callerCard.setVisibility(View.VISIBLE);
        }
        if (drawerLayout.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (drawerLayout.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (drawerLayout.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.drawerLayout.closeDrawers();
        }
    }
}


