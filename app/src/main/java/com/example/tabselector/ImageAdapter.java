package com.example.tabselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<CapturedImage> images;
    private Set<Integer> selectedPositions = new HashSet<>();
    private boolean selectionEnabled = false;


    public ImageAdapter(Context context, List<CapturedImage> images) {
        this.context = context;
        this.images = images;
    }



    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy ảnh từ CapturedImage và hiển thị trên ImageView
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(images.get(position).getImageData(), 0, images.get(position).getImageData().length);
        holder.imageView.setImageBitmap(imageBitmap);

        // Cập nhật checkbox theo trạng thái lựa chọn
        boolean isSelected = selectedPositions.contains(position);
        holder.checkBox.setChecked(isSelected);
        images.get(position).setSelected(isSelected); // Cập nhật trạng thái lựa chọn trong CapturedImage

        holder.checkBox.setVisibility(selectionEnabled ? View.VISIBLE : View.GONE);

        // Xử lý sự kiện click vào hình ảnh
        holder.imageView.setOnClickListener(view -> {
            if (selectionEnabled) {
                boolean isChecked = !holder.checkBox.isChecked();
                holder.checkBox.setChecked(isChecked);
                if (isChecked) {
                    selectedPositions.add(position);
                } else {
                    selectedPositions.remove(position);
                }
                images.get(position).setSelected(isChecked); // Cập nhật trạng thái trong CapturedImage
            } else {
                // Mở ImageViewerActivity
                openImageViewerActivity(images.get(position));
            }
        });

        return convertView;
    }

    private void openImageViewerActivity(CapturedImage capturedImage) {
        byte[] imageData = capturedImage.getImageData();
        Bitmap clickedImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

        Intent intent = new Intent(context, ImageViewerActivity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        clickedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("image_data", byteArray);

        // Truyền thời gian vào Intent
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedCaptureTime = sdf.format(capturedImage.getCreatedAt());
        intent.putExtra("capture_time", formattedCaptureTime);

        context.startActivity(intent);
    }


    // Phương thức để lấy danh sách ID của các hình ảnh đã chọn
    public List<Long> getSelectedImageIds() {
        List<Long> selectedIds = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            if (selectedPositions.contains(i)) { // Kiểm tra nếu vị trí được chọn
                selectedIds.add(images.get(i).getId()); // Lấy ID
            }
        }
        return selectedIds;
    }

    public boolean hasSelectedImages() {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).isSelected()) {
                return true; // Nếu có ít nhất một hình ảnh được chọn, trả về true
            }
        }
        return false; // Không có hình ảnh nào được chọn
    }

    private static class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;

        // Constructor cho ViewHolder nhận View làm tham số
        ViewHolder(View itemView) {
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public void setSelectionEnabled(boolean enabled) {
        this.selectionEnabled = enabled;
        notifyDataSetChanged(); // Cập nhật giao diện để phản ánh trạng thái mới
    }

    public void clearSelection() {
        selectedPositions.clear();
        notifyDataSetChanged(); // Cập nhật giao diện để bỏ chọn tất cả
    }

}
