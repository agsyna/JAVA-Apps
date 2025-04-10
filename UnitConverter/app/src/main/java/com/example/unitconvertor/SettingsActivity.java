package com.example.unitconvertor;

import android.content.SharedPreferences;

import android.os.Bundle;

import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android. view. MenuItem;

import com.airbnb.lottie.LottieAnimationView;


public class SettingsActivity extends AppCompatActivity {

//    private boolean isDarkTheme = false;
    private TextView themeText;
    private ToggleButton toggle;
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK = "is_dark";

    boolean isDarkMode = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_IS_DARK, false);
        toggleTheme(isDark);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.acitivity_setting);
        setTitle("Settings");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // back button pressed
//            }
//        });





        toggle = findViewById(R.id.toggleButton);
        themeText = findViewById(R.id.themetext);
        themeText.setText(isDark ? "Dark Theme" : "Light Theme");

//        LottieAnimationView themeAnimation = findViewById(R.id.themeToggle);
//        themeAnimation.setAnimation(R.raw.theme);


        toggle.setOnClickListener(v -> {
                    boolean newState = !isDark;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(KEY_IS_DARK, newState);
                    editor.apply();

                    AppCompatDelegate.setDefaultNightMode(
                            newState ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                    );
                }
        );



//        themeAnimation.setOnClickListener(v -> {
//            if (isDarkMode) {
//                // Play animation in reverse
//                themeAnimation.setSpeed(-1f);
//                themeAnimation.playAnimation();
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            } else {
//                // Play animation forward
//                themeAnimation.setSpeed(1f);
//                themeAnimation.playAnimation();
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            }
//            isDarkMode = !isDarkMode;
//        });

//        themeAnimation.setOnClickListener(v -> {
//                    boolean newState = !isDark;
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putBoolean(KEY_IS_DARK, newState);
//                    editor.apply();
//                    if (newState) {
////                        themeAnimation.animate(0.5);?
//                    } else {
////                        themeAnimation.setMinAndMaxProgress(0.0f, 0.5f);
//                    }
//
//                        themeAnimation.setSpeed(1f);
//                        themeAnimation.playAnimation();
//
//
//                    AppCompatDelegate.setDefaultNightMode(
//                            newState ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
//                    );
//                }
//        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void toggleTheme(boolean isDark) {
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}