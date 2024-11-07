package com.example.tabselector;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    /*Đồ án đầy đủ tính năng như thầy giao:
    Giao diện được chia làm 2 phần: Trang chủ và Cài đặt
    Trang chủ
        Cho phép chụp nhiều ảnh mỗi ngày.
        Luu trữ ảnh, xem chi tiết ảnh, xóa đơn, xóa nhiều, xóa tất cả.
        Hiển thị ngày của các ảnh (trong khung gridview) nằm phía trên gridview.
        Thanh menu có thể ẩn và hiện lại
    Cài đặt
        Hẹn giờ thông báo chụp ảnh. Nếu đã chụp không thông báo
     */
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                triggerNotification(); // Gọi hàm gửi Broadcast khi đã có quyền
            }
        } else {
            triggerNotification(); // Gọi hàm gửi Broadcast cho API thấp hơn
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                triggerNotification(); // Gọi hàm gửi Broadcast khi người dùng cấp quyền
            } else {
                Toast.makeText(this, "Quyền thông báo bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void triggerNotification() {
        Intent intent = new Intent(this, DailyReminderReceiver.class);
        sendBroadcast(intent);
    }



}