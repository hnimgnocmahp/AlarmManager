package com.example.tabselector;

import java.util.Date;

public class CapturedImage {
    private long id;            // ID duy nhất cho hình ảnh
    private byte[] imageData;   // Dữ liệu ảnh nhị phân
    private Date createdAt;      // Ngày tạo
    private boolean isSelected;   // Trạng thái lựa chọn (để hỗ trợ checkbox)

    // Constructor
    public CapturedImage(byte[] imageData, Date createdAt) {         // Khởi tạo ID
        this.imageData = imageData;
        this.createdAt = createdAt;
        this.isSelected = false;  // Mặc định không được chọn
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    // Getter cho ID
    public long getId() {
        return id;
    }

    // Getter cho dữ liệu ảnh
    public byte[] getImageData() {
        return imageData;
    }

    // Getter cho ngày tạo
    public Date getCreatedAt() {
        return createdAt;
    }

    // Getter và setter cho trạng thái lựa chọn
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "CapturedImage{" +
                "id=" + id +
                '}';
    }
}
