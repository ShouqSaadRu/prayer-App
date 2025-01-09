package com.example.prayerclock;
//best
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import android.app.NotificationManager;

import android.app.Notification;

import android.os.Bundle;

import android.view.View;

import android.widget.EditText;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime, sunriseTime, nextPrayerTextView, cityView, todayDate, countdown;
    private PrayTime prayTime;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CODE_GET_LOCATION = 1001;
    double latitude, longitude;
    LocationListener locationListener;
    LocationManager locationManager;
    String provider, city;
    int minTime, minDistance, Hours, Minutes;
    Date date;
    Calendar calendar;

    private boolean curentPrayerMode;

    double timezone1, longt1, lati1, timezone;
    private TextView AthkarTextView;
    private String[] athkar = {"اللهم اجعل لساني رطباً بذكرك وقلبي خاشعاً بخشيتك.", "حسبي الله لا إله إلا هو، عليه توكلت، وهو رب العرش العظيم.",
            "اللهم إني أعوذ بك من زوال نعمتك وتحول عافيتك وفجاءة نقمتك وجميع سخطك.", "اللهم إني أسألك العفو والعافية في الدنيا والآخرة.", "اللهم اغفر لي وارحمني واهدني وارزقني."};
    private int currentIndex = 0;
    private Handler handler;
    private final int INTERVAL = 40000; // Interval in milliseconds (1 minute)
    private ImageView settingsButton, backgroudImg, rightArrow, QiblaButton;


    private ArrayList<String> prayerTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        fajrTime = (TextView) findViewById(R.id.textViewFajrTime);
        dhuhrTime = (TextView) findViewById(R.id.textViewDhuhrTime);
        sunriseTime = (TextView) findViewById(R.id.textViewSunriseTime);
        asrTime = (TextView) findViewById(R.id.textViewAsrTime);
        maghribTime = (TextView) findViewById(R.id.textViewMaghribTime);
        ishaTime = (TextView) findViewById(R.id.textViewIshaTime);
        nextPrayerTextView = (TextView) findViewById(R.id.textViewNextPrayerName);
        settingsButton = (ImageView) findViewById(R.id.Setting);
        cityView = (TextView) findViewById(R.id.City);
        todayDate = (TextView) findViewById(R.id.Date);
        backgroudImg = (ImageView) findViewById(R.id.backgroundImg);
        rightArrow = (ImageView) findViewById(R.id.rightArrow);
        QiblaButton = (ImageView) findViewById(R.id.qiblah);
        Intent intent = new Intent(MainActivity.this, PrayNotification.class);
        intent.putExtra("NotificationID", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_MUTABLE);


