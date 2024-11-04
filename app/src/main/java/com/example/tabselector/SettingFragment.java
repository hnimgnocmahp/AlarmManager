package com.example.tabselector;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getContextForLanguage;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class SettingFragment extends Fragment {

    private TimePicker timePicker;
    private Button btnLuuThoiGian;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static final int REQUEST_EXACT_SCHEDULE = 2;
    private static final int REQUEST_USE_SCHEDULE = 3;
    private boolean exact = true;

    public SettingFragment() {}

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        btnLuuThoiGian = view.findViewById(R.id.btnLuuThoiGian);


        // Đặt sự kiện cho nút lưu
        btnLuuThoiGian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.POST_NOTIFICATIONS")
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.POST_NOTIFICATIONS"}, 1001);
                        return; // Exit the method until permission is granted
                    }
                }
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Set the calendar to the selected time
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                // If the selected time is before the current time, schedule for the next day
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                // Create an Intent and PendingIntent for the alarm
                Intent intent = new Intent(getActivity(), DailyReminderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getActivity(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                try {
                    // Set up the AlarmManager and schedule the alarm
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                    if (alarmManager != null) {
                        // Use setExact() for precise timing
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Toast.makeText(getActivity(), "Thời gian được đặt: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Yêu cầu cung cấp quyền thông báoư", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void resetPhotoTakenStatus(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("DailyPhotoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("photoTakenToday", false);
        editor.apply();
    }


}