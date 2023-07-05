package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.parking_helper.database.AppData;
import com.project.parking_helper.database.Database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    ImageView backButton, googleSignInButton, showPasswordButton;
    Button loginButton, registerButton;
    EditText email, password;
    private FirebaseAuth mAuth;

    private Database database;
    private ProgressLoadingBar progressLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backButton = findViewById(R.id.loginBack);
        loginButton = findViewById(R.id.loginButtonLogin);
        registerButton = findViewById(R.id.loginButtonRegister);
        googleSignInButton = findViewById(R.id.loginButtonGoogleSignIn);
        showPasswordButton = findViewById(R.id.loginButtonShowHidePassword);
        email = findViewById(R.id.loginEditTextUsername);
        password = findViewById(R.id.loginEditTextPassword);

        progressLoadingBar = new ProgressLoadingBar(this);
        mAuth = FirebaseAuth.getInstance();

        database = Database.getInstance(this);

        showPasswordButton.setOnClickListener(v -> {
            if (password.getInputType() == 129) {
                password.setInputType(1);
            } else {
                password.setInputType(129);
            }
        });

        backButton.setOnClickListener(v -> {
            finish();
        });

        loginButton.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                email.setError("Please enter your email");
                email.requestFocus();
                return;
            }
            else if (!(email.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+") || email.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"))) {
                email.setError("Please enter a valid email");
                email.requestFocus();
                return;
            }
            else if (password.getText().toString().isEmpty()) {
                password.setError("Please enter your password");
                password.requestFocus();
                return;
            }
            else if (password.getText().toString().length() < 8) {
                password.setError("Password must be at least 8 characters");
                password.requestFocus();
                return;
            }
            progressLoadingBar.startLoadingDialog();
            String emailText = this.email.getText().toString();
            String passwordText = this.password.getText().toString();

            loginUser(emailText, passwordText);

//            if (database.userDao().checkEmail(emailText) == null) {
//                email.setError("Email not registered");
//                email.setText("");
//                password.setText("");
//                email.requestFocus();
//            }
//            else if (!database.userDao().getPassword(emailText).equals(passwordText)) {
//                password.setError("Incorrect password");
//                password.setText("");
//                email.setText("");
//                password.requestFocus();
//            }
//            else {
//                database.appDao().insert(new AppData(emailText, passwordText));
//                Intent intent = new Intent(LoginPage.this, MainActivity.class);
//                startActivity(intent);
//            }
        });

        googleSignInButton.setOnClickListener(v -> {
            progressLoadingBar.startLoadingDialog();
            signIn();
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterPage.class);
            startActivity(intent);
        });
    }

    public void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInClient.signOut();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String email = account.getEmail();
                System.out.println(email);
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                progressLoadingBar.dismissDialog();
                startActivity(intent);
            }
            catch (ApiException e) {
                progressLoadingBar.dismissDialog();
                e.printStackTrace();
                Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser(String emaill, String passwordd) {
        mAuth.signInWithEmailAndPassword(emaill, passwordd).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String fullName = user.getDisplayName();
                    assert fullName != null;
                    String[] name = fullName.split(" ");
                    String firstName = name[0];
                    String lastName = name[1];
                    // retrieve current firebase user record
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // if user record exists, do nothing
                            if (snapshot.exists()) {
                                String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                                String vehicle = snapshot.child("vehicleNumber").getValue(String.class);
                                database.appDao().insert(new AppData(firstName, lastName, emaill, passwordd, phoneNumber, vehicle));
                            }
                            // else, create new user record
                            else {
                                Log.d("TAG", "Data don't exist");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "Error while retrieving Data");
                        }
                    });
                }

                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                progressLoadingBar.dismissDialog();
                startActivity(intent);
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                }
                catch (FirebaseAuthInvalidCredentialsException e) {
                    progressLoadingBar.dismissDialog();
                    password.setError("Incorrect password");
                    password.setText("");
                    email.setText("");
                    password.requestFocus();
                }
                catch (FirebaseAuthInvalidUserException e) {
                    progressLoadingBar.dismissDialog();
                    email.setError("Email not registered");
                    email.setText("");
                    password.setText("");
                    email.requestFocus();
                }
                catch (Exception e) {
                    progressLoadingBar.dismissDialog();
                    Toast.makeText(LoginPage.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
