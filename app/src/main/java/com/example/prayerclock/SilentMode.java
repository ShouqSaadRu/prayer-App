package com.example.prayerclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.widget.Toast;

public class SilentMode {

    public static class SilentService extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            SharedPreferences prefs = context.getSharedPreferences("PrayerPrefs", Context.MODE_PRIVATE);

            if ("SILENCE".equals(intent.getAction())) {
                int currentRingerMode = audioManager.getRingerMode();
                prefs.edit().putInt("PreviousRingerMode", currentRingerMode).apply();

                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(context, "The phone is switched to silent mode.", Toast.LENGTH_SHORT).show();

                // Schedule the restore after the specified duration
                scheduleRestore(context, intent.getIntExtra("duration", 0));
            } else if ("RESTORE".equals(intent.getAction())) {
                int previousRingerMode = prefs.getInt("PreviousRingerMode", AudioManager.RINGER_MODE_NORMAL);
                audioManager.setRingerMode(previousRingerMode);
                Toast.makeText(context, "The phone has been restored to its previous state.", Toast.LENGTH_SHORT).show();
            }
        }

        private void scheduleRestore(Context context, int durationMinutes) {
            Intent restoreIntent = new Intent(context, SilentService.class);
            restoreIntent.setAction("RESTORE");
            PendingIntent restorePendingIntent = PendingIntent.getBroadcast(context, 1, restoreIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            long restoreTimeMillis = System.currentTimeMillis() + durationMinutes * 60 * 1000;
            alarmManager.set(AlarmManager.RTC_WAKEUP, restoreTimeMillis, restorePendingIntent);
        }
    }

    public static void silencePhone(Context context, int durationInMinutes) {
        Intent intent = new Intent(context, SilentService.class);
        intent.setAction("SILENCE");
        intent.putExtra("duration", durationInMinutes);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long targetTimeMillis = System.currentTimeMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetTimeMillis, pendingIntent);
        Toast.makeText(context, "Phone will be silent for " + durationInMinutes + " minute(s)", Toast.LENGTH_SHORT).show();
    }
}
