package com.example.photoapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;


public class showImagesList extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ImageItem> imageList = new ArrayList<>();
    ImageAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        toolbar = findViewById(R.id.toolbar2);

        setSupportActionBar(toolbar);

        // back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Uri folderUri = getIntent().getParcelableExtra("folderUri");
        if (folderUri != null) {
            loadImagesFromFolder(folderUri);
        }

        adapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // go to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // to close current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadImagesFromFolder(Uri folderUri) {
        ContentResolver resolver = getContentResolver();

        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                folderUri, DocumentsContract.getTreeDocumentId(folderUri)
        );

        Cursor cursor = resolver.query(childrenUri, new String[]{
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
        }, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String docId = cursor.getString(0);
                String name = cursor.getString(1);
                String mimeType = cursor.getString(2);
                long lastModified = cursor.getLong(3);
                String sizeStr="";

                if (mimeType != null && mimeType.startsWith("image/")) {
                    Uri imageUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, docId);
                    String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(new Date(lastModified));

                    long size = 0;
                    try (Cursor sizeCursor = resolver.query(
                            imageUri,
                            new String[]{android.provider.OpenableColumns.SIZE},
                            null,
                            null,
                            null
                    )) {
                        if (sizeCursor != null && sizeCursor.moveToFirst()) {
                            size = sizeCursor.getLong(0); // in bytes
                            sizeStr = String.valueOf(size);
                        }
                    }

                    imageList.add(new ImageItem(name, imageUri.toString(), date, sizeStr));
                }
            }
            cursor.close();
        }
    }

    }

