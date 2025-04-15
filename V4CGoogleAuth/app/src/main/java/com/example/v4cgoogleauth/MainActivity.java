package com.example.v4cgoogleauth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<IntentSenderRequest> oneTapLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        Log.d(TAG, "OneTapLauncher: Activity result received");
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Log.d(TAG, "OneTapLauncher: Result OK, extracting credentials");
            try {
                SignInCredential credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(result.getData());
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    Log.d(TAG, "ID Token received, proceeding to Firebase auth");
                    firebaseAuthWithGoogle(idToken);
                } else {
                    Log.e(TAG, "ID Token is null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Sign-in failed: " + e.getMessage(), e);
            }
        } else {
            Log.e(TAG, "OneTapLauncher: Result not OK or data is null");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Initializing FirebaseAuth and OneTapClient");

        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true).setServerClientId(getString(R.string.default_web_client_id)).setFilterByAuthorizedAccounts(false).build()).setAutoSelectEnabled(true).build();

        Log.d(TAG, "onCreate: SignInRequest built");

        findViewById(R.id.signInWithGoogle).setOnClickListener(v -> {
            Log.d(TAG, "Sign-in button clicked");
            startSignIn();
        });
    }

    private void startSignIn() {
        Log.d(TAG, "startSignIn: Starting One Tap sign-in");
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(result -> {
            Log.d(TAG, "One Tap sign-in initiated successfully");
            IntentSenderRequest request = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
            oneTapLauncher.launch(request);
        }).addOnFailureListener(e -> Log.e(TAG, "One Tap failed: " + e.getMessage(), e));
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogle: Authenticating with Firebase using ID token");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG, "Firebase sign-in successful: " + user.getEmail());
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Sign In Successful", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Firebase auth failed", task.getException());
            }
        });
    }
}
