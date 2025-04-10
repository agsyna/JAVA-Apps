package com.example.unitconvertor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private boolean isDarkTheme = false;
    private Spinner fromSpinner, toSpinner;
    private EditText inputText;
    private TextView answer;
    private ImageButton setting;
    private Button convert_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setTitle("Unit Converter");

        fromSpinner = findViewById(R.id.fromUnitSpinner);
        toSpinner = findViewById(R.id.toUnitSpinner);
        inputText = findViewById(R.id.inputText);
        answer = findViewById(R.id.answer);
        setting = findViewById(R.id.setting);


        convert_btn = findViewById(R.id.convertButton);
        convert_btn.setOnClickListener(v -> convertUnits()); // v is for view that can was clicked


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.unit_types, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        setting.setOnClickListener(
                v->
                NavigateToSetting()
        );




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void NavigateToSetting (){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }


    public void convertUnits()
    {
        String from = fromSpinner.getSelectedItem().toString();
        String to = toSpinner.getSelectedItem().toString();
        String value = inputText.getText().toString();

        if(value.isEmpty())
        {
            Toast.makeText(this, "Please input a number", Toast.LENGTH_LONG).show();
            return;
        }

        double valueInDouble = Double.parseDouble(value);
        double convertedValue = convert(from, to, valueInDouble);
        answer.setText(String.format(Locale.getDefault(), "%.4f %s", convertedValue, to));
    }

    public double convert(String fromUnit, String toUnit, double valueInDouble)
    {
        HashMap<String, Double> conversionMap = new HashMap<>();
//        <item>Foot</item>
//        <item>Inch</item>
//        <item>Centimetre</item>
//        <item>Metre</item>
//        <item>Yard</item>
        conversionMap.put("Foot", 0.3048);
        conversionMap.put("Inch", 0.0254);
        conversionMap.put("Centimetre", 0.01);
        conversionMap.put("Metre", 1.0);
        conversionMap.put("Yard", 0.9144);

        Double fromValue = conversionMap.getOrDefault(fromUnit, null);
        Double toValue = conversionMap.getOrDefault(toUnit, null);

        if(fromValue == null || toValue == null)
        {
            Toast.makeText(this, "Invalid unit selection", Toast.LENGTH_LONG).show();
            return 0;
        }

        double valueInMetre = valueInDouble * fromValue;
        return valueInMetre/toValue;
    }

    public void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        recreate();
    }

}