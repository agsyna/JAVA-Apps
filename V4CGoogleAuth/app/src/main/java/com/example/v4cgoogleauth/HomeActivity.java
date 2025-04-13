package com.example.v4cgoogleauth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView welcomeText;
    private Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        welcomeText = findViewById(R.id.welcomeText);
        signOutBtn = findViewById(R.id.signout);

        if (user != null) {
            welcomeText.setText("Welcome, " + user.getDisplayName());
        } else {
            welcomeText.setText("Welcome!");
        }

        signOutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(HomeActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class)); // redirect to login
            finish();
        });
    }
}
