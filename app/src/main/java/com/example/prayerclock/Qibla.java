package com.example.prayerclock;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.ParseException;

public class Qibla extends AppCompatActivity implements SensorEventListener {

    private ImageView compassImageView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Location currentLocation;
    private float[] accelerometerData;
    private float[] magnetometerData;
    LocationListener locationListener;
    LocationManager locationManager;
    private ImageView backButton;

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qibla);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        backButton=findViewById(R.id.backButton);

        compassImageView = findViewById(R.id.compassImageView);
        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {

            startLocationUpdates();
        }
        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, "Accelerometer or magnetometer is not available on this device", Toast.LENGTH_SHORT).show();
        }
        backButton.setOnClickListener(v -> {

            onBackPressed();
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            accelerometerData = event.values.clone();
        } else if (event.sensor == magnetometer) {
            magnetometerData = event.values.clone();
        }
        updateCompass();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing for now
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    // Start listening for location updates

    private void updateCompass() {
        if (currentLocation == null || accelerometerData == null || magnetometerData == null)
            return;

        float[] rotationMatrix = new float[9];
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData)) {
            float[] orientationAngles = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            // Calculate device's orientation
            float azimuthInRadians = orientationAngles[0];
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);

            // Calculate Qibla direction
            float qiblaDirection = calculateQiblaDirection(currentLocation.getLatitude(), currentLocation.getLongitude());

            // Adjust compass image rotation with Qibla direction
            compassImageView.setRotation(azimuthInDegrees - qiblaDirection);
        }
    }

    private float calculateQiblaDirection(double latitude, double longitude) {
        double kaabaLatitude = 21.4225;
        double kaabaLongitude = 39.8262;

        double qiblaLatitude = Math.toRadians(kaabaLatitude - latitude);
        double qiblaLongitude = Math.toRadians(kaabaLongitude - longitude);
        double userLatitude = Math.toRadians(latitude);

        double direction = Math.atan2(Math.sin(qiblaLongitude), Math.cos(userLatitude) * Math.tan(qiblaLatitude) - Math.sin(userLatitude) * Math.cos(qiblaLongitude));
        return (float) Math.toDegrees(direction);
    }

    private void startLocationUpdates() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                updateCompass();
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Initialize locationManager before using it
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Check if locationManager is null
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                // Log an error or show a message indicating locationManager is null
                Log.e("Qibla", "LocationManager is null");
                Toast.makeText(this, "LocationManager is null", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Qibla.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish(); // Finish the SettingsActivity
    }


}
