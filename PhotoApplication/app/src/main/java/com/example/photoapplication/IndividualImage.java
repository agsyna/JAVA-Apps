package com.example.photoapplication;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.FileNotFoundException;

public class IndividualImage extends AppCompatActivity {

    ImageView imageView;
    TextView nameText, dateText, sizeText, uriText;
    ImageButton deleteBtn;
    Toolbar toolbar;

    Uri imageUri;
    String name, date, size;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        toolbar = findViewById(R.id.toolbar2);

        setSupportActionBar(toolbar);

        // back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageView = findViewById(R.id.imageView);
        nameText = findViewById(R.id.textName);
        dateText = findViewById(R.id.textDate);
        sizeText = findViewById(R.id.textSize);
        uriText = findViewById(R.id.textPath);
        deleteBtn = findViewById(R.id.imageButton);

        // Get data from intent
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("uri"));
        name = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        size = intent.getStringExtra("size");

        // Set data
        imageView.setImageURI(imageUri);
        nameText.setText(""+name);
        dateText.setText("  "+date);
        sizeText.setText("  "+size+" bytes");
        uriText.setText("  "+imageUri.toString());

        // Delete image
        deleteBtn.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            boolean deleted = DocumentsContract.deleteDocument(getContentResolver(), imageUri);
                            if (deleted) {
                                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                                finish(); // close activity
                            } else {
                                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                            }
                        } catch (FileNotFoundException e) {
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // go to MainActivity
            Intent intent = new Intent(this, showImagesList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // to close current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
