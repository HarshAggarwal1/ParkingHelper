package com.project.parking_helper;

import static android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER;
import static android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.ConnectionRequest;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.project.parking_helper.database.Database;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private final int requestCode = 1;
    ImageView userIcon, cameraIconImage, navigationMenu;
    MyDrawer drawerLayout;
    CardView callerCard;
    ImageView callerCardUserPic, callerCardCall, callerCardChat, callerCardCancel;
    TextView callerCardUserName;

    private static String generatedCode = "";
    private View layout;

    private Database database;
    private boolean callPermissionGranted = true;
    private boolean isBiometricSuccess = false;
    private FirebaseAuth mAuth;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Call Permission Accepted");
                }
            } else {
                Log.d("MainActivity", "Call Permission Denied");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        offerReplacingDefaultDialer();

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
        } else {
            Log.d("MainActivity", "Call Permission Not Granted");
            finish();
        }

        drawerLayout.setNavigationDrawer();

        userIcon = findViewById(R.id.main_user_icon);
        cameraIconImage = findViewById(R.id.main_camera_icon);
        navigationMenu = findViewById(R.id.main_menu_bar);
        callerCard = findViewById(R.id.main_caller_card);
        callerCardUserPic = findViewById(R.id.caller_card_pic);
        callerCardCall = findViewById(R.id.caller_card_phone_icon);
        callerCardChat = findViewById(R.id.caller_card_chat_icon);
        callerCardCancel = findViewById(R.id.caller_card_cancel_icon);
        callerCardUserName = findViewById(R.id.caller_card_name);

        mAuth = FirebaseAuth.getInstance();

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
//            if (telecomManager != null) {
//                telecomManager.placeCall(uri, null);
//            }

            makeCall(uri);

        });

        callerCardChat.setOnClickListener(v -> {
            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, SettingsPage.class);
//            startActivity(intent);
        });

        callerCardCancel.setOnClickListener(v -> {
            setGeneratedCode("");
            callerCard.setVisibility(View.GONE);
//            Intent intent = new Intent(MainActivity.this, CancelPage.class);
//            startActivity(intent);
        });
    }

    private void makeCall(Uri uri) {
        startActivity(new Intent(Intent.ACTION_CALL, uri));
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
                    intent = new Intent(MainActivity.this, LoginPage.class);
                    startActivity(intent);
                } else if (id == R.id.navMenuCallLog) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navMenuChat) {
                    Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navMenuSettings) {

                    if (database.appDao().count() == 0) {
                        Toast.makeText(MainActivity.this, "Please Login First!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (!isBiometricSuccess) {
                        BiometricManager biometricManager = BiometricManager.from(MainActivity.this);
                        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
                            return false;
                        } else if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                            return false;
                        }

                        Executor executor = ContextCompat.getMainExecutor(MainActivity.this);
                        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                            }

                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                isBiometricSuccess = true;
                                Intent intent = new Intent(MainActivity.this, SettingsPage.class);
                                if (isBiometricSuccess) {
                                    startActivity(intent);
                                }
                                isBiometricSuccess = false;
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                isBiometricSuccess = false;
                            }
                        });

                        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Biometric Authentication")
                                .setSubtitle("Please authenticate to open Settings")
                                .setNegativeButtonText("Cancel")
                                .build();


                        biometricPrompt.authenticate(promptInfo);
                    }
                } else if (id == R.id.navMenuLogout) {
                    mAuth.signOut();
                    database.appDao().deleteAll();
                    Menu menu = navigationView.getMenu();
                    menu.findItem(R.id.navMenuLogin).setVisible(true);
                    menu.findItem(R.id.navMenuLogout).setVisible(false);
                    Intent i = new Intent(MainActivity.this, LoginPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                drawerLayout.closeDrawers();
                return true;
            });
        }

        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            if (database.appDao().count() == 0) {
                NavigationView navigationView = findViewById(R.id.mainNavMenu);
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.navMenuLogin).setVisible(true);
                menu.findItem(R.id.navMenuLogout).setVisible(false);
            } else {
                NavigationView navigationView = findViewById(R.id.mainNavMenu);
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.navMenuLogin).setVisible(false);
                menu.findItem(R.id.navMenuLogout).setVisible(true);
            }

            if (slideOffset == 0) {
                layout.setAlpha(1);
                layout.setElevation(drawerView.getElevation() + 1);
                callerCard.setElevation(drawerView.getElevation() + 1);
                userIcon.setElevation(drawerView.getElevation() + 1);
                navigationMenu.setElevation(drawerView.getElevation() + 1);
                cameraIconImage.setElevation(drawerView.getElevation() + 1);
            } else {
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

            if (database.appDao().count() == 0) {
                NavigationView navigationView = findViewById(R.id.mainNavMenu);
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.navMenuLogin).setVisible(true);
                menu.findItem(R.id.navMenuLogout).setVisible(false);
            } else {
                NavigationView navigationView = findViewById(R.id.mainNavMenu);
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.navMenuLogin).setVisible(false);
                menu.findItem(R.id.navMenuLogout).setVisible(true);
            }

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
        public void onDrawerStateChanged(int newState) {
        }
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
        } else {
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

    private void offerReplacingDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
            Intent intent = new Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }
    }
}


