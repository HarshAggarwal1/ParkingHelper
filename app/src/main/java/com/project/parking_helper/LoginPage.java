package com.project.parking_helper;

import android.content.Intent;
import android.os.Bundle;
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
import com.project.parking_helper.database.AppData;
import com.project.parking_helper.database.Database;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    ImageView backButton, googleSignInButton, showPasswordButton;
    Button loginButton, registerButton;
    EditText email, password;

    private Database database;

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
            else if (!email.getText().toString().matches("[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+")) {
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

            String emailText = this.email.getText().toString();
            String passwordText = this.password.getText().toString();

            if (database.userDao().checkEmail(emailText) == null) {
                email.setError("Email not registered");
                email.requestFocus();
            }
            else if (!database.userDao().getPassword(emailText).equals(passwordText)) {
                password.setError("Incorrect password");
                password.requestFocus();
            }
            else {
                database.appDao().insert(new AppData(emailText, passwordText));
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        googleSignInButton.setOnClickListener(v -> {
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
                String name = account.getDisplayName();
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
            }
            catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
