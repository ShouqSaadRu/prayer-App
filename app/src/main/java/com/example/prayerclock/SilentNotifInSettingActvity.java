package com.example.prayerclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SilentNotifInSettingActvity extends AppCompatActivity {
    private Switch switchFajr, switchDhuhr, switchAsr, switchMaghrib, switchIsha;
    private Button btnSave, btn_notif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.silent_notif_in_settingactivity);
        initializeUI();
btnSave= findViewById(R.id.btn_save_settings);

        btnSave.setOnClickListener(v -> saveSettings());

    }

    private void initializeUI() {

        switchFajr = findViewById(R.id.switch_fajr);
        switchDhuhr = findViewById(R.id.switch_dhuhr);
        switchAsr = findViewById(R.id.switch_asr);
        switchMaghrib = findViewById(R.id.switch_maghrib);
        switchIsha = findViewById(R.id.switch_isha);

        boolean fajrSilentModeEnabled = PreferencesUtil.getFajrSilentMode(this);
        switchFajr.setChecked(fajrSilentModeEnabled);
        boolean dohrSilentModeEnabled = PreferencesUtil.getDhuhrSilentMode(this);
        switchDhuhr.setChecked(dohrSilentModeEnabled);
        boolean asrSilentModeEnabled = PreferencesUtil.getAsrSilentMode(this);
        switchAsr.setChecked(asrSilentModeEnabled);
        boolean maghripSilentModeEnabled = PreferencesUtil.getMaghribSilentMode(this);
        switchMaghrib.setChecked(maghripSilentModeEnabled);
        boolean ishaSilentModeEnabled = PreferencesUtil.getIshaSilentMode(this);
        switchIsha.setChecked(ishaSilentModeEnabled);

    }
    private void saveSettings() {

        PreferencesUtil.saveFajrSilentMode(this, switchFajr.isChecked());
        PreferencesUtil.saveDhuhrSilentMode(this, switchDhuhr.isChecked());
        PreferencesUtil.saveAsrSilentMode(this, switchAsr.isChecked());
        PreferencesUtil.saveMaghribSilentMode(this, switchMaghrib.isChecked());
        PreferencesUtil.saveIshaSilentMode(this, switchIsha.isChecked());

        Toast.makeText(SilentNotifInSettingActvity.this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SilentNotifInSettingActvity.this, MainActivity.class);
        startActivityForResult(intent, 1001);
        finish();
    }
}
