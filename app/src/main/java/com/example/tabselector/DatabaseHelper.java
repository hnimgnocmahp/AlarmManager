package com.example.tabselector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "selfieApp.db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    private static final String TABLE_IMAGES = "captured_images";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_DATA = "image_data";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_IS_SELECTED = "is_selected";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_IMAGES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_IMAGE_DATA + " BLOB, "
                + COLUMN_CREATED_AT + " INTEGER, "
                + COLUMN_IS_SELECTED + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    // Phương thức thêm ảnh
    // Phương thức thêm ảnh
    public void addCapturedImage(CapturedImage capturedImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_DATA, capturedImage.getImageData());
        values.put(COLUMN_CREATED_AT, capturedImage.getCreatedAt().getTime());
        values.put(COLUMN_IS_SELECTED, capturedImage.isSelected() ? 1 : 0);

        long id = db.insert(TABLE_IMAGES, null, values);
        if (id == -1) {
            Log.e("Database Error", "Failed to insert image");
        } else {
            capturedImage.setId(id); // Gán ID cho đối tượng CapturedImage
        }
        db.close();
    }

    public List<CapturedImage> getAllCapturedImages() {
        List<CapturedImage> imagesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_IMAGES, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)); // Lấy ID
                byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_DATA));
                long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
                boolean isSelected = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SELECTED)) == 1;

                CapturedImage capturedImage = new CapturedImage(imageData, new Date(createdAtMillis));
                capturedImage.setId(id); // Đặt ID vào đối tượng CapturedImage
                capturedImage.setSelected(isSelected);
                imagesList.add(capturedImage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return imagesList;
    }

    public List<CapturedImage> getCapturedImageWithTime(int hour, int minute) {
        List<CapturedImage> imagesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar currentTime = Calendar.getInstance();
        Calendar startOfDay = Calendar.getInstance();
        Calendar endOfTime = Calendar.getInstance();

        // Đặt thời điểm kết thúc với giờ và phút được truyền vào
        endOfTime.set(Calendar.HOUR_OF_DAY, hour);
        endOfTime.set(Calendar.MINUTE, minute);
        endOfTime.set(Calendar.SECOND, 59);
        endOfTime.set(Calendar.MILLISECOND, 999);

        // Kiểm tra nếu giờ-phút đặt nhỏ hơn thời gian hiện tại
        if (endOfTime.before(currentTime)) {
            // Đặt thời gian bắt đầu là 0 giờ của ngày tiếp theo
            startOfDay.add(Calendar.DAY_OF_YEAR, 1);
            startOfDay.set(Calendar.HOUR_OF_DAY, 0);
            startOfDay.set(Calendar.MINUTE, 0);
            startOfDay.set(Calendar.SECOND, 0);
            startOfDay.set(Calendar.MILLISECOND, 0);
        } else {
            // Đặt thời gian bắt đầu là 0 giờ của ngày hiện tại
            startOfDay.set(Calendar.HOUR_OF_DAY, 0);
            startOfDay.set(Calendar.MINUTE, 0);
            startOfDay.set(Calendar.SECOND, 0);
            startOfDay.set(Calendar.MILLISECOND, 0);
        }

        long startTime = startOfDay.getTimeInMillis();
        long endTime = endOfTime.getTimeInMillis();

        // Truy vấn cơ sở dữ liệu để lấy ảnh trong khoảng từ thời gian bắt đầu đến thời gian kết thúc
        String selection = COLUMN_CREATED_AT + " >= ? AND " + COLUMN_CREATED_AT + " <= ?";
        String[] selectionArgs = { String.valueOf(startTime), String.valueOf(endTime) };

        Cursor cursor = db.query(TABLE_IMAGES, null, selection, selectionArgs, null, null, COLUMN_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_DATA));
                long createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
                boolean isSelected = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SELECTED)) == 1;

                CapturedImage capturedImage = new CapturedImage(imageData, new Date(createdAtMillis));
                capturedImage.setId(id);
                capturedImage.setSelected(isSelected);
                imagesList.add(capturedImage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return imagesList;
    }

    // Phương thức xóa hình ảnh theo danh sách ID
    public void deleteCapturedImages(List<Long> ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (long id : ids) {
            db.delete(TABLE_IMAGES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        }
        db.close();
    }

    public void deleteAllCapturedImages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGES, null, null); // Xóa tất cả các bản ghi
        db.close();
    }
}
