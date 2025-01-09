package com.example.prayerclock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class SetLocation extends AppCompatActivity {

    private AutoCompleteTextView cityNameAutoCompleteTextView;

    private Button btnSave  ;
    double latitude, longitude;
    String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setlocation);

        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        cityNameAutoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.city_names));
        cityNameAutoCompleteTextView.setAdapter(arrayAdapter);


        btnSave = findViewById(R.id.get_location_button);


    }

    private void setupListeners() {


        btnSave.setOnClickListener(v -> {
            cityName = cityNameAutoCompleteTextView.getText().toString();
            if (!TextUtils.isEmpty(cityName)) {
                performGeocoding(cityName);
            } else {
                showToast("Please enter a city name");
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void performGeocoding(String cityName) {
        try {
            Geocoder geocoder = new Geocoder(SetLocation.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                fetchTimeZone(latitude, longitude);
            } else {
                showToast("Location not found for the given city");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Failed to find the location");
        }
    }

    private void fetchTimeZone(double latitude, double longitude) {
        String timezoneDbUrl = "https://api.timezonedb.com/v2.1/get-time-zone?key=GOM1YR4GR1K5&format=json&by=position&lat=" + latitude + "&lng=" + longitude;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, timezoneDbUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("status") && response.getString("status").equals("OK")) {
                                double timezone = Double.parseDouble(response.optString("gmtOffset")) / (60 * 60);
                                PreferencesUtil.getInstance(SetLocation.this).saveLocationData(latitude, longitude, timezone, cityName);

                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish(); // Finish the SetLocation activity
                                startActivity(new Intent(SetLocation.this, MainActivity.class));
                                finish(); // Finish the Settings activity

                            } else {
                                showToast("TimezoneDB API response does not contain expected fields");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("Error parsing TimezoneDB API response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToast("Error fetching timezone information");
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}


//to clarify if you entered
// http://api.timezonedb.com/v2.1/get-time-zone?key=GOM1YR4GR1K5&format=json&by=position&lat=26&lng=46
// in a browser these are the information that gonna appear:
//{
//    "status": "OK",
//    "message": "",
//    "countryCode": "SA",
//    "countryName": "Saudi Arabia",
//    "regionName": "Riyadh Region",
//    "cityName": "Tumayr",
//    "zoneName": "Asia/Riyadh",
//    "abbreviation": "AST",
//    "gmtOffset": 10800,
//    "dst": "0",
//    "zoneStart": -719636812,
//    "zoneEnd": null,
//    "nextAbbreviation": null,
//    "timestamp": 1711833926,
//    "formatted": "2024-03-30 21:25:26"
//}
