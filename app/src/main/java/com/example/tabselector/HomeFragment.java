package com.example.tabselector;


import android.animation.ValueAnimator;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final String CHANNEL_ID = "daily_reminder";
    private static final int NOTIFICATION_ID = 1;
    private ImageView cameraImageView;
    private ImageView arrowup;
    private ImageView arrowdown;
    private GridView gridView;
    private List<CapturedImage> capturedImages = new ArrayList<>(); // Danh sách ảnh chụp
    private ImageAdapter imageAdapter;
    private DatabaseHelper databaseHelper;
    private Button btnchon;
    private Button btnhuy;
    private Button btnxoa;
    private Button btnxoatc;
    private TextView filerTime;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {}

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        databaseHelper = new DatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cameraImageView = view.findViewById(R.id.camera);
        arrowup = view.findViewById(R.id.arrowup);
        arrowdown = view.findViewById(R.id.arrowdown);
        filerTime = view.findViewById(R.id.filerTime);
        gridView = view.findViewById(R.id.gv);
        imageAdapter = new ImageAdapter(requireContext(), capturedImages);
        gridView.setAdapter(imageAdapter);

        btnchon = view.findViewById(R.id.btnchon);
        btnhuy = view.findViewById(R.id.btnhuy);
        btnxoa = view.findViewById(R.id.btnxoa);
        btnxoatc = view.findViewById(R.id.btnxoatc);

        // Tải ảnh từ cơ sở dữ liệu khi mở Fragment
        loadCapturedImagesFromDatabase();

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (capturedImages.isEmpty() || firstVisibleItem >= capturedImages.size()) return;

                // Lấy ngày tháng của ảnh đầu tiên trong phần hiển thị
                CapturedImage firstVisibleImage = capturedImages.get(firstVisibleItem);
                SimpleDateFormat dateFormat = new SimpleDateFormat("d 'thg' M, yyyy", Locale.getDefault());
                String dateText = dateFormat.format(firstVisibleImage.getCreatedAt()); // Format ngày tháng
                filerTime.setText(dateText); // Hiển thị ngày tháng trên TextView
            }
        });

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        int originalPaddingTop = gridView.getPaddingTop(); // Save original paddingTop

        arrowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraImageView.setVisibility(View.VISIBLE);
                btnchon.setVisibility(View.VISIBLE);
                btnhuy.setVisibility(View.GONE);
                btnxoa.setVisibility(View.VISIBLE);
                btnxoatc.setVisibility(View.VISIBLE);

                ValueAnimator animator = ValueAnimator.ofInt(0, 135);
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int paddingValue = (int) valueAnimator.getAnimatedValue();
                        gridView.setPadding(0, originalPaddingTop, 0, paddingValue); // Keep paddingTop constant
                    }
                });
                animator.start();

                arrowup.setVisibility(View.GONE);
                arrowdown.setVisibility(View.VISIBLE);
            }
        });

        arrowdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraImageView.setVisibility(View.GONE);
                arrowdown.setVisibility(View.GONE);
                btnchon.setVisibility(View.GONE);
                btnhuy.setVisibility(View.GONE);
                btnxoa.setVisibility(View.GONE);
                btnxoatc.setVisibility(View.GONE);

                ValueAnimator animator = ValueAnimator.ofInt(135, 0);
                animator.setDuration(300);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int paddingValue = (int) valueAnimator.getAnimatedValue();
                        gridView.setPadding(0, originalPaddingTop, 0, paddingValue); // Keep paddingTop constant
                    }
                });
                animator.start();
                arrowup.setVisibility(View.VISIBLE);
            }
        });


        btnchon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Bật chế độ chọn hình ảnh
                imageAdapter.setSelectionEnabled(true);
                btnhuy.setVisibility(View.VISIBLE);
            }
        });

        btnhuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hủy chế độ chọn hình ảnh
                imageAdapter.setSelectionEnabled(false);
                imageAdapter.clearSelection(); // Xóa tất cả lựa chọn
                btnhuy.setVisibility(View.GONE);
            }
        });

        btnxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy danh sách ID của các hình ảnh đã chọn
                List<Long> selectedImageIds = imageAdapter.getSelectedImageIds();

                new AlertDialog.Builder(view.getContext())
                        .setTitle("Xác Nhận Xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa các ảnh đã chọn?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Gọi phương thức xóa các hình ảnh đã chọn
                                databaseHelper.deleteCapturedImages(selectedImageIds);
                                //Bỏ lựa chon
                                imageAdapter.setSelectionEnabled(false);
                                imageAdapter.clearSelection(); // Xóa tất cả lựa chọn
                                btnhuy.setVisibility(View.GONE);
                                // Cập nhật giao diện hoặc làm mới danh sách sau khi xóa
                                loadCapturedImagesFromDatabase(); // Tải lại ảnh từ cơ sở dữ liệu
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Đóng hộp thoại khi nhấn "Không"
                            }
                        })
                        .show(); // Hiển thị AlertDialog

            }
        });

        btnxoatc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo AlertDialog để xác nhận
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Xác Nhận Xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa tất cả hình ảnh?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Gọi phương thức xóa tất cả hình ảnh
                                databaseHelper.deleteAllCapturedImages();
                                btnhuy.setVisibility(View.GONE);
                                // Cập nhật giao diện hoặc làm mới danh sách sau khi xóa
                                loadCapturedImagesFromDatabase(); // Tải lại ảnh từ cơ sở dữ liệu
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Đóng hộp thoại khi nhấn "Không"
                            }
                        })
                        .show(); // Hiển thị AlertDialog
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Chuyển đổi ảnh Bitmap sang mảng byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageData = outputStream.toByteArray();

            // Lưu ảnh vào cơ sở dữ liệu
            CapturedImage capturedImage = new CapturedImage(imageData, new Date());
            databaseHelper.addCapturedImage(capturedImage);

            // Tải lại ảnh từ cơ sở dữ liệu và cập nhật GridView
            loadCapturedImagesFromDatabase();


        }
    }

    private void loadCapturedImagesFromDatabase() {
        capturedImages.clear(); // Xóa ảnh cũ khỏi danh sách
        List<CapturedImage> imagesFromDb = databaseHelper.getAllCapturedImages();

        capturedImages.addAll(imagesFromDb);

        // Thông báo Adapter rằng dữ liệu đã thay đổi
        imageAdapter.notifyDataSetChanged();
    }

}