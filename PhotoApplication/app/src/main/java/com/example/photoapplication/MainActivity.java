package com.example.photoapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_FOLDER_PICKER = 3;

    private Uri selectedFolderUri;
    private Uri photoUri;
    private ImageView imageView;
    private File photoFile;
    private Button captureBtn;
    private Button showImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
       captureBtn = findViewById(R.id.captureBtn);
       showImages = findViewById(R.id.showImages);

        captureBtn.setOnClickListener(v -> pickFolder());
        showImages.setOnClickListener(v -> pickFolderToShowImages());
    }

    private void pickFolderToShowImages() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, 1234);
    }


    private void pickFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_FOLDER_PICKER);
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "IMG_" + timeStamp;
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile(fileName, ".jpg", storageDir);

                photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        photoFile
                );

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            } catch (Exception e) {
                showToast("ERROR: " + e.getMessage());
            }
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOLDER_PICKER && resultCode == RESULT_OK && data != null) {
            selectedFolderUri = data.getData();

            int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(selectedFolderUri, takeFlags);

            showToast("FOLDER SELECTED!");
            captureImage();
        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageURI(photoUri);

            // Save to gallery
            saveToGallery(photoFile);

            // Save to selected folder
            if (selectedFolderUri != null) {
                saveToFolder(photoFile);
            } else {
                showToast("NO FOLDER SELECTED");
            }
        }
        else if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            Uri folderUri = data.getData();
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            assert folderUri != null;
            getContentResolver().takePersistableUriPermission(folderUri,
                    takeFlags);
            showToast("SHOWING IMAGES");
            Intent intent = new Intent(this, showImagesList.class);
            intent.putExtra("folderUri", folderUri);
            startActivity(intent);
        }
    }

    private void saveToGallery(File imageFile) {
        try {
            String fileName = imageFile.getName();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera");

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri);
                     InputStream in = new FileInputStream(imageFile)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    showToast("SAVED TO GALLERY");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("FAILED TO SAVE TO GALLERY");
        }
    }

    private void saveToFolder(File imageFile) {
        try {
            String fileName = imageFile.getName();

            // convert tree URI to document URI
            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(
                    selectedFolderUri,
                    DocumentsContract.getTreeDocumentId(selectedFolderUri)
            );

            Uri imageUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    docUri,
                    "image/jpeg",
                    fileName
            );

            if (imageUri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(imageUri);
                     InputStream in = new FileInputStream(imageFile)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    showToast("SAVED TO SELECTED FOLDER!");
                }
            } else {
                showToast("FAILED TO CREATE IMAGE URI");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToast("SAVE FAILED: " + e.getMessage());
        }
    }





    private void showToast(String message) {
        Toast.makeText(this, message.toUpperCase(Locale.getDefault()), Toast.LENGTH_SHORT).show();
    }
}
