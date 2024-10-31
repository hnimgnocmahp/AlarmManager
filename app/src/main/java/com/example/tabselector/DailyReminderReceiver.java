package com.example.tabselector;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DailyReminderReceiver extends BroadcastReceiver {
    private final String CHANNEL_ID = "reminder_channel";

    private final int NOTI_ID = 99;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        // Tạo Intent để mở ứng dụng khi người dùng nhấn vào thông báo
        Intent openapp = new Intent(context, MainActivity.class); // Mở MainActivity hoặc Activity khác
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openapp, PendingIntent.FLAG_IMMUTABLE);

        // Tạo và cấu hình thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                //                .setSmallIcon(R.drawable.ic_notification) // Thay bằng icon của bạn
                .setContentTitle("Nhắc nhở chụp ảnh")
                .setContentText("Bạn chưa chụp ảnh nào hôm nay, hãy chụp ảnh ngay!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Gửi thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (notificationManager != null) {
            notificationManager.notify(NOTI_ID, builder.build());
        }

    }

    private void createNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nhắc nhở chụp ảnh hàng ngày";
            String desc = "Kênh nhắc nhở chụp ảnh hàng ngày";
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(desc);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
