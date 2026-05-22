package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import Firebase classes
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // UI Elements
    private EditText etUsername, etPassword;
    private Button btnLoginSignUp;

    // Declare a Firebase Auth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsets().left, insets.getSystemWindowInsets().top, insets.getSystemWindowInsets().right, insets.getSystemWindowInsets().bottom);
            return insets;
        });

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSignUp = findViewById(R.id.btnLoginSignUp);
        ImageView imgLogo = findViewById(R.id.imgLogo);
        imgLogo.setImageResource(R.drawable.logo);

        btnLoginSignUp.setOnClickListener(v -> {
            // NOTE: Firebase uses Email format for the username.
            String email = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(MainActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the corrected login/signup method
            loginOrSignUp(email, password);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and navigate to home if they are.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHome();
        }
    }

    private void loginOrSignUp(String email, String password) {
        // 1. First, try to sign in.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(MainActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    } else {
                        // If sign in fails, it could be a wrong password OR the user doesn't exist.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());

                        // 2. We ONLY try to create an account if the sign-in failed because the user does not exist.
                        //    Firebase provides specific exceptions we can check for.
                        //    In this case, we will try to create an account if login fails for any reason *except*
                        //    a user collision on creation. This simplifies the logic.

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, createTask -> {
                                    if (createTask.isSuccessful()) {
                                        // Sign up success
                                        Log.d(TAG, "createUserWithEmail:success");
                                        Toast.makeText(MainActivity.this, "Account Created Successfully.", Toast.LENGTH_SHORT).show();
                                        navigateToHome();
                                    } else {
                                        // If creating an account also fails, it's likely because the user
                                        // already exists (so the password was wrong), or another error occurred.
                                        Log.w(TAG, "createUserWithEmail:failure", createTask.getException());

                                        if (createTask.getException() instanceof FirebaseAuthUserCollisionException) {
                                            // This means the user exists, so the original login attempt failed due to a wrong password.
                                            Toast.makeText(MainActivity.this, "Authentication failed. Incorrect password.", Toast.LENGTH_LONG).show();
                                        } else {
                                            // Another error occurred (e.g., malformed email, no internet).
                                            Toast.makeText(MainActivity.this, "Authentication failed. Check email format.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish(); // Prevent user from going back to the login screen
    }
}