//----------atkar view-----------
        AthkarTextView = findViewById(R.id.Athkar);
        handler = new Handler();
        startRepeatingTask();



        date = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        applyUserSettings();


        prayTime = new PrayTime();


        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {

            startLocationUpdates();
        }


        try {
            updatePrayers();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        setDate(todayDate);
        try {
            updatePrayers();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        QiblaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Qibla.class);
                startActivity(intent);
            }
        });


    }

    // Start listening for location updates
    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;
        minTime = 5000;
        minDistance = 5;
        if (PreferencesUtil.getInstance(MainActivity.this).getCityName().equals("")) {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) { //it is called automatically when the location is changed
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                city = getCityFromCoordinates(MainActivity.this, latitude, longitude);
                timezone = getTimeZone1();
                PreferencesUtil.getInstance(MainActivity.this).saveLocationData(latitude, longitude, timezone, city);

            }
        };
        if (provider != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener);
        }
    }}
    public double getTimeZone1() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = (timez.getRawOffset() / 1000.0) / 3600;
        return hoursDiff;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void applyUserSettings() {
        SharedPreferences prefs = getSharedPreferences("PrayerPrefs", MODE_PRIVATE);
        String method = prefs.getString("calculation_method", "Default Method");
        boolean isSilent = prefs.getBoolean("silent_mode", false);

        todayDate.setText("Calculation Method: " + method);
        if (isSilent) {
            scheduleSilentMode();
        }
    }

    private void updatePrayers() throws ParseException {
        prayTime.setCalcMethod(4); // choosing 4 which is um-alqura makkah calculation method

        rightArrow.setOnClickListener(this);

        latitude = PreferencesUtil.getInstance(MainActivity.this).getLatitude();


        longitude = PreferencesUtil.getInstance(MainActivity.this).getLongitude();


        timezone = PreferencesUtil.getInstance(MainActivity.this).getTimezone();

        city = PreferencesUtil.getInstance(MainActivity.this).getCityName();

        prayerTimes = prayTime.getPrayerTimes(calendar, latitude, longitude, timezone);




        cityView.setText(city);


        // Access individual prayer times from the ArrayList
        // retrieve fajr time at index 0
        String fajrTime12 = prayerTimes.get(0);
        if (fajrTime12.charAt(0) == '0') { // substring so that 04:40 becomes 4:40 while 11:50 remain 11:50
            fajrTime12 = fajrTime12.substring(1);
        }
        fajrTime.setText(fajrTime12);


        // retrieve sunrise time at index 1
        String sunRise12 = prayerTimes.get(1);
        if (sunRise12.charAt(0) == '0') { // substring so that 04:40 becomes 4:40 while 11:50 remain 11:50
            sunRise12 = sunRise12.substring(1);
        }
        sunriseTime.setText(sunRise12);

        // retrieve Dhuhr time at index 2
        String Dhuhr12 = prayerTimes.get(2);
        if (Dhuhr12.charAt(0) == '0') { // substring so that 04:40 becomes 4:40 while 11:50 remain 11:50
            Dhuhr12 = Dhuhr12.substring(1);
        }
        dhuhrTime.setText(Dhuhr12);

        // retrieve asr time at index 3
        String asr12 = prayerTimes.get(3);
        if (asr12.charAt(0) == '0') { // substring so that 04:40 becomes 4:40 while 11:50 remain 11:50
            asr12 = asr12.substring(1);
        }
        asrTime.setText(asr12);


        // retrieve maghrib time at index 5
        String maghribTime12 = prayerTimes.get(5);
        if (maghribTime12.charAt(0) == '0') { // substring so that 04:40 becomes 4:40 while 11:50 remain 11:50
            maghribTime12 = maghribTime12.substring(1);
        }
        maghribTime.setText(maghribTime12);


        // retrieve Isha time at index 6
        String ishaTime12 = prayerTimes.get(6);
        if (ishaTime12.charAt(0) == '0') { // substring so that 04:40 becomes 4:40 while 11:50 remain 11:50
            ishaTime12 = ishaTime12.substring(1);
        }
        ishaTime.setText(ishaTime12);


// Set the next prayer, sunrise, sunset
        int nearestNextPrayerIndex = 0;
        long nearestNextPrayerTimeDifference = Long.MAX_VALUE;
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTimeString = currentTimeFormat.format(currentTime.getTime());
        String[] prayerNames = {"Fajr", "Sunrise", "Dhuhr", "Asr", "Sunset", "Maghrib", "Isha"};
        for (int i = 0; i < prayerTimes.size(); i++) {
            // Get the prayer time at the current index
            String prayerTime = prayerTimes.get(i);

            SimpleDateFormat prayerTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            try {
                Date prayerDate = prayerTimeFormat.parse(prayerTime);
                String prayerTimeString = currentTimeFormat.format(prayerDate);

                long timeDifference = getTimeDifference(currentTimeString, prayerTimeString);

                if (timeDifference > 0 && timeDifference < nearestNextPrayerTimeDifference && i != 4) {
                    nearestNextPrayerIndex = i;
                    nearestNextPrayerTimeDifference = timeDifference;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        findViewById(R.id.fajrRelativeLayout).setBackground(null);
        findViewById(R.id.sunriseRelativeLayout).setBackground(null);
        findViewById(R.id.dhuhrRelativeLayout).setBackground(null);
        findViewById(R.id.asrRelativeLayout).setBackground(null);
        findViewById(R.id.maghribRelativeLayout).setBackground(null);
        findViewById(R.id.ishaRelativeLayout).setBackground(null);

// Display the nearest next prayer time
        if (nearestNextPrayerIndex != 0) {
            String nearestNextPrayerName = prayerNames[nearestNextPrayerIndex];
            String nearestNextPrayerTime = prayerTimes.get(nearestNextPrayerIndex);
            int value = 0;
            String[] parts = nearestNextPrayerTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1].substring(0, 2));
            String period = parts[1].substring(3);
            if (period.equalsIgnoreCase("am")) value = 0;
            if (period.equalsIgnoreCase("pm")) value = 1;
            setAlarm(hour, minutes, value);
            if (nearestNextPrayerIndex == 1) {
                nextPrayerTextView.setText("Next SunRise is at " + nearestNextPrayerTime);
                backgroudImg.setImageResource(R.drawable.noonimg);
                findViewById(R.id.sunriseRelativeLayout).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_layout));
            } else {
                nextPrayerTextView.setText("Next Prayer is " + nearestNextPrayerName + " at " + nearestNextPrayerTime);
                switch (nearestNextPrayerName) {
                    case "Dhuhr":
                        backgroudImg.setImageResource(R.drawable.noonimg);
                        findViewById(R.id.dhuhrRelativeLayout).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_layout));
                        curentPrayerMode = PreferencesUtil.getDhuhrSilentMode(this);
                        break;
                    case "Asr":
                        backgroudImg.setImageResource(R.drawable.afternoonimg);
                        findViewById(R.id.asrRelativeLayout).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_layout));
                        curentPrayerMode = PreferencesUtil.getAsrSilentMode(this);
                        break;
                    case "Maghrib":
                        backgroudImg.setImageResource(R.drawable.sunsetomg);
                        findViewById(R.id.maghribRelativeLayout).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_layout));
                        curentPrayerMode = PreferencesUtil.getMaghribSilentMode(this);
                        break;
                    case "Isha":
                        backgroudImg.setImageResource(R.drawable.nightimg);
                        findViewById(R.id.ishaRelativeLayout).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_layout));
                        curentPrayerMode = PreferencesUtil.getIshaSilentMode(this);
                        break;
                    default:
                        break;
                }
            }
        } else {
            nextPrayerTextView.setText("Next Prayer is " + prayerNames[0] + " at " + fajrTime12);
            backgroudImg.setImageResource(R.drawable.sunriseimg);
            findViewById(R.id.fajrRelativeLayout).setBackground(ContextCompat.getDrawable(this, R.drawable.selected_layout));
        } //end of setting next prayer

    }

    @Override
    public void onClick(View v) {
        if (v == (findViewById(R.id.rightArrow)))
            calendar.add(Calendar.DATE, 1);
        try {
            setDate(todayDate);
            updatePrayers();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (v.getTag() != null && v.getTag() == "public") {
            int current = v.getId();
            ImageView image = findViewById(current);
            image.setImageResource(R.drawable.noti);
            image.setTag("silent");
        } else if (v.getTag() != null && v.getTag() == "silent") {
            int current = v.getId();
            ImageView image = findViewById(current);
            image.setImageResource(R.drawable.notiloud);
            image.setTag("public");
        }
    }



    private void scheduleNotification(String prayerName, String prayerTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PrayNotification.class); // Use PrayNotification as the receiver class
        intent.putExtra("prayer_name", prayerName);
        intent.putExtra("prayer_time", prayerTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Convert prayer time to milliseconds
        // You may need to adjust this conversion based on your requirements
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date date = sdf.parse(prayerTime);
            long triggerTime = date.getTime();
            // Schedule the notification
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private long getTimeDifference(String currentTime, String prayerTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date current = sdf.parse(currentTime);
            Date prayer = sdf.parse(prayerTime);
            return (prayer.getTime() - current.getTime()) / (60 * 1000); // Difference in minutes
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.MAX_VALUE;
        }
    }

    private String getCityFromCoordinates(Context context, double latitude, double longitude) { //get the city name for the current location
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;


        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                city = addresses.get(0).getLocality(); // Get the city from the first address in the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return city;
    }

    public void setDate(TextView view) {

        Date today = calendar.getTime();//getting date
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");//formating according to my need
        String date = formatter.format(today);
        view.setText(date);
    }


    private void scheduleSilentMode() {
        SharedPreferences preferences = getSharedPreferences("PrayerPrefs", MODE_PRIVATE);
        boolean silentFajr = preferences.getBoolean("SilentFajr", false);
        boolean silentDhuhr = preferences.getBoolean("SilentDhuhr", false);
        // Retrieve prayer times (assuming they are already calculated and stored)

        if (silentFajr) {
            scheduleSilent(prayerTimes.get(0)); // Assuming this is the time for Fajr
        }
        if (silentDhuhr) {
            scheduleSilent(prayerTimes.get(2)); // Assuming this is the time for Dhuhr
        }
        // Schedule for other times
    }

    private void scheduleSilent(String prayerTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SilentMode.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            cal.setTime(sdf.parse(prayerTime));
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    Runnable updateTextRunnable = new Runnable() {
        @Override
        public void run() {
            updateText();
            handler.postDelayed(this, INTERVAL);
        }
    };

    void startRepeatingTask() {
        updateText();
        handler.postDelayed(updateTextRunnable, INTERVAL);
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(updateTextRunnable);
    }

    private void updateText() {
        AthkarTextView.setText(athkar[currentIndex]);
        currentIndex = (currentIndex + 1) % athkar.length; // Move to the next text, looping back to the beginning when reaching the end
    }

    private void setAlarm(int hour, int minute, int value) {


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.AM_PM, value);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        PendingIntent pendingIntent;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        if (curentPrayerMode)
            pendingIntent = PendingIntent.getBroadcast(this, -1, intent, PendingIntent.FLAG_MUTABLE);
        else
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        }

    }

}


