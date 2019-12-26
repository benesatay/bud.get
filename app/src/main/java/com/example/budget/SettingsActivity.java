package com.example.budget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    Intent intent;

    public TextView languageTextView;

    public String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.sToolbar);
        toolbar.setTitle("Settings");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton turkeyButton = findViewById(R.id.turkeyButton);
        ImageButton ukButton = findViewById(R.id.ukButton);


        intent = new Intent(this, MainActivity.class);

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


}
