package com.example.budget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String LANGUAGE = "lang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLocale();


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new today_page());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.today:
                    selectedFragment = new today_page();
                    break;
                case R.id.month:
                    selectedFragment = new month_page();
                    break;
                case R.id.profile:
                    selectedFragment = new profile_page();
                    break;
            }
            loadFragment(selectedFragment);
            return true;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.custom_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                final Gson gson = new Gson();

                String jsonTotalPaymentMonthly = sharedPref.getString("jsonTotalPaymentMonthly", null);
                Type typeTotalPaymentMonthly = new TypeToken<String>() {}.getType();
                String currencyFromJson = sharedPref.getString("currencyToJson", null);
                Type typeCurrency = new TypeToken<String>() {}.getType();

                String totalPaymentInMonthForSharing;
                String currency;

                try {
                    totalPaymentInMonthForSharing = gson.fromJson(jsonTotalPaymentMonthly, typeTotalPaymentMonthly);
                    currency = gson.fromJson(currencyFromJson, typeCurrency);
                } catch (NullPointerException e) {
                    totalPaymentInMonthForSharing = "0";
                    currency = "";
                    System.out.println("totalPaymentInMonthForSharing and currency are null");
                }

                String shareBodyText = "Bu ayki toplam harcamam " + totalPaymentInMonthForSharing + currency;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocale() {
        try {
            String language = getIntent().getStringExtra(LANGUAGE);
            Locale languageLocale = new Locale(language);
            Resources languageResources = getResources();
            DisplayMetrics dm = languageResources.getDisplayMetrics();
            Configuration conf = languageResources.getConfiguration();
            conf.locale = languageLocale;
            languageResources.updateConfiguration(conf, dm);
        } catch (NullPointerException e) {
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }

    /*
    @Override
    public void paymentAndpaymentDateToMonth(String payment, String date) {

        FragmentManager fm = getSupportFragmentManager();
        month_page mp = (month_page)fm.findFragmentById(R.id.mainFrameLayout);
        mp.getPaymentsAndDates(payment, date);
    }
   */

}