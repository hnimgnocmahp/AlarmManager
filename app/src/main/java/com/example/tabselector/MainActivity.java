package com.example.tabselector;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppPreferences";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private boolean exact = true;
    private TabLayout myTab;
    private ViewPager2 myViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        myTab = findViewById(R.id.myTab);
        myViewPager = findViewById(R.id.myViewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MainActivity.this);
        myViewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(myTab, myViewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Trang chủ");
                    } else if (position == 1) {
                        tab.setText("Cài đặt");
                    }
                }
        ).attach();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
//        scheduleDailyReminder();
    }

//    @SuppressLint("ScheduleExactAlarm")
//    private void scheduleDailyReminder() {
//        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(PREFS_NAME, 0);
//        int hour = sharedPreferences.getInt(KEY_HOUR, 8);
//        int minute = sharedPreferences.getInt(KEY_MINUTE, 0);
//
//        try {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, minute);
//            calendar.set(Calendar.SECOND, 0);
//
//            if (calendar.before(Calendar.getInstance())) {
//                calendar.add(Calendar.DATE, 1); // Nếu thời gian đã qua, đặt lịch cho ngày hôm sau
//            }
//
//            Log.d("AlarmManager", "Scheduled time: " + calendar.getTimeInMillis());
//
//            Intent intent = new Intent(MainActivity.this, DailyReminderReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                    MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//            );
//
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            if (alarmManager != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && exact) {
//
//                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                } else {
//                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//                }
//                Log.d("AlarmManager", "Alarm set successfully");
//            }
//        } catch (Exception e) {
//            Log.d("AlarmManagerError", "Error scheduling alarm: " + e.getMessage(), e);
//        }
//    }

}