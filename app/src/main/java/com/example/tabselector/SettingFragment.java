package com.example.tabselector;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class SettingFragment extends Fragment {

    private TimePicker timePicker;
    private Button btnLuuThoiGian;
    private boolean exact = true;
    private static final String PREFS_NAME = "AppPreferences";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

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

                saveTime();
                scheduleDailyReminder();
            }
        });

        return view;
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), DailyReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Set the alarm to repeat every day at 9 AM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && exact == true) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void saveTime() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Lưu thời gian vào SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HOUR, hour);
        editor.putInt(KEY_MINUTE, minute);
        editor.apply();

        // Hiển thị thông báo
        Toast.makeText(getContext(), "Đã lưu thời gian hẹn giờ!", Toast.LENGTH_SHORT).show();
    }
}