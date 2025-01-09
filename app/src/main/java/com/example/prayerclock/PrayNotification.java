package com.example.prayerclock;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PrayNotification extends BroadcastReceiver {

    private Context context;
    private static final String CHANNEL_ID = "prayer_channel_id";
    private static final String CHANNEL_NAME = "Prayer Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "Notification channel for prayer times";
    private static final String ACTION_CONFIRM_PRAYER = "com.example.prayerclock.ACTION_CONFIRM_PRAYER";
    private static final String ACTION_IGNORE_PRAYER = "com.example.prayerclock.ACTION_IGNORE_PRAYER";
    private static final int NOTIFICATION_ID = 1;
    private static final int ATHAN_NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        startPrayerTimeNotifications();
    }

    public void startPrayerTimeNotifications() {
        createNotificationChannel(context);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkPrayerTimes();
            }
        }, 0, 60000); // Check every minute, adjust as needed
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkPrayerTimes() {
        Calendar currentTime = Calendar.getInstance();
        List<PrayerTime> prayerTimes = getPrayerTimesForToday(); // Implement this method to get prayer times for today

        for (PrayerTime prayerTime : prayerTimes) {
            if (currentTime.after(prayerTime.getTime())) {
                sendNotification(prayerTime.getName(), formatPrayerTime(prayerTime.getTime()));
            }
        }
    }

    private void sendNotification(String prayerName, String prayerTime) {
        // Sound for notification
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Load Athan sound
        Uri athanSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.athan);

        // Intent for confirming the prayer
        Intent confirmIntent = new Intent(context, PrayerActionReceiver.class);
        confirmIntent.setAction(ACTION_CONFIRM_PRAYER);
        confirmIntent.putExtra("prayerName", prayerName);
        PendingIntent confirmPendingIntent = PendingIntent.getBroadcast(context, 0, confirmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent for dismissing the prayer
        Intent ignoreIntent = new Intent(context, PrayerActionReceiver.class);
        ignoreIntent.setAction(ACTION_IGNORE_PRAYER);
        ignoreIntent.putExtra("prayerName", prayerName);
        PendingIntent ignorePendingIntent = PendingIntent.getBroadcast(context, 1, ignoreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_prayer)
                .setContentTitle("Prayer Time Reminder")
                .setContentText("It's time for " + prayerName + " at " + prayerTime)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_confirm, "Confirm", confirmPendingIntent)
                .addAction(R.drawable.ic_ignore, "Ignore", ignorePendingIntent);

        // Add Athan sound to the notification
        builder.setSound(athanSoundUri);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check for permission to show notifications
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, handle the scenario (e.g., request permission from the user)
            return;
        }

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private String formatPrayerTime(Calendar prayerTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(prayerTime.getTime());
    }

    // Dummy implementation, replace with your actual logic
    private List<PrayerTime> getPrayerTimesForToday() {
        // Implement logic to fetch prayer times for today
        return null;
    }

    // Dummy implementation, replace with your actual PrayerTime class
    private static class PrayerTime {
        private String name;
        private Calendar time;

        public String getName() {
            return name;
        }

        public Calendar getTime() {
            return time;
        }
    }

    public static class PrayerActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String prayerName = intent.getStringExtra("prayerName");

            if (ACTION_CONFIRM_PRAYER.equals(action)) {
                // Handle confirmation action
            } else if (ACTION_IGNORE_PRAYER.equals(action)) {
                // Handle ignore action
            }
        }
    }
}
