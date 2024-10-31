package com.example.tabselector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ImageViewerActivity extends AppCompatActivity {
    TextView timeTextView;
    TextView dateTextView;
    ImageView imageView;
    ImageView exitTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        timeTextView = findViewById(R.id.timeTextView);

        byte[] imageData = getIntent().getByteArrayExtra("image_data");

        if (imageData != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1; // Giữ kích thước gốc
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
            imageView.setImageBitmap(bitmap);
        }

        // Nhận dữ liệu ngày giờ
        String captureTime = getIntent().getStringExtra("capture_time");

        // Tách ngày và giờ
        String[] dateTimeParts = captureTime.split(" ");
        String date = dateTimeParts[0];  // Ngày
        String time = dateTimeParts[1];  // Giờ

        // Thiết lập các TextView
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView.setText(date);
        timeTextView.setText(time);

        exitTextView = findViewById(R.id.exitTextView);
        exitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}