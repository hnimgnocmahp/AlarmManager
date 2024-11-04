package com.example.tabselector;


import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class DailyReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_selfie_reminder_channel";
    private static final int NOTIFICATION_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        // Tạo thông báo
        SharedPreferences preferences = context.getSharedPreferences("DailyPhotoPrefs", Context.MODE_PRIVATE);
        boolean photoTakenToday = preferences.getBoolean("photoTakenToday", false);

        if (!photoTakenToday) {
            // Gửi thông báo vì ảnh chưa được chụp hôm nay
            createNotificationChannel(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_menu_camera)
                    .setContentTitle("Nhắc nhở hàng ngày")
                    .setContentText("Đã đến lúc chụp ảnh selfie!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("photoTakenToday", false);
        editor.apply();
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Reminder";
            String description = "Channel for daily selfie reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}
