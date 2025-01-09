package com.example.prayerclock;
import android.content.Context;
import android.content.SharedPreferences;
public class PreferencesUtil {

    private static final String PREFERENCES_FILE_KEY = "com.example.prayerclock.preferences";
    private static final String SELECTED_CITY_KEY = "selected_city";
    private static final String CALCULATION_METHOD_KEY = "calculation_method";
    private static final String SILENT_MODE_KEY = "silent_mode";
    // Keys for individual prayer silent mode settings
    private static final String KEY_FAJR_SILENT_MODE = "fajr_silent_mode";
    private static final String KEY_DHUHR_SILENT_MODE = "dhuhr_silent_mode";
    private static final String KEY_ASR_SILENT_MODE = "asr_silent_mode";
    private static final String KEY_MAGHRIB_SILENT_MODE = "maghrib_silent_mode";
    private static final String KEY_ISHA_SILENT_MODE = "isha_silent_mode";




//Shouq's
    private static final String PREF_NAME = "LocationPrefs";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String TIMEZONE_KEY = "timezone";
    private static final String CITY_NAME_KEY = "city_name";

    private static PreferencesUtil instance;
    private SharedPreferences sharedPreferences;


    //Shouq's

    private PreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesUtil getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesUtil(context.getApplicationContext());
        }
        return instance;
    }

    public void saveLocationData(double latitude, double longitude, double timezone, String cityName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LATITUDE_KEY, (float) latitude);
        editor.putFloat(LONGITUDE_KEY, (float) longitude);
        editor.putFloat(TIMEZONE_KEY, (float) timezone);
        editor.putString(CITY_NAME_KEY, cityName);
        editor.apply();
    }

    public double getLatitude() {
        return sharedPreferences.getFloat(LATITUDE_KEY, 0);
    }

    public double getLongitude() {
        return sharedPreferences.getFloat(LONGITUDE_KEY, 0);
    }

    public double getTimezone() {
        return sharedPreferences.getFloat(TIMEZONE_KEY, 0);
    }

    public String getCityName() {
        return sharedPreferences.getString(CITY_NAME_KEY, "");
    }









    // Save preferences
    public static void savePreferences(Context context, String city, String method, boolean isSilentMode) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SELECTED_CITY_KEY, city);
        editor.putString(CALCULATION_METHOD_KEY, method);
        editor.putBoolean(SILENT_MODE_KEY, isSilentMode);
        editor.apply();
    }

    // Retrieve city setting
    public static String getSelectedCity(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getString(SELECTED_CITY_KEY, "Default City");
    }

    // Retrieve calculation method setting
    public static String getCalculationMethod(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getString(CALCULATION_METHOD_KEY, "Default Method");
    }

    // Retrieve silent mode setting
    public static boolean isSilentModeEnabled(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(SILENT_MODE_KEY, false);
    }
    public static void saveFajrSilentMode(Context context, boolean enabled) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(KEY_FAJR_SILENT_MODE, enabled).apply();
    }

    public static boolean getFajrSilentMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_FAJR_SILENT_MODE, false);
    }

    public static void saveDhuhrSilentMode(Context context, boolean enabled) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(KEY_DHUHR_SILENT_MODE, enabled).apply();
    }

    public static boolean getDhuhrSilentMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_DHUHR_SILENT_MODE, false);
    }

    public static void saveAsrSilentMode(Context context, boolean enabled) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(KEY_ASR_SILENT_MODE, enabled).apply();
    }

    public static boolean getAsrSilentMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_ASR_SILENT_MODE, false);
    }

    public static void saveMaghribSilentMode(Context context, boolean enabled) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(KEY_MAGHRIB_SILENT_MODE, enabled).apply();
    }

    public static boolean getMaghribSilentMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_MAGHRIB_SILENT_MODE, false);
    }

    public static void saveIshaSilentMode(Context context, boolean enabled) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(KEY_ISHA_SILENT_MODE, enabled).apply();
    }

    public static boolean getIshaSilentMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(KEY_ISHA_SILENT_MODE, false);
    }



}



