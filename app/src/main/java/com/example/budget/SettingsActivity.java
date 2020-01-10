package com.example.budget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    Uri uri;
    Bitmap bitmap;
    ImageButton profileImageButton;

    Boolean boolCam;
    Boolean boolGal;

    Intent intent;

    public TextView languageTextView;

    public String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        boolCam = false;
        boolGal = false;

        Toolbar toolbar = findViewById(R.id.sToolbar);
        toolbar.setTitle("Settings");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton turkeyButton = findViewById(R.id.turkeyButton);
        ImageButton ukButton = findViewById(R.id.ukButton);

        intent = new Intent(this, MainActivity.class);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SettingsActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        turkeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("tr");
            }
        });

        ukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });

        final Button tlButton = findViewById(R.id.tlButton);
        final Button dolarButton = findViewById(R.id.dolarButton);
        final Button euroButton = findViewById(R.id.euroButton);

        tlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tlButton.setBackgroundColor(Color.parseColor("#AEEA00"));
                dolarButton.setBackgroundColor(Color.parseColor("#212121"));
                euroButton.setBackgroundColor(Color.parseColor("#212121"));

                tlButton.setTextColor(Color.parseColor("#212121"));
                euroButton.setTextColor(Color.parseColor("#BDBDBD"));
                dolarButton.setTextColor(Color.parseColor("#BDBDBD"));

                currency = "₺";
                intent.putExtra(today_page.CURRENCY, currency);
            }
        });

        dolarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dolarButton.setBackgroundColor(Color.parseColor("#AEEA00"));
                tlButton.setBackgroundColor(Color.parseColor("#212121"));
                euroButton.setBackgroundColor(Color.parseColor("#212121"));

                dolarButton.setTextColor(Color.parseColor("#212121"));
                tlButton.setTextColor(Color.parseColor("#BDBDBD"));
                euroButton.setTextColor(Color.parseColor("#BDBDBD"));

                currency = "$";
                intent.putExtra(today_page.CURRENCY, currency);
            }
        });

        euroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                euroButton.setBackgroundColor(Color.parseColor("#AEEA00"));
                dolarButton.setBackgroundColor(Color.parseColor("#212121"));
                tlButton.setBackgroundColor(Color.parseColor("#212121"));

                euroButton.setTextColor(Color.parseColor("#212121"));
                dolarButton.setTextColor(Color.parseColor("#BDBDBD"));
                tlButton.setTextColor(Color.parseColor("#BDBDBD"));

                currency = "€";
                intent.putExtra(today_page.CURRENCY, currency);
            }
        });

        profileImageButton = findViewById(R.id.profileImageButton);
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPick(v);
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPick(v);
            }
        });
    }

    public void setLocale(final String language) {
        Locale myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        languageTextView = findViewById(R.id.languageTextView);
        if(languageTextView.getText().equals("Language")) {
            languageTextView.setText("Dil");
        } else {
            languageTextView.setText("Language");
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.SettingsConstraintLayout), getString(R.string.Language_Changed), Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(language.equals("tr")) {
                    setLocale("en");
                } else {
                    setLocale("tr");
                }
                Toast.makeText(getApplicationContext(), getString(R.string.UNDO_TEXT), Toast.LENGTH_SHORT).show();
            }
        });
        snackbar.show();

        intent.putExtra(MainActivity.LANGUAGE, language);
    }

    //backbutton on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //finish(); // close this activity and return to preview activity (if there is any)
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickPick(View view) {
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder pickDialog = new AlertDialog.Builder(this);
        pickDialog.setTitle("Choose your profile picture");

        pickDialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Camera")) {
                    boolCam = true;
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 100);
                } else if (options[item].equals("Gallery")) {
                    boolGal = true;
                    Intent pickPhoto = new Intent();
                    pickPhoto.setType("image/*");
                    pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), 100);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        pickDialog.show();
    }

    public void loadPick(View view) {
        if (getIntent().hasExtra("profileImage")) {
            bitmap = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("profileImage"), 0, getIntent().getByteArrayExtra("profileImage").length);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        intent.putExtra("profileImage", byteArrayOutputStream.toByteArray());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intentImage = new Intent(SettingsActivity.this, MainActivity.class);

        if (resultCode == -1 && boolCam) {
            uri = data.getData();
            bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            profileImageButton.setImageBitmap(bitmap);
            intentImage.putExtra("profileImage", byteArrayOutputStream.toByteArray());


        } else if (resultCode == -1 && boolGal) {
            if (data != null) {
                try {
                    uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90,byteArrayOutputStream);
                    profileImageButton.setImageBitmap(bitmap);
                    intentImage.putExtra("profileImage", byteArrayOutputStream.toByteArray());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
