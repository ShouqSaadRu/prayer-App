package com.example.prayerclock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class SettingsActivity extends AppCompatActivity  {

    double latitude, longitude, timezone;
    String cityName;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CODE_SET_LOCATION = 1001;

    LocationListener locationListener;
    LocationManager locationManager;
    String provider, city;
    int minTime, minDistance, Hours, Minutes;
    private Button  btnChooseLocation,notifBtn;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        notifBtn = findViewById(R.id.btn_notif);
        btnChooseLocation = findViewById(R.id.btn_choose_location);
        backButton=findViewById(R.id.backButton);


        setupListeners();

    }

    private void setupListeners() {
        notifBtn.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this,SilentNotifInSettingActvity.class));
        });

        btnChooseLocation.setOnClickListener(v -> {
            showChooseLocationDialog();
        });

        backButton.setOnClickListener(v -> {

            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Finish the SettingsActivity
    }


    private void startLocationUpdates() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;
        minTime = 5000;
        minDistance = 5;

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) { //it is called automatically when the location is changed
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    timezone = getTimeZone1();
                    cityName = getCityFromCoordinates(latitude,longitude);
                    PreferencesUtil.getInstance(SettingsActivity.this).saveLocationData(latitude, longitude, timezone, city);

                }
            };
            if (provider != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener);

        }}



    // Handle the permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                startLocationUpdates();
            } else {
                // Permission denied, handle it or show a message to the user
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showChooseLocationDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);

        Button btnCurrentLocation = dialog.findViewById(R.id.btn_current_location);
        Button btnManualSelection = dialog.findViewById(R.id.btn_manual_selection);

        btnCurrentLocation.setOnClickListener(v -> {

            dialog.dismiss();
            startLocationUpdates();
            // Call onLocationChanged immediately after starting location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation != null) {
                    locationListener.onLocationChanged(lastKnownLocation);
                }
            }

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        });

        btnManualSelection.setOnClickListener(v -> {
            dialog.dismiss();


            Intent intent = new Intent(SettingsActivity.this, SetLocation.class);
            startActivityForResult(intent, REQUEST_CODE_SET_LOCATION);



        });


        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_LOCATION) {

            this.finish();



        }
    }



    // Method to get the city name from coordinates
    private String getCityFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String city = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                city = addresses.get(0).getLocality(); // Get the city from the first address in the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return city;
    }




    public double getTimeZone1() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = (timez.getRawOffset() / 1000.0) / 3600;
        return hoursDiff;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }



}


