package com.example.unitconvertor;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner fromSpinner, toSpinner;
    private EditText inputText;
    private TextView answer;

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

        findViewById(R.id.convertButton).setOnClickListener(v -> convertUnits()); // v is for view that can was clicked

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.unit_types, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
}